package uz.platform.forestyapp.service;

import uz.platform.forestyapp.entity.Employee;
import uz.platform.forestyapp.entity.User;
import uz.platform.forestyapp.entity.enums.EmployeeStatusName;
import uz.platform.forestyapp.payload.ApiResponse;
import uz.platform.forestyapp.payload.EmployeeDto;
import uz.platform.forestyapp.payload.EmployeeEditDto;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {

    List<Employee> getEmployees(User user);
    ApiResponse getEmployee(User user,UUID id);

    ApiResponse addEmployee(EmployeeDto employeeDto, User currentUser);
    ApiResponse blockEmployee(UUID employeeId, UUID educationCenterId);
    ApiResponse deleteEmployee(UUID employeeId, User currentUser);

    ApiResponse finishWork(UUID employeeId, User currentUser);

    ApiResponse editEmployee(EmployeeEditDto employeeDto, User user,UUID id);

    Object getEmployeePayments(User user, UUID id);
}
