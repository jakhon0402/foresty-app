package uz.platform.forestyapp.service;

import uz.platform.forestyapp.entity.EducationCenter;
import uz.platform.forestyapp.entity.Teacher;
import uz.platform.forestyapp.entity.User;
import uz.platform.forestyapp.payload.ApiResponse;
import uz.platform.forestyapp.payload.TeacherDto;
import uz.platform.forestyapp.payload.TeacherEditDto;

import java.util.List;
import java.util.UUID;

public interface TeacherService {
    ApiResponse addTeacher(TeacherDto teacherDto, User currentUser);

    ApiResponse blockTeacher(UUID teacherId, UUID educationCenterId);

    ApiResponse deleteTeacher(UUID teacherId, User currentUser);

    ApiResponse finishWork(UUID teacherId, User currentUser);

    List<Teacher> getTeachers(User currentUser);

    ApiResponse getTeacher(UUID id,User currentUser);

    ApiResponse getTeacherGroups(User user, UUID id);

    ApiResponse editTeacher(TeacherEditDto teacherDto, User user, UUID id);

    ApiResponse getTeacherPayments(User user, UUID id);
}
