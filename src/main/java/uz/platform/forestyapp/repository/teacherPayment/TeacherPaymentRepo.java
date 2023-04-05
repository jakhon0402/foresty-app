package uz.platform.forestyapp.repository.teacherPayment;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.platform.forestyapp.entity.TeacherPayment;
import uz.platform.forestyapp.entity.enums.PaymentStatusName;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeacherPaymentRepo extends JpaRepository<TeacherPayment, UUID> {
    Optional<TeacherPayment> findByMonthAndTeacherGroupPaymentGroupId(Integer month, UUID groupId);
    List<TeacherPayment> findAllByTeacherGroupPaymentGroupId(UUID id);

    List<TeacherPayment> findAllByTeacherGroupPaymentGroupTeacherIdAndStatusOrderByCreatedAt(UUID id,PaymentStatusName status);

    List<TeacherPayment> findAllByTeacherGroupPaymentGroupIdAndMonthBetweenOrderByMonthAsc(UUID id,Integer fromMonth,Integer toMonth);

    void deleteAllByTeacherGroupPaymentGroupId(UUID id);

    void deleteAllByTeacherGroupPaymentGroupTeacherId(UUID id);

    boolean existsByTeacherGroupPaymentIdAndStatusInAndIdNot(UUID tgpId, List<PaymentStatusName> status, UUID spId);
}
