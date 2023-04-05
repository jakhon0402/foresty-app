package uz.platform.forestyapp.service.impls;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.platform.forestyapp.entity.Activity;
import uz.platform.forestyapp.entity.User;
import uz.platform.forestyapp.entity.enums.RoleName;
import uz.platform.forestyapp.payload.ApiResponse;
import uz.platform.forestyapp.repository.ActivityRepo;
import uz.platform.forestyapp.repository.UserRepo;
import uz.platform.forestyapp.service.ActivityService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    ActivityRepo activityRepo;

    @Autowired
    UserRepo userRepo;


    @Override
    public ApiResponse getLatestActivities(User currentUser) {
        List<Activity> activities = activityRepo.findTop21ByUserEducationCenterIdAndUserRoleRoleNameInOrderByCreatedAtDesc(currentUser.getEducationCenter().getId(), Arrays.asList(RoleName.ADMIN, RoleName.MODERATOR, RoleName.FINANCIER));
        return new ApiResponse(activities,true);
    }

    @Override
    public ApiResponse getEmployeeActivities(User currentUser, UUID userId) {
        Optional<User> optionalUser = userRepo.findById(userId);
        if(optionalUser.isEmpty()) return new ApiResponse("Bunday idlik user mavjud emas!",false);
        User user = optionalUser.get();
        if(!user.getEducationCenter().getId().equals(currentUser.getEducationCenter().getId())) return new ApiResponse("Ushbu user joriy o'quv markazga tegishli emas!",false);
        List<Activity> activities = activityRepo.findTop50ByUserIdOrderByCreatedAtDesc(user.getId());
        return new ApiResponse(activities,true);
    }

    @Override
    public ApiResponse getActivities(User currentUser) {
        return new ApiResponse(activityRepo.findTop50ByUserIdOrderByCreatedAtDesc(currentUser.getId()),true);
    }
}
