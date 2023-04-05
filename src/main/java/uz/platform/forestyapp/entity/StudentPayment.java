package uz.platform.forestyapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.platform.forestyapp.entity.enums.PaymentStatusName;
import uz.platform.forestyapp.entity.enums.StudentStatusName;
import uz.platform.forestyapp.entity.template.AbsUUIDEntity;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StudentPayment extends AbsUUIDEntity {
    @Column
    private UUID studentId;

    @Column(nullable = false)
    private String studentFullName;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private StudentStatusName studentStatus;

    @Column(nullable = false)
    private Long paymentAmount;

    @ManyToOne(fetch = FetchType.EAGER)
    private GroupPayment groupPayment;

    @Column(nullable = false)
    private long paidAmount;

    @Column(nullable = false)
    private long debtor;

    @Column(nullable = false)
    private PaymentStatusName status;
}
