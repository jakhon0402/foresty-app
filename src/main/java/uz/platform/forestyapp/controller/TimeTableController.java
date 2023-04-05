package uz.platform.forestyapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.platform.forestyapp.entity.User;
import uz.platform.forestyapp.payload.response.LessonTimeRes;
import uz.platform.forestyapp.payload.response.TimeTableRoomRes;
import uz.platform.forestyapp.repository.RoomRepo;
import uz.platform.forestyapp.security.CurrentUser;
import uz.platform.forestyapp.service.LessonTimeService;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/timetable")
public class TimeTableController {

    @Autowired
    LessonTimeService lessonTimeService;

    @Autowired
    RoomRepo roomRepo;

    @GetMapping("/today")
    public HttpEntity<?> getTodayTimeTable(@CurrentUser User user){
        ZoneId zoneId = ZoneId.of("Asia/Samarkand");
        List<LessonTimeRes> timeTable = lessonTimeService.getTimeTable(user, ZonedDateTime.now(zoneId));
        return ResponseEntity.ok().body(timeTable);
    }

    @GetMapping
    public HttpEntity<?> getCalendarData(@CurrentUser User user, @RequestParam("date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date){
        ZoneId zoneId = ZoneId.of("Asia/Samarkand");
        List<LessonTimeRes> timeTable = lessonTimeService.getTimeTable(user, ZonedDateTime.ofInstant(date.toInstant(), zoneId));

        return ResponseEntity.ok().body(timeTable);
    }
}
