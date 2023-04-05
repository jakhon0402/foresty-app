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
import uz.platform.forestyapp.entity.enums.PaymentStatusName;
import uz.platform.forestyapp.entity.template.AbsUUIDEntity;

import java.sql.Date;
import java.time.ZonedDateTime;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
@Entity
public class TeacherPayment extends AbsUUIDEntity {
    @Column(nullable = false,updatable = false)
    private int month;

    @Column(nullable = false)
    private ZonedDateTime fromDate;

    @Column(nullable = false)
    private ZonedDateTime toDate;

    @Column(nullable = false)
    private PaymentStatusName status;

    @Column(nullable = false)
    private long paymentAmount;

    @Column(nullable = false)
    private long paidAmount;

    @Column(nullable = false)
    private long debtor;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    private TeacherGroupPayment teacherGroupPayment;


}
