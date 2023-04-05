package uz.platform.forestyapp.service.impls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.platform.forestyapp.entity.*;
import uz.platform.forestyapp.entity.enums.*;
import uz.platform.forestyapp.payload.ApiResponse;
import uz.platform.forestyapp.payload.response.*;
import uz.platform.forestyapp.repository.*;
import uz.platform.forestyapp.repository.studentPayment.GroupPaymentRepo;
import uz.platform.forestyapp.repository.studentPayment.StudentPaymentRepo;
import uz.platform.forestyapp.repository.teacherPayment.TeacherGroupPaymentRepo;
import uz.platform.forestyapp.repository.teacherPayment.TeacherPaymentRepo;
import uz.platform.forestyapp.service.PaymentService;
import uz.platform.forestyapp.service.utils.ActivityService;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired StudentPaymentRepo studentPaymentRepo;

    @Autowired TeacherPaymentRepo teacherPaymentRepo;

    @Autowired TeacherGroupPaymentRepo teacherGroupPaymentRepo;

    @Autowired EmployeePaymentRepo employeePaymentRepo;

    @Autowired ActivityService activityService;

    @Autowired GroupPaymentRepo groupPaymentRepo;

    @Autowired GroupRepo groupRepo;

    @Autowired TeacherRepo teacherRepo;

    @Autowired
    EmployeeRepo employeeRepo;

    @Autowired
    PaymentCenterRepo paymentCenterRepo;


    @Override
    public ApiResponse getGroups(User currentUser) {
        return new ApiResponse(groupRepo.getGroupsPayment(currentUser.getEducationCenter().getId()),true);
    }

    @Override
    public ApiResponse getGroup(User currentUser, UUID groupId) {
        Optional<Group> optionalGroup = groupRepo.findById(groupId);
        if(optionalGroup.isEmpty()) return new ApiResponse("Bunday idlik guruh mavjud emas!",false);
        Group group = optionalGroup.get();
        if(!group.getEducationCenter().getId().equals(currentUser.getEducationCenter().getId())) return new ApiResponse("Ushbu guruh joriy o'quv markazga tegishli emas!",false);
        PaymentGroup groupPayment = groupRepo.getGroupPayment(group.getId());
        List<GroupPayment> groupPayments = groupPaymentRepo.findAllByGroupId(group.getId());
        PaymentGroupRes paymentGroupRes = new PaymentGroupRes(
                groupPayment.getGroupData(),groupPayments,groupPayment.getStudentsCount(),groupPayment.getDebtorsCount()
        );
        return new ApiResponse(paymentGroupRes,true);
    }

    @Override
    public ApiResponse getStudentPayments(User currentUser, UUID groupPaymentId) {
        Optional<GroupPayment> optionalGroupPayment = groupPaymentRepo.findById(groupPaymentId);
        if(optionalGroupPayment.isEmpty()) return new ApiResponse("Bunday idlik guruh tulovi mavjud emas!",false);
        GroupPayment groupPayment = optionalGroupPayment.get();
        Group group = groupPayment.getGroup();
        if(!group.getEducationCenter().getId().equals(currentUser.getEducationCenter().getId())) return new ApiResponse("Ushbu guruh joriy o'quv markazga tegishli emas!",false);
        List<StudentPayment> studentPayments = studentPaymentRepo.findAllByGroupPaymentId(groupPayment.getId());
        return new ApiResponse(studentPayments,true);
    }

    @Override
    public ApiResponse getTeachers(User user) {
        List<PaymentTeachers> paymentTeachers = teacherRepo.getPaymentTeachers(user.getEducationCenter().getId());
        return new ApiResponse(paymentTeachers,true);
    }

    @Override
    public ApiResponse getTeacher(User user, UUID id) {
        Optional<Teacher> optionalTeacher = teacherRepo.findById(id);
        if(optionalTeacher.isEmpty()) return new ApiResponse("Bunday idlik o'qituvchi mavjud emas!",true);
        Teacher teacher = optionalTeacher.get();
        if(!teacher.getTeacher().getEducationCenter().getId().equals(user.getEducationCenter().getId())) return new ApiResponse("Ushbu o'qituvchi joriy o'quv markazga tegishli emas!",false);
        List<TeacherGroupPaymentImpl> teacherGroupPayments = teacherGroupPaymentRepo.getTeacherGroups(teacher.getId());
        PaymentTeacherRes paymentTeacherRes = PaymentTeacherRes.builder().teacher(teacher).teacherGroups(teacherGroupPayments).build();
        return new ApiResponse(paymentTeacherRes,true);
    }

    @Override
    public ApiResponse getTeacherGroupPayments(User user, UUID id) {
        Optional<Group> optionalGroup = groupRepo.findById(id);
        if(optionalGroup.isEmpty()) return new ApiResponse("Bunday idlik guruh mavjud emas!",false);
        Group group = optionalGroup.get();
        if(!group.getEducationCenter().getId().equals(user.getEducationCenter().getId())) return new ApiResponse("Ushbu guruh joriy o'quv markazga tegishli emas!",false);
        List<TeacherPayment> teacherPayments = teacherPaymentRepo.findAllByTeacherGroupPaymentGroupId(group.getId());
        return new ApiResponse(teacherPayments,true);
    }

    @Override
    public ApiResponse getEmployees(User user) {
        List<PaymentEmployees> paymentEmployees = employeeRepo.getPaymentEmployeesByEduCenterId(user.getEducationCenter().getId());
        return new ApiResponse(paymentEmployees,true);
    }

    @Override
    public ApiResponse getEmployee(User user, UUID id) {
        Optional<Employee> optionalEmployee = employeeRepo.findById(id);
        if(optionalEmployee.isEmpty()) return new ApiResponse("Bunday idlik xodim mavjud emas!",true);
        Employee employee = optionalEmployee.get();
        if(!employee.getEmployee().getEducationCenter().getId().equals(user.getEducationCenter().getId())) return new ApiResponse("Ushbu xodim joriy o'quv markazga tegishli emas!",false);
        List<EmployeePayment> employeePayments = employeePaymentRepo.findAllByEmployeeId(id);
        return new ApiResponse(employeePayments,true);
    }

    @Override
    public ApiResponse getPayments(User user) {
        ZoneId zoneId = ZoneId.of("Asia/Samarkand");
        ZonedDateTime toDate = ZonedDateTime.now(zoneId);
        ZonedDateTime fromDate = ZonedDateTime.of(toDate.getYear(),1,1,0,0,0,0,zoneId);
        List<PaymentCenter> paymentCenters = paymentCenterRepo.getAllPaymentsByEduCenterId(Timestamp.from(fromDate.toInstant()),Timestamp.from(toDate.toInstant()),user.getEducationCenter().getId());
        return new ApiResponse(paymentCenters,true);
    }

    @Override
    public ApiResponse getFinanceData(User user) {
        ZoneId zoneId = ZoneId.of("Asia/Samarkand");
        ZonedDateTime yesterday = ZonedDateTime.now(zoneId).minusDays(1);
        return new ApiResponse(paymentCenterRepo.getFinanceData(user.getEducationCenter().getId(),Timestamp.from(yesterday.toInstant())),true);
    }


    @Override
    public ApiResponse receivePayment(UUID studentPaymentId,Long money, User currentUser) {
        Optional<StudentPayment> optionalStudentPayment = studentPaymentRepo.findById(studentPaymentId);
        if(optionalStudentPayment.isEmpty()) return new ApiResponse("Bunday idlik talaba to'lovi mavjud emas!",false);

        StudentPayment studentPayment = optionalStudentPayment.get();
        if(!studentPayment.getGroupPayment().getGroup().getEducationCenter().getId().equals(currentUser.getEducationCenter().getId())) return new ApiResponse("Ushbu to'lov joriy uquv markaz uchun emas!",false);
        if(studentPayment.getGroupPayment().getMonth()>studentPayment.getGroupPayment().getGroup().getCurrentMonth()) return new ApiResponse("Ushbu oy uchun to'lov hali amalga oshirib bo'lmaydi!",false);
        if(studentPayment.getStatus().equals(PaymentStatusName.PAID)) return new ApiResponse("To'lov allaqachon amalga oshirilgan",false);
        if(money<=0||money>studentPayment.getDebtor()) return new ApiResponse("To'lov qiymati noto'g'ri kiritildi!",false);

        GroupPayment groupPayment = studentPayment.getGroupPayment();
        if(money==studentPayment.getDebtor()){
            studentPayment.setStatus(PaymentStatusName.PAID);
            if(!studentPaymentRepo.existsByGroupPaymentIdAndStatusInAndIdNot(groupPayment.getId(),List.of(PaymentStatusName.PROCESS,PaymentStatusName.UNPAID),studentPayment.getId())){
                groupPayment.setStatus(GroupPaymentStatus.COMPLETED);
                groupPaymentRepo.save(groupPayment);
            }
        }
        studentPayment.setPaidAmount(money + studentPayment.getPaidAmount());
        studentPayment.setDebtor(studentPayment.getDebtor()-money);

        PaymentCenter paymentCenter = PaymentCenter.builder()
                .amount(money)
                .context(studentPayment.getStudentFullName())
                .secondaryContext(studentPayment.getGroupPayment().getGroup().getName() + " " + studentPayment.getGroupPayment().getMonth() + "-oy uchun")
                .type(PaymentTypeName.INCOME)
                .educationCenter(groupPayment.getGroup().getEducationCenter())
                        .build();
        paymentCenterRepo.save(paymentCenter);

        studentPaymentRepo.save(studentPayment);
        String context = studentPayment.getStudentFullName() + " " + studentPayment.getGroupPayment().getGroup().getName() + " " + studentPayment.getGroupPayment().getMonth() + "-oy uchun";
        activityService.addActivity(context,money.toString(),"", ActivityName.RECEIVE_PAYMENT,currentUser);
        return new ApiResponse("To'lov amalga oshirildi!",true);
    }

    @Override
    public ApiResponse payPayment(UUID teacherPaymentId,Long money, User currentUser) {
        Optional<TeacherPayment> optionalTeacherPayment = teacherPaymentRepo.findById(teacherPaymentId);
        if(optionalTeacherPayment.isEmpty()) return new ApiResponse("Bunday idlik o'qituvchi to'lovi mavjud emas!",false);

        TeacherPayment teacherPayment = optionalTeacherPayment.get();
        if(!teacherPayment.getTeacherGroupPayment().getGroup().getEducationCenter().getId().equals(currentUser.getEducationCenter().getId())) return new ApiResponse("Ushbu to'lov joriy uquv markaz uchun emas!",false);
        if(teacherPayment.getMonth()>=teacherPayment.getTeacherGroupPayment().getGroup().getCurrentMonth()&&teacherPayment.getTeacherGroupPayment().getGroup().getStatus().equals(GroupStatusName.ACTIVE)) return new ApiResponse("Ushbu oy uchun to'lov hali amalga oshirib bo'lmaydi!",false);

        if(teacherPayment.getStatus().equals(PaymentStatusName.PROCESS)) return new ApiResponse("Invalid teacher paymentCenter!",false);
        if(teacherPayment.getStatus().equals(PaymentStatusName.PAID)) return new ApiResponse("To'lov allaqachon amalga oshirilgan",false);
        if(money<=0||money>teacherPayment.getDebtor()) return new ApiResponse("To'lov qiymati noto'g'ri kiritildi",false);


        if(money==teacherPayment.getDebtor()){
            teacherPayment.setStatus(PaymentStatusName.PAID);
            TeacherGroupPayment teacherGroupPayment = teacherPayment.getTeacherGroupPayment();
            if(!teacherPaymentRepo.existsByTeacherGroupPaymentIdAndStatusInAndIdNot(teacherGroupPayment.getId(),List.of(PaymentStatusName.PROCESS,PaymentStatusName.UNPAID),teacherPayment.getId())){
                teacherGroupPayment.setStatus(GroupPaymentStatus.COMPLETED);
                teacherGroupPaymentRepo.save(teacherGroupPayment);
            }
        }
        teacherPayment.setPaidAmount(money + teacherPayment.getPaidAmount());
        teacherPayment.setDebtor(teacherPayment.getDebtor()-money);


        teacherPaymentRepo.save(teacherPayment);
        User user = teacherPayment.getTeacherGroupPayment().getGroup().getTeacher().getTeacher();
        String teacherFullName = user.getFirstName() + " " + user.getLastName();
        String context = teacherFullName + " " + teacherPayment.getTeacherGroupPayment().getGroup().getName() + " " + teacherPayment.getMonth() + "-oy uchun";

        PaymentCenter paymentCenter = PaymentCenter.builder()
                .amount(money)
                .context(teacherFullName)
                .secondaryContext(teacherPayment.getTeacherGroupPayment().getGroup().getName() + " " + teacherPayment.getMonth() + "-oy uchun")
                .type(PaymentTypeName.EXPENSE)
                .educationCenter(user.getEducationCenter())
                .build();
        paymentCenterRepo.save(paymentCenter);

        activityService.addActivity(context,money.toString(),"", ActivityName.PAYMENT_TEACHER,currentUser);
        return new ApiResponse("To'lov amalga oshirildi!",true);
    }

    @Override
    public ApiResponse paySalary(UUID employeePaymentId,Long money, User currentUser) {
        Optional<EmployeePayment> optionalEmployeePayment = employeePaymentRepo.findById(employeePaymentId);
        if(optionalEmployeePayment.isEmpty()) return new ApiResponse("Bunday idlik xodim to'lovi mavjud emas!",false);

        EmployeePayment employeePayment = optionalEmployeePayment.get();
        if(!employeePayment.getEmployee().getEmployee().getEducationCenter().getId().equals(currentUser.getEducationCenter().getId())) return new ApiResponse("Ushbu to'lov joriy uquv markaz uchun emas!",false);
        if(employeePayment.getStatus().equals(PaymentStatusName.PAID)) return new ApiResponse("To'lov allaqachon amalga oshirilgan",false);
        if(money<=0||money>employeePayment.getDebtor()) return new ApiResponse("Kiritilgan summa qarzdorlikdan ko'p kiritildi!",false);
        if(money==employeePayment.getDebtor()){
            employeePayment.setStatus(PaymentStatusName.PAID);
        }
        employeePayment.setPaidAmount(money + employeePayment.getPaidAmount());
        employeePayment.setDebtor(employeePayment.getDebtor()-money);

        employeePaymentRepo.save(employeePayment);
        User user = employeePayment.getEmployee().getEmployee();
        String employeeFullName = user.getFirstName() + " " + user.getLastName();
        String context = employeeFullName + " " + employeePayment.getMonth() + "-oy uchun";
        PaymentCenter paymentCenter = PaymentCenter.builder()
                .amount(money)
                .context(employeeFullName)
                .secondaryContext(employeePayment.getMonth() + "-oy uchun")
                .type(PaymentTypeName.EXPENSE)
                .educationCenter(user.getEducationCenter())
                .build();
        paymentCenterRepo.save(paymentCenter);
        activityService.addActivity(context,money.toString(),"", ActivityName.PAYMENT_EMPLOYEE,currentUser);
        return new ApiResponse("To'lov amalga oshirildi!",true);
    }


}
