package uz.platform.forestyapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.platform.forestyapp.entity.EmployeePayment;
import uz.platform.forestyapp.entity.enums.PaymentStatusName;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmployeePaymentRepo extends JpaRepository<EmployeePayment, UUID> {
    Optional<EmployeePayment> findByMonthAndEmployeeId(Long month,UUID employeeId);
    void deleteAllByEmployeeId(UUID employeeId);

    List<EmployeePayment> findAllByEmployeeId(UUID id);

    List<EmployeePayment> findAllByEmployeeIdAndStatus(UUID id, PaymentStatusName status);

}
