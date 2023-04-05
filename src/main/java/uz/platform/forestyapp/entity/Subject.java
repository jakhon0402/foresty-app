package uz.platform.forestyapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.platform.forestyapp.entity.enums.ColorName;
import uz.platform.forestyapp.entity.template.AbsUUIDEntity;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Subject extends AbsUUIDEntity {
    @Column(nullable = false,length = 30)
    private String name;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private ColorName color;

//    @JsonIgnore
//    @OneToMany(fetch = FetchType.LAZY,mappedBy = "subject")
//    private List<Group> groups;

    @Column(nullable = false)
    private boolean isPrivate;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private EducationCenter educationCenter;
}
