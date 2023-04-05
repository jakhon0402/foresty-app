package uz.platform.forestyapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.platform.forestyapp.entity.enums.GroupPaymentStatus;
import uz.platform.forestyapp.entity.template.AbsUUIDEntity;

import java.sql.Date;
import java.time.ZonedDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class GroupPayment extends AbsUUIDEntity {
    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private ZonedDateTime fromDate;

    @Column(nullable = false)
    private ZonedDateTime toDate;

    @Column(nullable = false)
    private GroupPaymentStatus status;

//    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    private Group group;
}
