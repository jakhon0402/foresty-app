package uz.platform.forestyapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.platform.forestyapp.entity.enums.CurrentPlanStatus;
import uz.platform.forestyapp.entity.template.AbsUUIDEntity;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class CurrentPlan extends AbsUUIDEntity {
    @ManyToOne
    private Plan plan;

    @Column(nullable = false)
    private ZonedDateTime expireDate;

    @Column(nullable = false)
    private CurrentPlanStatus status;
}
