package uz.platform.forestyapp.service.impls;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.platform.forestyapp.entity.Client;
import uz.platform.forestyapp.entity.Order;
import uz.platform.forestyapp.entity.OrderTransaction;
import uz.platform.forestyapp.entity.Payment;
import uz.platform.forestyapp.entity.enums.TransactionState;
import uz.platform.forestyapp.payload.json.*;
import uz.platform.forestyapp.repository.ClientRepo;
import uz.platform.forestyapp.repository.OrderRepo;
import uz.platform.forestyapp.repository.OrderTransactionRepo;
import uz.platform.forestyapp.repository.PaymentRepo;
import uz.platform.forestyapp.service.PaycomService;

import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class PaycomServiceImpl implements PaycomService {
    @Autowired
    ClientRepo clientRepo;

    @Autowired
    OrderRepo orderRepo;

    @Autowired
    PaymentRepo paymentRepo;

    @Autowired
    OrderTransactionRepo orderTransactionRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    private final Long TIME_EXPIRED_PAYCOM_ORDER = 43_200_000L;

    @Override
    public JSONObject payWithPaycom(PaycomRequestForm requestForm, String auth) {
        Params params = requestForm.getParams();

        JSONRPC2Response response = new JSONRPC2Response(params.getId());

        //BASIC AUTH BO'SH BO'LSA YOKI XATO KELGAN BO'LSA ERROR RESPONSE BERAMIZ
        if (auth == null || checkPaycomUserAuth(auth, response)) {
            return response.toJSONObject();
        }

        //PAYCOM QAYSI METHODDA KELAYOTGANLIGIGA QARAB ISH BAJARAMIZ
        switch (requestForm.getMethod()) {
            case "CheckPerformTransaction":
                checkPerformTransaction(requestForm, response);
                break;
            case "CreateTransaction":
                createTransaction(requestForm, response);
                break;
            case "PerformTransaction":
                performTransaction(requestForm, response);
                break;
            case "CancelTransaction":
                cancelTransaction(requestForm, response);
                break;
            case "CheckTransaction":
                checkTransaction(requestForm, response);
                break;
            case "GetStatement":
                getStatement(requestForm, response);
                break;
        }

        return response.toJSONObject();
    }



    /**
     * ORDER HAMMA JIHATDAN TO'G'RILIGINI TEKSHIRAMIZ
     * https://developer.help.paycom.uz/ru/metody-merchant-api/checkperformtransaction
     *
     * @param requestForm @RequestBody
     * @param response    JSONRPC2Response
     * @return boolena
     */
    public boolean checkPerformTransaction(PaycomRequestForm requestForm, JSONRPC2Response response) {

        //PAYCOMDAN ACOUNT FIELDI KELMASA
        if (requestForm.getParams().getAccount() == null) {
            response.setError(new JSONRPC2Error(
                    -31050,
                    "Account field not found",
                    "account"
            ));
            return false;
        }

        //PAYCOMDAN ACOUNT FIELDI KELMASA
        if (requestForm.getParams().getAccount().getOrder() == null) {
            response.setError(new JSONRPC2Error(
                    -31050,
                    "Order not found",
                    "order"
            ));
            return false;
        }

        //PAYCOMDAN AMOUNT FIELDI NULL YOKI 0 KELSA
        if (requestForm.getParams().getAmount() == null || requestForm.getParams().getAmount() == 0) {
            response.setError(new JSONRPC2Error(
                    -31001,
                    "Amount error or null",
                    "amount"));
            return false;
        }

        //PAYCOM DAN KELGAN ORDER ID ORQALI ORDERNI OLAMIZ
        Optional<Order> optionalOrder = orderRepo.findById(requestForm.getParams().getAccount().getOrder());

        //AGAR ORDER BO'LSA
        if (optionalOrder.isEmpty()) {
            response.setError(new JSONRPC2Error(
                    -31050,
                    "Order not found",
                    "order"));
            return false;
        }

        //ORDER SUM BILAN PAYCOMDAN KELGAN SUM TENGLIGI TEKSHIRILYAPTI
        Order order = optionalOrder.get();
        if (!order.getOrderSum().equals(requestForm.getParams().getAmount())) {
            response.setError(new JSONRPC2Error(
                    -31001,
                    "Wrong amount",
                    "amount"));
            return false;
        }

        //ORDER CANCEL BO'LGAN YOKI YO'QLIGINI TEKSHRIAMIZ
        if (order.isCancelled()) {
            response.setError(new JSONRPC2Error(
                    -31099,
                    "Order already cancelled",
                    "order"));
            return false;
        }

        //ORDER ALLAQACHON YAKUNLANAGAN BO'LSA
        if (order.isPaid()) {
            response.setError(new JSONRPC2Error(
                    -31099,
                    "Order already finished",
                    "order"));
            return false;
        }

        response.setResult(new CheckPerformTransactionAllowResponse(
                new AdditionalInfo(order.getId(), order.getOrderSum()),
                true));
        return true;
    }


    /**
     * YANGI TRANSACTION OCHISH UCHUN YOKI ESKISI BO'LSA UNI MUDDATI O'TMAGANLIGINI TEKSHIRAMIZ
     * https://developer.help.paycom.uz/ru/metody-merchant-api/createtransaction
     *
     * @param requestForm @RequestBody
     * @param response    JSONRPC2Response
     */
    private void createTransaction(PaycomRequestForm requestForm, JSONRPC2Response response) {

        //PAYCOM DAN KELGAN ID BO'YICHA TRASACTION OLYAPMIZ
        Optional<OrderTransaction> optionalOrderTransaction = orderTransactionRepo.findByTransactionId(requestForm.getParams().getId());

        OrderTransaction orderTransaction;

        //AGAR OrderTransaction AVVAL YARATILGAN BO'LSA
        if (optionalOrderTransaction.isPresent()) {
            orderTransaction = optionalOrderTransaction.get();

            //OrderTransaction STATE IN PROGRESS DA BO'LMASA XATO QAYTARAMIZ
            if (!orderTransaction.getState().equals(TransactionState.STATE_IN_PROGRESS.getCode())) {
                response.setError(new JSONRPC2Error(
                        -31008,
                        "Unable to complete operation",
                        "transaction"));
                return;
            }

            //OrderTransaction YARATILGAN VAQTI 12 SOATDAN  KO'P BO'LSA XATO QAYTARAMIZ. MUDDATI O'TGAN ORDER
            if (System.currentTimeMillis() - orderTransaction.getTransactionCreationTime().getTime() > TIME_EXPIRED_PAYCOM_ORDER) {
                response.setError(new JSONRPC2Error(
                        -31008,
                        "Unable to complete operation",
                        "transaction"));

                //ORDER_TRANSACTION NI O'ZGARTIRIB SAQLAB QO'YAMIZ
                orderTransaction.setReason(4);
                orderTransaction.setState(TransactionState.STATE_CANCELED.getCode());
                orderTransactionRepo.save(orderTransaction);
                return;
            }
        }

        //OrderTransaction YARATILMAGAN BO'LSA
        else {

            //ORDER HAMMA JIHATDAN TO'G'RILIGINI TEKSHIRAMIZ
            boolean checkPerformTransaction = checkPerformTransaction(requestForm, response);

            //AGAR ORDER XATO BO'LSA XATONI YUBORAMIZ
            if (!checkPerformTransaction) {
                return;
            }

            //YANGI OrderTransaction
            orderTransaction = new OrderTransaction(
                    requestForm.getParams().getId(),
                    new Timestamp(requestForm.getParams().getTime()),
                    TransactionState.STATE_IN_PROGRESS.getCode(),
                    requestForm.getParams().getAccount().getOrder());

            orderTransactionRepo.save(orderTransaction);
        }

        //AVVAL SAQLANGAN MUDDATO O'TMAGAN OrderTransaction YOKI YANGI SAQLANGAN OrderTransaction NING MA'LUMOTLARI QAYTARILYAPTI
        response.setResult(new ResultForm(
                orderTransaction.getTransactionCreationTime().getTime(),
                orderTransaction.getState(),
                orderTransaction.getId().toString()));
    }


    /**
     * TO'LOVNI AMALGA OSHIRADIGAN METHOD
     * https://developer.help.paycom.uz/ru/metody-merchant-api/performtransaction
     *
     * @param requestForm @RequestBody
     * @param response    JSONRPC2Response
     */
    private void performTransaction(PaycomRequestForm requestForm, JSONRPC2Response response) {

        //PAYCOM DAN KELGAN ID BO'YICHA OrderTransaction NI QIDIRAMIZ
        Optional<OrderTransaction> optionalOrderTransaction = orderTransactionRepo.findByTransactionId(requestForm.getParams().getId());

        //AGAR OrderTransaction TOPILMASA XATOLIK QAYTARAMIZ
        if (optionalOrderTransaction.isEmpty()) {
            response.setError(new JSONRPC2Error(
                    -31003,
                    "Order transaction not found",
                    "transaction"));
            return;
        }

        OrderTransaction orderTransaction = optionalOrderTransaction.get();

        //OrderTransaction NING STATE IN_PROGRESS(1) BO'LSA
        if (orderTransaction.getState().equals(TransactionState.STATE_IN_PROGRESS.getCode())) {

            //OrderTransaction YARATILGAN VAQTI 12 SOATDAN  KO'P BO'LSA XATO QAYTARAMIZ. MUDDATI O'TGAN ORDER
            if (System.currentTimeMillis() - orderTransaction.getTransactionCreationTime().getTime() > TIME_EXPIRED_PAYCOM_ORDER) {
                response.setError(new JSONRPC2Error(
                        -31008,
                        "Unable to complete operation",
                        "transaction"));

                //ORDER_TRANSACTION NI O'ZGARTIRIB SAQLAB QO'YAMIZ
                orderTransaction.setReason(4);
                orderTransaction.setState(TransactionState.STATE_CANCELED.getCode());
                orderTransactionRepo.save(orderTransaction);
                return;
            }

            orderTransaction.setState(TransactionState.STATE_DONE.getCode());
            orderTransaction.setPerformTime(new Timestamp(System.currentTimeMillis()));
            orderTransactionRepo.save(orderTransaction);

            //TO'LOV BO'LDI
            Payment payment = new Payment(
                    orderTransaction.getOrder().getClient(),
                    (double) orderTransaction.getOrder().getOrderSum(),
                    new Timestamp(System.currentTimeMillis()),
                    orderTransaction.getId(),
                    orderTransaction.getTransactionId());
            paymentRepo.save(payment);

            Order order = orderTransaction.getOrder();
            order.setPaid(true);
            orderRepo.save(order);
        }

        //OrderTransaction GA TO'LOV QILINIB YAKUNIGA YETGAN BO'LSA
        if (orderTransaction.getState().equals(TransactionState.STATE_DONE.getCode())) {
            response.setResult(new ResultForm(
                    orderTransaction.getPerformTime().getTime(),
                    orderTransaction.getState(),
                    orderTransaction.getId().toString()));
            return;
        }

        //OrderTransaction NING STATE DONE(2) BO'LMASA XATOLIK BERAMIZ
        response.setError(new JSONRPC2Error(
                -31008,
                "Unable to complete operation",
                "transaction"));
    }

    /**
     * TRANSACTION NI BEKOR QILISH UCHUN METHOD
     * https://developer.help.paycom.uz/ru/metody-merchant-api/canceltransaction
     *
     * @param requestForm @RequestBody
     * @param response    JSONRPC2Response
     */
    private void cancelTransaction(PaycomRequestForm requestForm, JSONRPC2Response response) {
        Optional<OrderTransaction> optionalOrderTransaction = orderTransactionRepo.findByTransactionId(requestForm.getParams().getId());

        if (optionalOrderTransaction.isEmpty()) {
            response.setError(new JSONRPC2Error(
                    -31003,
                    "Order transaction not found",
                    "transaction"));
            return;
        }

        OrderTransaction orderTransaction = optionalOrderTransaction.get();

        if (orderTransaction.getState().equals(TransactionState.STATE_IN_PROGRESS.getCode())) {
            orderTransaction.setState(TransactionState.STATE_CANCELED.getCode());
            orderTransaction.setReason(requestForm.getParams().getReason());
            orderTransaction.setCancelTime(new Timestamp(System.currentTimeMillis()));
            orderTransactionRepo.save(orderTransaction);

            response.setResult(new ResultForm(
                    orderTransaction.getCancelTime().getTime(),
                    orderTransaction.getState(),
                    orderTransaction.getId().toString()));
            return;
        }

        if (orderTransaction.getState().equals(TransactionState.STATE_DONE.getCode())) {

            Order order = orderTransaction.getOrder();

            if (!order.isDelivered()) {
                Optional<Payment> optionalPayment = paymentRepo.findFirstByOrderTransactionIdOrderByPayDateDesc(orderTransaction.getId());

                if (optionalPayment.isPresent()) {
                    Payment payment = optionalPayment.get();
                    payment.setCancelled(true);
                    paymentRepo.save(payment);

                    orderTransaction.setState(TransactionState.STATE_POST_CANCELED.getCode());
                    orderTransaction.setReason(requestForm.getParams().getReason());
                    orderTransaction.setCancelTime(new Timestamp(System.currentTimeMillis()));
                    orderTransactionRepo.save(orderTransaction);

                    response.setResult(new ResultForm(
                            orderTransaction.getCancelTime().getTime(),
                            orderTransaction.getState(),
                            orderTransaction.getId().toString()));
                } else {
                    response.setError(new JSONRPC2Error(
                            -31007,
                            "Unable to cancel transaction",
                            "transaction"));
                    return;
                }
            } else {
                {
                    response.setError(new JSONRPC2Error(
                            -31007,
                            "Order already delivered",
                            "order"));
                    return;
                }
            }
        }
        response.setResult(new ResultForm(
                orderTransaction.getCancelTime().getTime(),
                orderTransaction.getState(),
                orderTransaction.getId().toString()));
    }

    /**
     * TRANSACTION HOLATINI BILISH UCHUN METHOD
     * https://developer.help.paycom.uz/ru/metody-merchant-api/checktransaction
     *
     * @param requestForm @RequestBody
     * @param response    JSONRPC2Response
     */
    private void checkTransaction(PaycomRequestForm requestForm, JSONRPC2Response response) {
        Optional<OrderTransaction> optionalOrderTransaction = orderTransactionRepo.findByTransactionId(requestForm.getParams().getId());

        if (optionalOrderTransaction.isEmpty()) {
            response.setError(new JSONRPC2Error(
                    -31003,
                    "Order transaction not found",
                    "transaction"));
            return;
        }

        OrderTransaction orderTransaction = optionalOrderTransaction.get();

        response.setResult(new ResultForm(
                orderTransaction.getCancelTime() != null ? orderTransaction.getCancelTime().getTime() : 0,
                orderTransaction.getTransactionCreationTime().getTime(),
                orderTransaction.getPerformTime() != null ? orderTransaction.getPerformTime().getTime() : 0,
                orderTransaction.getReason(),
                orderTransaction.getState(),
                orderTransaction.getId().toString()));
    }

    /**
     * PAYCOM TOMONIDAN MUVAFFAQIYATLI BAJRILGAN BARCHA OrderTransaction LARNI QAYTARAMIZ
     * https://developer.help.paycom.uz/ru/metody-merchant-api/getstatement
     *
     * @param requestForm @RequestBody
     * @param response    JSONRPC2Response
     */
    private void getStatement(PaycomRequestForm requestForm, JSONRPC2Response response) {

        //DB DAN PAYCOM BERGAN VAQT OALIG'IDA TRANSACTION STATE DONE(2) BO'LGAN OrderTransaction LAR OLINADI
        List<OrderTransaction> orderTransactionList =
                orderTransactionRepo.findAllByStateAndTransactionCreationTimeBetween(TransactionState.STATE_DONE.getCode(), new Timestamp(requestForm.getParams().getFrom()), new Timestamp(requestForm.getParams().getTo()));

        List<Transaction> transactions = new ArrayList<>();

        //OrderTransaction LARDAN Transaction OBJECTIGA MAP QILINADI
        for (OrderTransaction orderTransaction : orderTransactionList) {
            Transaction transaction = new Transaction(
                    orderTransaction.getTransactionId(),
                    new Account(orderTransaction.getOrderId()),
                    orderTransaction.getOrder().getOrderSum(),
                    0L,
                    orderTransaction.getTransactionCreationTime().getTime(),
                    orderTransaction.getPerformTime().getTime(),
                    null,
                    orderTransaction.getState(),
                    orderTransaction.getTransactionCreationTime().getTime(),
                    orderTransaction.getId().toString());

            transactions.add(transaction);
        }

        //PAYCOMGA Transaction LISTI YUBORILADI
        response.setResult(transactions);
    }



    /**
     * PAYCOM DAN KELGAN BASIC AUTHNI TEKSHIRAMIZ
     *
     * @param basicAuth String
     * @param response  JSONRPC2Response
     * @return boolean
     */
    private boolean checkPaycomUserAuth(String basicAuth, JSONRPC2Response response) {

        basicAuth = basicAuth.substring("Basic".length()).trim();

        byte[] decode = Base64.getDecoder().decode(basicAuth);

        basicAuth = new String(decode, Charset.defaultCharset());

        String[] split = basicAuth.split(":", 2);

        Optional<Client> optionalClient = clientRepo.findByPhoneNumber("Paycom");

        if (optionalClient.isPresent()) {
            Client client = optionalClient.get();
            if (passwordEncoder.matches(split[1], client.getPassword())) {

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(client, null, new ArrayList<>());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        response.setError(new JSONRPC2Error(-32504,
                "Error authentication",
                "auth"));
        return false;
    }
}
