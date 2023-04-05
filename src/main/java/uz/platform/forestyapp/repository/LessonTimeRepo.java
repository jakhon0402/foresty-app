package uz.platform.forestyapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.platform.forestyapp.entity.LessonTime;
import uz.platform.forestyapp.entity.enums.GroupStatusName;
import uz.platform.forestyapp.entity.enums.WeekDayName;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public interface LessonTimeRepo extends JpaRepository<LessonTime, UUID> {
    List<LessonTime> findAllByGroupRoomIdAndDayAndGroupStatusNot(UUID roomId, WeekDayName day,GroupStatusName status);

    List<LessonTime> findAllByGroupRoomIdAndDayAndGroupIdNotAndGroupStatusNot(UUID roomId, WeekDayName day,UUID id,GroupStatusName status);

    void deleteAllByGroupId(UUID id);

    List<LessonTime> findAllByDayAndGroupStatusNotAndGroupEducationCenterIdAndGroupStartDateBefore(WeekDayName day, GroupStatusName status,UUID eduCenterId, ZonedDateTime now);
}
