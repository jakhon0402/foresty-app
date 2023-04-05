package uz.platform.forestyapp.payload.response;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.platform.forestyapp.entity.Subject;
import uz.platform.forestyapp.entity.enums.MinuteName;
import uz.platform.forestyapp.entity.enums.WeekDayName;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LessonTimeRes {

    private String groupName;

    private String teacherFullName;

    private Subject subject;

    private String roomName;

    private WeekDayName day;

    private Integer fromHour;

    private MinuteName fromMinute;

    private Integer toHour;

    private MinuteName toMinute;

    private double fromTime;

    private double toTime;

    private boolean isCompleted;

}
