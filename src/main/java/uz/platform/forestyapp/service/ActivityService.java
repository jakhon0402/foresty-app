package uz.platform.forestyapp.service;

import uz.platform.forestyapp.entity.User;
import uz.platform.forestyapp.payload.ApiResponse;

import java.util.UUID;

public interface ActivityService {
    ApiResponse getLatestActivities(User currentUser);
    ApiResponse getEmployeeActivities(User currentUser, UUID userId);
    ApiResponse getActivities(User currentUser);
}
