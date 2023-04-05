package uz.platform.forestyapp.service;

import uz.platform.forestyapp.entity.User;
import uz.platform.forestyapp.payload.ApiResponse;

import java.util.UUID;

public interface PaymentService {
    ApiResponse receivePayment(UUID studentPaymentId, Long money, User currentUser);
    ApiResponse payPayment(UUID teacherPaymentId,Long money, User currentUser);
    ApiResponse paySalary(UUID employeePaymentId,Long money,User currentUser);

    ApiResponse getGroups(User currentUser);

    ApiResponse getGroup(User currentUser,UUID groupId);

    ApiResponse getStudentPayments(User currentUser,UUID groupPaymentId);

    ApiResponse getTeachers(User user);

    ApiResponse getTeacher(User user, UUID id);

    ApiResponse getTeacherGroupPayments(User user, UUID id);

    ApiResponse getEmployees(User user);

    ApiResponse getEmployee(User user, UUID id);

    ApiResponse getPayments(User user);

    ApiResponse getFinanceData(User user);
}
