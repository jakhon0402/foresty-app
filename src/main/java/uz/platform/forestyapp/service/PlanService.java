package uz.platform.forestyapp.service;

import uz.platform.forestyapp.entity.Plan;
import uz.platform.forestyapp.entity.enums.PlanLimitName;
import uz.platform.forestyapp.payload.ApiResponse;
import uz.platform.forestyapp.payload.PlanDto;

import java.util.List;
import java.util.UUID;

public interface PlanService {
    ApiResponse addPlan(PlanDto planDto);

    List<Plan> getPlans();

    ApiResponse editPlan(PlanDto planDto, UUID id);
}
