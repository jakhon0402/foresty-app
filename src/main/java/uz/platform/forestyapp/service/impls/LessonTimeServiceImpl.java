package uz.platform.forestyapp.service.impls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.platform.forestyapp.entity.Group;
import uz.platform.forestyapp.entity.LessonTime;
import uz.platform.forestyapp.entity.User;
import uz.platform.forestyapp.entity.enums.GroupStatusName;
import uz.platform.forestyapp.entity.enums.MinuteName;
import uz.platform.forestyapp.entity.enums.WeekDayName;
import uz.platform.forestyapp.payload.response.LessonTimeRes;
import uz.platform.forestyapp.repository.LessonTimeRepo;
import uz.platform.forestyapp.repository.RoomRepo;
import uz.platform.forestyapp.service.LessonTimeService;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class LessonTimeServiceImpl implements LessonTimeService {

    @Autowired
    LessonTimeRepo lessonTimeRepo;


    @Override
    public List<LessonTimeRes> getTimeTable(User currentUser,ZonedDateTime date) {
        WeekDayName day = WeekDayName.values()[date.getDayOfWeek().getValue()-1];
        List<LessonTime> lessonTimes = lessonTimeRepo.findAllByDayAndGroupStatusNotAndGroupEducationCenterIdAndGroupStartDateBefore(day, GroupStatusName.COMPLETED, currentUser.getEducationCenter().getId(), date.plusHours(1));
        if(lessonTimes.isEmpty()) return null;
        List<LessonTimeRes> lessonTimeResList = new ArrayList<>();
        for (LessonTime lessonTime : lessonTimes) {
            boolean isCompleted = false;
            MinuteName minuteName = lessonTime.getToMinute();
            int minute = minuteName.equals(MinuteName.ZERO) ? 0 : minuteName.equals(MinuteName.FIFTEEN) ? 15 : minuteName.equals(MinuteName.HALF) ? 30 : minuteName.equals(MinuteName.FOURTYFIVE) ? 45 : 0;
            if(lessonTime.getToHour()<date.getHour()) isCompleted = true;
            else if(lessonTime.getToHour()==date.getHour()){
                if(minute<date.getMinute()) isCompleted = true;
            }
            Group group = lessonTime.getGroup();
            User user = group.getTeacher().getTeacher();
            LessonTimeRes lessonTimeRes = LessonTimeRes.builder()
                    .isCompleted(isCompleted)
                    .groupName(group.getName())
                    .subject(group.getSubject())
                    .roomName(String.valueOf(group.getRoom().getRoomNumber()))
                    .teacherFullName(user.getFirstName() + " " + user.getLastName())
                    .fromHour(lessonTime.getFromHour())
                    .fromMinute(lessonTime.getFromMinute())
                    .toHour(lessonTime.getToHour())
                    .toMinute(lessonTime.getToMinute())
                    .fromTime(lessonTime.getFromTime())
                    .toTime(lessonTime.getToTime())
                    .day(lessonTime.getDay())
                    .build();
            lessonTimeResList.add(lessonTimeRes);
        }
        return lessonTimeResList;
    }
}
