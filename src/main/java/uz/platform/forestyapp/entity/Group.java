package uz.platform.forestyapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.platform.forestyapp.entity.enums.GroupMonthType;
import uz.platform.forestyapp.entity.enums.GroupStatusName;
import uz.platform.forestyapp.entity.enums.GroupTypeName;
import uz.platform.forestyapp.entity.enums.StudentStatusName;
import uz.platform.forestyapp.entity.template.AbsUUIDEntity;

import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "groups")
public class Group extends AbsUUIDEntity {
    @Column(nullable = false,length = 50)
    private String name;

    @Column(nullable = false)
    private GroupTypeName type;

    @Column(nullable = false)
    private Integer currentMonth;

    @Column(nullable = false)
    private GroupStatusName status;

    @ManyToOne(fetch = FetchType.LAZY)
    private Teacher teacher;

    @Column(nullable = false)
    private Integer agreement;

    @ManyToOne(fetch = FetchType.LAZY)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    private Room room;

    @Column(nullable = false)
    private ZonedDateTime startDate;

    @Column
    private Integer duration;

    @Column(nullable = false)
    private GroupMonthType monthType;

    @Column
    private Long testDaysCount;

    @Column(nullable = false)
    private ZonedDateTime paymentStartsDate;

    @Column(nullable = false)
    private Long price;

    @OneToMany(fetch = FetchType.EAGER,mappedBy = "group")
    private List<LessonTime> lessonTimes;

    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER,mappedBy = "group")
    private List<GroupStudent> groupStudents;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private EducationCenter educationCenter;

    @Column
    private Long studentsCount;

}
