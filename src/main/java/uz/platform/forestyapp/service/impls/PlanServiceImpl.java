package uz.platform.forestyapp.service.impls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.platform.forestyapp.entity.EducationCenter;
import uz.platform.forestyapp.entity.Plan;
import uz.platform.forestyapp.entity.enums.PlanLimitName;
import uz.platform.forestyapp.payload.ApiResponse;
import uz.platform.forestyapp.payload.PlanDto;
import uz.platform.forestyapp.repository.EducationCenterRepo;
import uz.platform.forestyapp.repository.PlanRepo;
import uz.platform.forestyapp.service.PlanService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PlanServiceImpl implements PlanService {
    @Autowired
    PlanRepo planRepo;

    @Autowired
    EducationCenterRepo educationCenterRepo;


    @Override
    public ApiResponse addPlan(PlanDto planDto) {
        Plan plan = Plan.builder()
                .name(planDto.getName())
                .price(planDto.getPrice())
                .employeesLimit(planDto.getEmployeesLimit())
                .teachersLimit(planDto.getTeachersLimit())
                .studentsLimit(planDto.getStudentsLimit())
                .groupsLimit(planDto.getGroupsLimit())
                .subjectsLimit(planDto.getSubjectsLimit())
                .roomsLimit(planDto.getRoomsLimit())
                .build();
        planRepo.save(plan);

        return new ApiResponse("Yangi tarif reja qo'shildi!",true);
    }

    @Override
    public List<Plan> getPlans() {
        return planRepo.findAll();
    }

    @Override
    public ApiResponse editPlan(PlanDto planDto, UUID id) {
        Optional<Plan> optionalPlan = planRepo.findById(id);
        if(optionalPlan.isEmpty()){
            return new ApiResponse("Bunday idlik tarif reja mavjud emas!",false);
        }
        Plan plan = optionalPlan.get();
        plan.setName(planDto.getName());
        plan.setPrice(planDto.getPrice());
        plan.setEmployeesLimit(planDto.getEmployeesLimit());
        plan.setTeachersLimit(planDto.getTeachersLimit());
        plan.setStudentsLimit(planDto.getStudentsLimit());
        plan.setGroupsLimit(planDto.getGroupsLimit());
        plan.setSubjectsLimit(planDto.getSubjectsLimit());
        plan.setRoomsLimit(planDto.getRoomsLimit());
        planRepo.save(plan);
        return new ApiResponse("Yangi tarif reja o'zgartirildi!",true);
    }



}
