package uz.platform.forestyapp.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.platform.forestyapp.entity.Teacher;
import uz.platform.forestyapp.entity.TeacherGroupPayment;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentTeacherRes {
    private Teacher teacher;
    private List<TeacherGroupPaymentImpl> teacherGroups;
}
