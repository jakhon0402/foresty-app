package uz.platform.forestyapp.repository.studentPayment;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.platform.forestyapp.entity.Student;
import uz.platform.forestyapp.entity.StudentPayment;
import uz.platform.forestyapp.entity.enums.PaymentStatusName;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentPaymentRepo extends JpaRepository<StudentPayment, UUID> {
    List<StudentPayment> findAllByStatusAndGroupPaymentMonthAndGroupPaymentGroupId(PaymentStatusName status,Integer month,UUID id);
    List<StudentPayment> findAllByGroupPaymentId(UUID id);

    List<StudentPayment> findAllByGroupPaymentGroupId(UUID id);

    List<StudentPayment> findAllByStudentIdAndStatusOrderByCreatedAt(UUID id,PaymentStatusName status);

    List<StudentPayment> findAllByGroupPaymentGroupIdAndStudentIdAndGroupPaymentMonthBetweenOrderByGroupPaymentMonth(UUID groupId, UUID studentId,Integer fromMonth,Integer toMonth);

    List<StudentPayment> findAllByGroupPaymentGroupIdAndStudentIdAndGroupPaymentMonthBetweenOrderByGroupPaymentMonthDesc(UUID groupId, UUID studentId,Integer fromMonth,Integer toMonth);


    void deleteAllByGroupPaymentGroupIdAndStudentIdAndGroupPaymentMonthBetween(UUID groupId, UUID studentId,Integer fromMonth,Integer toMonth);

    void deleteAllByGroupPaymentGroupId(UUID groupId);

    boolean existsByGroupPaymentIdAndStatusInAndIdNot(UUID groupPaymentId,List<PaymentStatusName> status,UUID spId);

}
