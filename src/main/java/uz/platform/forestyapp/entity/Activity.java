package uz.platform.forestyapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.platform.forestyapp.entity.enums.ActivityName;
import uz.platform.forestyapp.entity.template.AbsUUIDEntity;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Activity extends AbsUUIDEntity {
    @Column(nullable = false)
    private String context;

    @Column
    private String secondaryContext;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private ActivityName activityName;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}
