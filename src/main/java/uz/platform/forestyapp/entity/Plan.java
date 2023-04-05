package uz.platform.forestyapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.platform.forestyapp.entity.template.AbsUUIDEntity;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Plan extends AbsUUIDEntity {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private Long employeesLimit;

    @Column(nullable = false)
    private Long teachersLimit;

    @Column(nullable = false)
    private Long studentsLimit;

    @Column(nullable = false)
    private Long groupsLimit;

    @Column(nullable = false)
    private Long subjectsLimit;

    @Column(nullable = false)
    private Long roomsLimit;

}
