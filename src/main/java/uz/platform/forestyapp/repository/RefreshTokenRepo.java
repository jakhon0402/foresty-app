package uz.platform.forestyapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.platform.forestyapp.entity.RefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken, UUID> {
    void deleteAllByUserId(UUID id);

    Optional<RefreshToken> findByUserId(UUID id);
}
