package uz.platform.forestyapp.payload.response;

import uz.platform.forestyapp.entity.Group;
import uz.platform.forestyapp.entity.Subject;
import uz.platform.forestyapp.entity.Teacher;
import uz.platform.forestyapp.entity.enums.TeacherStatusName;

import java.util.List;

public interface PaymentTeachers {
    Teacher getTeacher();
    Long getGroupsCount();
    Long getMonthlySalary();
    Long getDebtorsCount();
}
