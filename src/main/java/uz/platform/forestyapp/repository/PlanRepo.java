package uz.platform.forestyapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.platform.forestyapp.entity.Plan;

import java.util.UUID;

public interface PlanRepo extends JpaRepository<Plan, UUID> {
}
