package uz.platform.forestyapp.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TimeTableRoomRes {
    private long roomNumber;
    private List<LessonTimeRes> lessonTimes;
}
