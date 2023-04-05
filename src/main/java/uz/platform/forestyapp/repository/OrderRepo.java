package uz.platform.forestyapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.platform.forestyapp.entity.Order;

import java.util.UUID;

public interface OrderRepo extends JpaRepository<Order, UUID> {
}
