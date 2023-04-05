package uz.platform.forestyapp.service;

import uz.platform.forestyapp.entity.User;
import uz.platform.forestyapp.payload.response.LessonTimeRes;

import java.time.ZonedDateTime;
import java.util.List;

public interface LessonTimeService {
    List<LessonTimeRes> getTimeTable(User currentUser, ZonedDateTime date);
}
