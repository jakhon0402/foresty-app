package uz.platform.forestyapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.platform.forestyapp.entity.Address;

import java.util.UUID;

public interface AddressRepo extends JpaRepository<Address, UUID> {
}
