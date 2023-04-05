package uz.platform.forestyapp.service.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.platform.forestyapp.entity.Group;
import uz.platform.forestyapp.entity.LessonTime;
import uz.platform.forestyapp.entity.enums.MinuteName;
import uz.platform.forestyapp.entity.enums.WeekDayName;
import uz.platform.forestyapp.payload.LessonTimeDto;
import uz.platform.forestyapp.repository.LessonTimeRepo;

@Service
public class LessonTimeService {


    public LessonTime saveLessonTime(LessonTimeDto lessonTimeDto, Group group){

        return LessonTime.builder()
                .day(WeekDayName.values()[lessonTimeDto.getDay()])
                .fromHour(lessonTimeDto.getFromHour())
                .fromMinute(MinuteName.values()[lessonTimeDto.getFromMinute()])
                .toHour(lessonTimeDto.getToHour())
                .toMinute(MinuteName.values()[lessonTimeDto.getToMinute()])
                .fromTime(getTime(lessonTimeDto.getFromHour(),MinuteName.values()[lessonTimeDto.getFromMinute()]))
                .toTime(getTime(lessonTimeDto.getToHour(), MinuteName.values()[lessonTimeDto.getToMinute()]))
                .group(group)
                .build();
    }

    public double getTime(Integer hour, MinuteName minute){
        double tempMinute = 0;

        if(minute.equals(MinuteName.ZERO)) tempMinute=0;
        else if(minute.equals(MinuteName.FIFTEEN)) tempMinute=0.15;
        else if(minute.equals(MinuteName.HALF)) tempMinute=0.5;
        else if(minute.equals(MinuteName.FOURTYFIVE)) tempMinute=0.75;

        return hour + tempMinute;
    }
}
