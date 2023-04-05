package uz.platform.forestyapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.platform.forestyapp.entity.Payment;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepo extends JpaRepository<Payment, UUID> {
    Optional<Payment> findFirstByOrderTransactionIdOrderByPayDateDesc(Long orderTransactionId);
}
