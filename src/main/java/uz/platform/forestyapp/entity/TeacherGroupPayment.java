package uz.platform.forestyapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.platform.forestyapp.entity.enums.GroupPaymentStatus;
import uz.platform.forestyapp.entity.template.AbsUUIDEntity;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TeacherGroupPayment extends AbsUUIDEntity {
    @Column(nullable = false)
    private GroupPaymentStatus status;

    @OneToOne
    private Group group;
}
