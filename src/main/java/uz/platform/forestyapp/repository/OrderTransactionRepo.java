package uz.platform.forestyapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.platform.forestyapp.entity.OrderTransaction;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderTransactionRepo extends JpaRepository<OrderTransaction, UUID> {
    Optional<OrderTransaction> findByTransactionId(String transactionId);

    List<OrderTransaction> findAllByStateAndTransactionCreationTimeBetween(Integer state, Timestamp fromTransactionCreationTime, Timestamp toTransactionCreationTime);
}
