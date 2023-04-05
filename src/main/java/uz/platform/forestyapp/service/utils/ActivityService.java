package uz.platform.forestyapp.service.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.platform.forestyapp.entity.Activity;
import uz.platform.forestyapp.entity.User;
import uz.platform.forestyapp.entity.enums.ActivityName;
import uz.platform.forestyapp.repository.ActivityRepo;

import java.util.UUID;


@Service
public class ActivityService {
    @Autowired
    ActivityRepo activityRepo;

    public Activity addActivity(String context,String secondaryContext, String url, ActivityName activityName, User user){
        Activity activity = Activity.builder()
                .context(context)
                .activityName(activityName)
                .secondaryContext(secondaryContext)
                .url("http://localhost:3000"+url)
                .user(user)
                .build();
        return activityRepo.save(activity);
    }

    public Activity addActivity(String context, String url, ActivityName activityName, User user){
        Activity activity = Activity.builder()
                .context(context)
                .activityName(activityName)
                .url("http://localhost:3000"+url)
                .user(user)
                .build();
        return activityRepo.save(activity);
    }

    public void deleteActivities(UUID userId){
        activityRepo.deleteAllByUserId(userId);
    }
}
