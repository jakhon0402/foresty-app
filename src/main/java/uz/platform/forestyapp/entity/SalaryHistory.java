package uz.platform.forestyapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.platform.forestyapp.entity.template.AbsUUIDEntity;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SalaryHistory extends AbsUUIDEntity {
    @Column(nullable = false)
    private long salaryAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    private Employee employee;

    @Column(nullable = false)
    private Date fromDate;

    @Column(nullable = false)
    private Date toDate;
}
