package uz.platform.forestyapp.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Room extends AbsUUIDEntity {
    @Column(nullable = false)
    private long roomNumber;

    @Column
    private String roomName;

    @Column(nullable = false)
    private int floor;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private EducationCenter educationCenter;
}
