package uz.platform.forestyapp.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HomeRes {

    private HomeField employee;
    private HomeField teacher;
    private HomeField group;
    private HomeField student;

    public HomeRes(Long employeeCount,double employeePercentage,
                   Long teacherCount,double teacherPercentage,
                   Long groupCount,double groupPercentage,
                   Long studentCount,double studentPercentage) {
        this.employee = new HomeField(employeeCount, employeePercentage);
        this.teacher = new HomeField(teacherCount, teacherPercentage);
        this.group = new HomeField(groupCount, groupPercentage);
        this.student = new HomeField(studentCount, studentPercentage);
    }

    @Data
    @AllArgsConstructor
    private static class HomeField {
        private Long count;
        private double percentage;
    }
}


