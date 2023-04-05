package uz.platform.forestyapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.platform.forestyapp.entity.Client;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepo extends JpaRepository<Client, UUID> {
    boolean existsByPhoneNumber(String paycom);

    Optional<Client> findByPhoneNumber(String username);
}
