package uz.platform.forestyapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.platform.forestyapp.entity.enums.TeacherStatusName;
import uz.platform.forestyapp.entity.template.AbsUUIDEntity;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Teacher extends AbsUUIDEntity implements Serializable {
    @OneToOne(cascade = {CascadeType.REMOVE,CascadeType.PERSIST,CascadeType.MERGE},fetch = FetchType.LAZY)
    private User teacher;

    @Column(nullable = false)
    private TeacherStatusName status;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Subject> subjects;

    @JsonIgnore
    @OneToMany(mappedBy = "teacher",fetch = FetchType.EAGER)
    private List<Group> groups;

    @Column(nullable = false)
    private double rating;

    private Long monthlySalary;

    @ManyToOne(fetch = FetchType.LAZY)
    private EducationCenter educationCenter;

    @PreRemove @PreUpdate @PrePersist
    @PostLoad
    public void updateMonthlySalary() {
        if (groups != null) {
            long salary = 0;
            for (Group group : groups) {
                salary = salary + group.getPrice()*group.getStudentsCount()*group.getAgreement()/100;
            }
            monthlySalary = salary;
        }
    }
}
