package uz.platform.forestyapp.payload.response;

import uz.platform.forestyapp.entity.Group;
import uz.platform.forestyapp.entity.GroupPayment;
import uz.platform.forestyapp.entity.StudentPayment;

import java.util.List;

public interface PaymentGroup {
    Group getGroupData();
    Long getDebtorsCount();
    Long getStudentsCount();
}
