package uz.platform.forestyapp.repository.studentPayment;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.platform.forestyapp.entity.GroupPayment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupPaymentRepo extends JpaRepository<GroupPayment, UUID> {
    Optional<GroupPayment> findByMonthAndGroupId(Integer month, UUID groupId);

    List<GroupPayment> findAllByGroupId(UUID groupId);

    void deleteAllByGroupId(UUID groupId);

}
