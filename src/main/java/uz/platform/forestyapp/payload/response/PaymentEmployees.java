package uz.platform.forestyapp.payload.response;

import uz.platform.forestyapp.entity.EmployeePayment;
import uz.platform.forestyapp.entity.enums.EmployeeStatusName;
import uz.platform.forestyapp.entity.enums.GroupPaymentStatus;
import uz.platform.forestyapp.entity.enums.PaymentStatusName;
import uz.platform.forestyapp.entity.enums.RoleName;

import java.util.List;
import java.util.UUID;

public interface PaymentEmployees {
    UUID getId();
    String getFirstName();
    String getLastName();
    EmployeeStatusName getEmployeeStatus();
    RoleName getRoleName();
    Long getSalary();
    PaymentStatusName getStatus();
}
