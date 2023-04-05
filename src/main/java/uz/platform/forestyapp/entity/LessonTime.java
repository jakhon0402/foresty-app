package uz.platform.forestyapp.entity;

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
import uz.platform.forestyapp.entity.enums.MinuteName;
import uz.platform.forestyapp.entity.enums.WeekDayName;
import uz.platform.forestyapp.entity.template.AbsUUIDEntity;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class LessonTime extends AbsUUIDEntity {
    @Column(nullable = false)
    private WeekDayName day;

    @Column(nullable = false)
    private Integer fromHour;

    @Column(nullable = false)
    private MinuteName fromMinute;

    @Column(nullable = false)
    private Integer toHour;

    @Column(nullable = false)
    private MinuteName toMinute;

    @Column(nullable = false)
    private double fromTime;

    @Column(nullable = false)
    private double toTime;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Group group;
}
