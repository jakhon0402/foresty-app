package uz.platform.forestyapp.service.utils;

import org.springframework.stereotype.Service;
import uz.platform.forestyapp.entity.TeacherPayment;

@Service
public class TeacherPaymentChangeService {

    public void changeTeacherPayment(TeacherPayment teacherPayment,Integer agreement,Long price,Long studentsCount){
        long paymentAmount = agreement * price / 100 * studentsCount;
        teacherPayment.setPaymentAmount(paymentAmount);
        teacherPayment.setDebtor(paymentAmount);
    }
}
