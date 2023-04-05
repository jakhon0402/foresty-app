package uz.platform.forestyapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.platform.forestyapp.entity.OnlineUser;

public interface OnlineUserRepo extends JpaRepository<OnlineUser,Long> {
}
