package uz.platform.forestyapp.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.platform.forestyapp.entity.Group;
import uz.platform.forestyapp.entity.Subject;
import uz.platform.forestyapp.entity.enums.GroupStatusName;

import java.util.UUID;


public interface PaymentGroups {
    UUID getId();
    Subject getSubject();
    String getName();
    GroupStatusName getStatus();
    Long getPrice();
    Long getDebtorsCount();
    Long getStudentsCount();
}
