package uz.platform.forestyapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.platform.forestyapp.entity.Subject;
import uz.platform.forestyapp.entity.enums.ColorName;
import uz.platform.forestyapp.entity.enums.WeekDayName;

import java.sql.Date;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/test")
public class TestController {


    @PostMapping
    public String postTest(){
        return "hola";
    }

    public static void main(String[] args) {
//        System.out.println(patternMatches("jakhon99dev@mail.com","^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
//                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$"));
//        ZoneId zoneId = ZoneId.of("Asia/Tashkent");
//        ZoneId zoneId1 = ZoneId.of("Asia/Samarkand");
//
//        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
//        ZonedDateTime zonedDateTime1 = ZonedDateTime.now(zoneId1);
//
//        System.out.println(zonedDateTime.isAfter(zonedDateTime1));
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Samarkand")).plusDays(5);
        System.out.println(now.getDayOfWeek().getValue());
        System.out.println(WeekDayName.values()[now.getDayOfWeek().getValue()-1]);
    }

    public static boolean patternMatches(String emailAddress, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }
}
