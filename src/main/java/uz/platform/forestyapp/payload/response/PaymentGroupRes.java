package uz.platform.forestyapp.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import uz.platform.forestyapp.entity.Group;
import uz.platform.forestyapp.entity.GroupPayment;

import java.util.List;

@Data
@AllArgsConstructor
public class PaymentGroupRes {
    private Group group;
    private List<GroupPayment> groupPayments;
    private Long studentsCount;
    private Long debtorsCount;
}
