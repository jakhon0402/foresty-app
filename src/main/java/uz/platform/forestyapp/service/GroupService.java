package uz.platform.forestyapp.service;

import uz.platform.forestyapp.entity.EducationCenter;
import uz.platform.forestyapp.entity.Group;
import uz.platform.forestyapp.entity.User;
import uz.platform.forestyapp.payload.ApiResponse;
import uz.platform.forestyapp.payload.GroupDto;

import java.util.List;
import java.util.UUID;

public interface GroupService {
    ApiResponse addGroup(GroupDto groupDto, User currentUser);
    ApiResponse editGroup(UUID groupId, GroupDto groupDto, User currentUser);
    ApiResponse deleteGroup(UUID groupId, User currentUser);

    ApiResponse addStudentToGroup(UUID groupId, UUID studentId,User currentUser);
    ApiResponse removeStudentFromGroup(UUID groupStudentId, User currentUser);

    ApiResponse completeGroup(UUID groupId, User currentUser);

    List<Group> getGroups(User user);

    ApiResponse getGroup(User currentUser,UUID id);

    ApiResponse getStudents(User user, UUID id);

    ApiResponse getTopGroups(User user);
}
