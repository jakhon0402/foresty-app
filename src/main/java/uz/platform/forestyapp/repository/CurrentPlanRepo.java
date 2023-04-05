package uz.platform.forestyapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.platform.forestyapp.entity.CurrentPlan;

import java.util.UUID;

public interface CurrentPlanRepo extends JpaRepository<CurrentPlan, UUID> {
}
