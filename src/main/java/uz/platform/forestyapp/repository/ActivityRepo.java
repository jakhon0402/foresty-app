package uz.platform.forestyapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.platform.forestyapp.entity.Activity;
import uz.platform.forestyapp.entity.enums.RoleName;

import java.util.List;
import java.util.UUID;

public interface ActivityRepo extends JpaRepository<Activity, UUID> {
    void deleteAllByUserId(UUID userId);

    List<Activity> findTop21ByUserEducationCenterIdAndUserRoleRoleNameInOrderByCreatedAtDesc(UUID id, List<RoleName> roleNames);

    List<Activity> findTop50ByUserIdOrderByCreatedAtDesc(UUID id);
}
