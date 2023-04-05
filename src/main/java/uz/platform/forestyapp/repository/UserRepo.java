package uz.platform.forestyapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.platform.forestyapp.entity.Role;
import uz.platform.forestyapp.entity.User;
import uz.platform.forestyapp.entity.enums.RoleName;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepo extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

    boolean existsByUsernameAndIdNot(String username,UUID id);

    Optional<User> findByEmailAndRole(String email, Role role);

    Optional<User> findByRole(Role role);
    List<User> findAllByEmail(String email);
    boolean existsByEmailAndRole(String email, Role role);

    boolean existsByEmailAndRoleIsNot(String email,Role role);

    boolean existsByEmailAndRoleRoleNameIsNotAndIdNot(String email, RoleName role, UUID id);
}
