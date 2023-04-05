package uz.platform.forestyapp.service;

import uz.platform.forestyapp.entity.EducationCenter;
import uz.platform.forestyapp.entity.Student;
import uz.platform.forestyapp.entity.User;
import uz.platform.forestyapp.payload.ApiResponse;
import uz.platform.forestyapp.payload.StudentDto;
import uz.platform.forestyapp.payload.response.StudentRes;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentService {
    List<Student> getStudents(User currentUser);

    ApiResponse getStudent(UUID id,User currentUser);

    ApiResponse getStudentGroups(User currentUser,UUID studentId);

    ApiResponse addStudent(StudentDto studentDto, User currentUser);
    ApiResponse editStudent(UUID studentId,StudentDto studentDto,User currentUser);

    ApiResponse graduateStudent(UUID studentId, User currentUser);

    ApiResponse deleteStudent(UUID studentId,User currentUser);

    ApiResponse getStudentPayments(User user, UUID id);
}
