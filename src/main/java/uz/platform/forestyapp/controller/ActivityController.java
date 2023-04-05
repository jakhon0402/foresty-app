package uz.platform.forestyapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.platform.forestyapp.entity.User;
import uz.platform.forestyapp.payload.ApiResponse;
import uz.platform.forestyapp.security.CurrentUser;
import uz.platform.forestyapp.service.ActivityService;

import java.util.UUID;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {
    @Autowired
    ActivityService activityService;

    @GetMapping("/latest")
    HttpEntity<?> getLatestActivity(@CurrentUser User user){
        ApiResponse apiResponse = activityService.getLatestActivities(user);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @GetMapping("/{id}")
    HttpEntity<?> getEmployeeActivities(@CurrentUser User user, @PathVariable("id")UUID id){
        ApiResponse apiResponse = activityService.getEmployeeActivities(user,id);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @GetMapping("/profile")
    HttpEntity<?> getProfileActivities(@CurrentUser User user){
        ApiResponse apiResponse = activityService.getActivities(user);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }
}
