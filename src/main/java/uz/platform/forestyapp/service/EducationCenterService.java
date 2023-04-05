package uz.platform.forestyapp.service;

import uz.platform.forestyapp.entity.EducationCenter;
import uz.platform.forestyapp.entity.enums.PlanLimitName;
import uz.platform.forestyapp.payload.ApiResponse;
import uz.platform.forestyapp.payload.EducationCenterDto;

import java.util.List;
import java.util.UUID;

public interface EducationCenterService {
    ApiResponse addEducationCenter(EducationCenterDto educationCenterDto);

    List<EducationCenter> getEducationCenters();
    boolean checkPlanExpireDate(UUID educationCenterId);
}
