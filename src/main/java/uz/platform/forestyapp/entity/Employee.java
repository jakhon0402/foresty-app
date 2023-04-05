package uz.platform.forestyapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.platform.forestyapp.entity.enums.EmployeeStatusName;
import uz.platform.forestyapp.entity.template.AbsUUIDEntity;

import java.time.ZonedDateTime;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Employee extends AbsUUIDEntity {
    @OneToOne(cascade = {CascadeType.REMOVE,CascadeType.PERSIST,CascadeType.MERGE})
    private User employee;

    @Column(nullable = false)
    private long currentSalary;

    @Column(nullable = false)
    private EmployeeStatusName status;

    @Column(nullable = false)
    private Integer testDaysCount;

    @Column(nullable = false)
    private ZonedDateTime jobStartsDate;

    @Column(nullable = false)
    private Long workedMonthCount;

    @OneToOne
    private Attachment profilePhoto;

    @JsonIgnore
    @OneToMany(cascade = {CascadeType.REMOVE},fetch = FetchType.LAZY,mappedBy = "employee")
    private List<EmployeePayment> employeePayments;
}
