package uz.platform.forestyapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.platform.forestyapp.entity.EducationCenter;

import java.util.Optional;
import java.util.UUID;

public interface EducationCenterRepo extends JpaRepository<EducationCenter, UUID> {
    boolean existsByUsername(String username);
}
