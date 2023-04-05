package uz.platform.forestyapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.platform.forestyapp.entity.enums.PaymentTypeName;
import uz.platform.forestyapp.entity.template.AbsUUIDEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class PaymentCenter extends AbsUUIDEntity {
    @Column(updatable = false)
    private Long amount;

    @Column(nullable = false)
    private String context;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private PaymentTypeName type;

    @Column
    private String secondaryContext;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    private EducationCenter educationCenter;
}
