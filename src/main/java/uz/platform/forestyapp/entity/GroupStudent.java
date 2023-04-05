package uz.platform.forestyapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.platform.forestyapp.entity.enums.StudentStatusName;
import uz.platform.forestyapp.entity.template.AbsUUIDEntity;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class GroupStudent extends AbsUUIDEntity {
    @ManyToOne(optional = false,fetch = FetchType.EAGER)
    private Group group;

    @ManyToOne(optional = false, fetch = FetchType.EAGER,cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    private Student student;

    @Column(nullable = false)
    private StudentStatusName status;

    @Column
    private ZonedDateTime leavedDate;
}
