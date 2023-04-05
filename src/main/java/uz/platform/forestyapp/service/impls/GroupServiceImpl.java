package uz.platform.forestyapp.service.impls;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uz.platform.forestyapp.entity.*;
import uz.platform.forestyapp.entity.enums.*;
import uz.platform.forestyapp.payload.ApiResponse;
import uz.platform.forestyapp.payload.GroupDto;
import uz.platform.forestyapp.payload.LessonTimeDto;
import uz.platform.forestyapp.repository.*;
import uz.platform.forestyapp.repository.studentPayment.GroupPaymentRepo;
import uz.platform.forestyapp.repository.studentPayment.StudentPaymentRepo;
import uz.platform.forestyapp.repository.teacherPayment.TeacherGroupPaymentRepo;
import uz.platform.forestyapp.repository.teacherPayment.TeacherPaymentRepo;
import uz.platform.forestyapp.service.GroupService;
import uz.platform.forestyapp.service.utils.ActivityService;
import uz.platform.forestyapp.service.utils.LessonTimeService;
import uz.platform.forestyapp.service.utils.PlanLimitService;
import uz.platform.forestyapp.service.utils.TeacherPaymentChangeService;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class GroupServiceImpl implements GroupService {

    @Autowired GroupRepo groupRepo;

    @Autowired SubjectRepo subjectRepo;

    @Autowired TeacherRepo teacherRepo;

    @Autowired RoomRepo roomRepo;

    @Autowired LessonTimeRepo lessonTimeRepo;

    @Autowired PlanLimitService planLimitService;

    @Autowired LessonTimeService lessonTimeService;

    @Autowired ActivityService activityService;

    @Autowired TeacherPaymentChangeService teacherPaymentChangeService;

    @Autowired StudentRepo studentRepo;

    @Autowired GroupStudentRepo groupStudentRepo;

    @Autowired StudentPaymentRepo studentPaymentRepo;

    @Autowired GroupPaymentRepo groupPaymentRepo;

    @Autowired TeacherPaymentRepo teacherPaymentRepo;

    @Autowired TeacherGroupPaymentRepo teacherGroupPaymentRepo;


    //---------------------------------
    // YANGI GURUH OCHISH
    // --------------------------------
    @Override
    public ApiResponse addGroup(GroupDto groupDto, User currentUser) {
        if(planLimitService.checkPlanLimit(currentUser.getEducationCenter().getId(), PlanLimitName.GROUP)){
            return new ApiResponse("Guruhlar soni limitdan kupayib ketdi!",false);
        }
        ZoneId zoneId = ZoneId.of("Asia/Samarkand");
        UUID educationCenterId = currentUser.getEducationCenter().getId();

        //GURUH DTO QIYMATLARINI TEKSHIRAMIZ!
        if(planLimitService.checkPlanLimit(educationCenterId, PlanLimitName.GROUP)){
            return new ApiResponse("Guruhlar soni limitdan oshib ketdi",false);
        }
        Optional<Subject> optionalSubject = subjectRepo.findById(groupDto.getSubjectId());
        if(optionalSubject.isEmpty()){
            return new ApiResponse("Bunday idlik fan mavjud emas!",false);
        }
        Subject subject = optionalSubject.get();
        if(subject.isPrivate()){
            if(!educationCenterId.equals(subject.getEducationCenter().getId())){
                return new ApiResponse("Ushbu fan joriy uquv markazga tegishli emas!",false);
            }
        }


        Optional<Teacher> optionalTeacher = teacherRepo.findById(groupDto.getTeacherId());
        if(optionalTeacher.isEmpty()){
            return new ApiResponse("Bunday idlik o'qituvchi mavjud emas!",false);
        }
        Teacher teacher = optionalTeacher.get();
        if(!educationCenterId.equals(teacher.getTeacher().getEducationCenter().getId())){
            return new ApiResponse("Ushbu o'qituvchi joriy uquv markazga tegishli emas!",false);
        }

        if(!teacher.getStatus().equals(TeacherStatusName.ACTIVE))
            return new ApiResponse("Ushbu o'qituvchi ACTIVE emas!",false);


        Optional<Room> optionalRoom = roomRepo.findById(groupDto.getRoomId());
        if(optionalRoom.isEmpty()){
            return new ApiResponse("Bunday idlik xona mavjud emas!",false);
        }
        Room room = optionalRoom.get();
        if(!educationCenterId.equals(room.getEducationCenter().getId())){
            return new ApiResponse("Ushbu xona joriy uquv markazga tegishli emas!",false);
        }

        if((groupDto.getAgreement()>100||groupDto.getAgreement()<=0)){
            return new ApiResponse("Kelishiv qiymati 0 va 100 oralig'ida bo'lishi kerak!",false);
        }

        if(groupDto.getLessonTimes().size()>5){
            return new ApiResponse("Dars kunlari 5 kundan oshmasligi kerak!",false);
        }
        ZonedDateTime groupStartsDate = ZonedDateTime.ofInstant(groupDto.getStartsDate().toInstant(),zoneId);
        if(ZonedDateTime.now(zoneId).isAfter(groupStartsDate)){
            return new ApiResponse("Darsning boshlanish vaqti noto'g'ri kiritildi!",false);
        }

        if(groupDto.getTestDayCount()<=0||groupDto.getTestDayCount()>30){
            return new ApiResponse("Guruh sinov kun muddati xato kiritildi!",false);
        }
        if(groupDto.getDuration()!=null) {
            if (groupDto.getDuration() == 0 || groupDto.getDuration() > 12 || groupDto.getDuration()<-1) {
                return new ApiResponse("Guruh davomiyligi xato kiritildi!", false);
            }
        }

        if(groupDto.getLessonTimes()==null||groupDto.getLessonTimes().size()==0){
            return new ApiResponse("Guruh dars vaqtlari bo'sh bo'lmasligi kerak!", false);
        }

        //-------------------------
        //GURUH OBYEKTINI SHAKLLANTIRAMIZ!
        //------------------------
        Group group = Group.builder()
                .name(groupDto.getName())
                .currentMonth(1)
                .subject(subject)
                .teacher(teacher)
                .room(room)
                .agreement(groupDto.getAgreement())
                .type(groupDto.getGroupType())
                .duration(groupDto.getDuration())
                .monthType(groupDto.getDuration()==-1? GroupMonthType.INDEFINITE:GroupMonthType.DEFINITE)
                .startDate(groupStartsDate)
                .paymentStartsDate(groupStartsDate.plusDays(groupDto.getTestDayCount().longValue()))
                .status(GroupStatusName.NEW)
                .price(groupDto.getPrice())
                .testDaysCount(Long.valueOf(groupDto.getTestDayCount()))
                .educationCenter(currentUser.getEducationCenter())
                .studentsCount(0L)
                .build();

        // GURUH DTO DAN DARS VAQTLARINI LISTGA JOYLAYMIZ!
        // VA HAR BIRINI VAQTINI TEKSHIRAMIZ .
        List<LessonTime> lessonTimes = new ArrayList<>();
        List<LessonTimeDto> lessonTimeDtos = groupDto.getLessonTimes();
        for (LessonTimeDto lessonTimeDto : lessonTimeDtos) {
            if(lessonTimeDto.getFromHour()> lessonTimeDto.getToHour()){
                return new ApiResponse("Boshlanish vaqti Tugash vaqtidan oldin kiritilgan!",false);
            }
            if((lessonTimeDto.getFromHour()<5&&lessonTimeDto.getFromHour()>23)&&(lessonTimeDto.getToHour()<5&&lessonTimeDto.getToHour()>23)){
                return new ApiResponse("Boshlanish va Tugash vaqt soatlari 5 va 23 oralig'ida emas!",false);
            }

            // HAFAT KUNI VA XONA BO'YICHA DARS VAQTLARINI AJRATIB OLIB LISTGA JOYLAYMIZ!
            List<LessonTime> lessonTimesByRoomAndDay = lessonTimeRepo.findAllByGroupRoomIdAndDayAndGroupStatusNot(room.getId(), WeekDayName.values()[lessonTimeDto.getDay()],GroupStatusName.COMPLETED);
            //AGAR LIST BO'SH BO'LSA DARS VAQTINI SAQLAB KEYINGISIGA O'TAMIZ!
            if(lessonTimesByRoomAndDay.size()==0){
                lessonTimes.add(lessonTimeService.saveLessonTime(lessonTimeDto,group));
                continue;
            }
            //AGAR DARS VAQTLARI MAVJUD BO'LSA ULARNI VAQTI BILAN TEKSHIRIB CHIQAMIZ!
            for (LessonTime lessonTime : lessonTimesByRoomAndDay) {
                double fromTime = lessonTimeService.getTime(lessonTimeDto.getFromHour(),MinuteName.values()[lessonTimeDto.getFromMinute()]);
                double toTime = lessonTimeService.getTime(lessonTimeDto.getToHour(),MinuteName.values()[lessonTimeDto.getToMinute()]);
                if((fromTime>=lessonTime.getFromTime()&&fromTime<lessonTime.getToTime()) ||
                        toTime>=lessonTime.getFromTime()&&toTime<lessonTime.getToTime() || (fromTime<lessonTime.getFromTime()&&toTime>lessonTime.getToTime())){
                    return new ApiResponse(lessonTime.getDay().uz+" kuni "+lessonTime.getFromHour() + ":"+lessonTime.getFromMinute().minute + " dan " +lessonTime.getToHour() + ":"+lessonTime.getToMinute().minute + " gacha " + room.getRoomNumber()+" xonada " + lessonTime.getGroup().getName() + " guruhning darsi mavjud!",false);
                }
                lessonTimes.add(lessonTimeService.saveLessonTime(lessonTimeDto,group));
            }
        }
        groupRepo.save(group);
        lessonTimeRepo.saveAll(lessonTimes);
        String groupName = group.getName();
        activityService.addActivity(groupName,"", ActivityName.ADD_GROUP,currentUser);
        return new ApiResponse("Yangi guruh saqlandi!",true);
    }

    //---------------------------------
    // GURUHNI TAHRIRLASH
    // --------------------------------
    @Override
    public ApiResponse editGroup(UUID groupId, GroupDto groupDto, User currentUser) {
        ZoneId zoneId = ZoneId.of("Asia/Samarkand");
        Optional<Group> optionalGroup = groupRepo.findById(groupId);

        if(optionalGroup.isEmpty()){
            return new ApiResponse("Bunday idlik guruh tizimda mavjud emas!",false);
        }
        Group group = optionalGroup.get();
        UUID educationCenterId = group.getEducationCenter().getId();
        if(!group.getEducationCenter().getId().equals(currentUser.getEducationCenter().getId())){
            return new ApiResponse("Ushbu guruh joriy o'quv markazga tegishli emas!",false);
        }

        if(groupDto.getLessonTimes()!=null){
            if(groupDto.getLessonTimes().size()>5){
                return new ApiResponse("Dars kunlari 5 kundan oshmasligi kerak!",false);
            }
        }

        if(groupDto.getStartsDate()!=null){
            ZonedDateTime groupStartsDate = ZonedDateTime.ofInstant(groupDto.getStartsDate().toInstant(),zoneId);
            if(ZonedDateTime.now(zoneId).isAfter(groupStartsDate)){
                return new ApiResponse("Darsning boshlanish vaqti noto'g'ri kiritildi!",false);
            }
            if(ZonedDateTime.now().isAfter(group.getStartDate())){
                return new ApiResponse("Dars boshlanish sanasi vaqti o'tib bo'lgan!",false);
            }
            group.setStartDate(ZonedDateTime.ofInstant(groupDto.getStartsDate().toInstant(),zoneId));
        }
        if(groupDto.getTestDayCount()!=null){
            if(groupDto.getTestDayCount()<=0&&groupDto.getTestDayCount()>30){
                return new ApiResponse("Guruh sinov kun muddati xato kiritildi!",false);
            }
            if (ZonedDateTime.now().isAfter(group.getStartDate().plusDays(group.getTestDaysCount()))) {
                return new ApiResponse("Sinov muddati vaqti o'tib bo'lgan!", false);
            }
            group.setTestDaysCount(Long.valueOf(groupDto.getTestDayCount()));

        }
        if(groupDto.getAgreement()!=null){
            if(groupDto.getAgreement()>100||groupDto.getAgreement()<=0){
                return new ApiResponse("Kelishiv qiymati 0 va 100 oralig'ida bo'lishi kerak!",false);
            }
            if (ZonedDateTime.now().isAfter(group.getStartDate().plusDays(group.getTestDaysCount()))) {
                return new ApiResponse("Sinov muddati vaqti o'tib bo'lgan!", false);
            }
            group.setAgreement(groupDto.getAgreement());

        }
        if(groupDto.getDuration()!=null){
            if (groupDto.getDuration() == 0 || groupDto.getDuration() > 12 || groupDto.getDuration()<-1) {
                return new ApiResponse("Guruh davomiyligi xato kiritildi!", false);
            }
            if (ZonedDateTime.now().isAfter(group.getStartDate().plusDays(group.getTestDaysCount()))) {
                return new ApiResponse("Sinov muddati vaqti o'tib bo'lgan!", false);
            }
            group.setDuration(groupDto.getDuration());
            group.setMonthType(groupDto.getDuration()==-1? GroupMonthType.INDEFINITE:GroupMonthType.DEFINITE);
        }

        Optional<Subject> optionalSubject = subjectRepo.findById(groupDto.getSubjectId());
        if(optionalSubject.isEmpty()){
            return new ApiResponse("Bunday idlik fan mavjud emas!",false);
        }
        Subject subject = optionalSubject.get();
        if(subject.isPrivate()){
            if(!educationCenterId.equals(subject.getEducationCenter().getId())){
                return new ApiResponse("Ushbu fan joriy uquv markazga tegishli emas!",false);
            }
        }


        Optional<Teacher> optionalTeacher = teacherRepo.findById(groupDto.getTeacherId());
        if(optionalTeacher.isEmpty()){
            return new ApiResponse("Bunday idlik o'qituvchi mavjud emas!",false);
        }
        Teacher teacher = optionalTeacher.get();
        if(!educationCenterId.equals(teacher.getTeacher().getEducationCenter().getId())){
            return new ApiResponse("Ushbu o'qituvchi joriy uquv markazga tegishli emas!",false);
        }

        if(!teacher.getStatus().equals(TeacherStatusName.ACTIVE))
            return new ApiResponse("Ushbu o'qituvchi ACTIVE emas!",false);


        Optional<Room> optionalRoom = roomRepo.findById(groupDto.getRoomId());
        if(optionalRoom.isEmpty()){
            return new ApiResponse("Bunday idlik xona mavjud emas!",false);
        }
        Room room = optionalRoom.get();
        if(!educationCenterId.equals(room.getEducationCenter().getId())){
            return new ApiResponse("Ushbu xona joriy uquv markazga tegishli emas!",false);
        }

        if(groupDto.getLessonTimes()!=null||!room.getId().equals(group.getRoom().getId())){
            List<LessonTime> lessonTimes = new ArrayList<>();
            List<LessonTimeDto> lessonTimeDtos = groupDto.getLessonTimes();
            for (LessonTimeDto lessonTimeDto : lessonTimeDtos) {
                if(lessonTimeDto.getFromHour()> lessonTimeDto.getToHour()){
                    return new ApiResponse("Boshlanish vaqti Tugash vaqtidan oldin kiritilgan!",false);
                }
                if((lessonTimeDto.getFromHour()<5&&lessonTimeDto.getFromHour()>23)&&(lessonTimeDto.getToHour()<5&&lessonTimeDto.getToHour()>23)){
                    return new ApiResponse("Boshlanish va Tugash vaqt soatlari 5 va 23 oralig'ida emas!",false);
                }

                // HAFAT KUNI VA XONA BO'YICHA DARS VAQTLARINI AJRATIB OLIB LISTGA JOYLAYMIZ!
                List<LessonTime> lessonTimesByRoomAndDay = lessonTimeRepo.findAllByGroupRoomIdAndDayAndGroupIdNotAndGroupStatusNot(room.getId(), WeekDayName.values()[lessonTimeDto.getDay()],group.getId(),GroupStatusName.COMPLETED);
                //AGAR LIST BO'SH BO'LSA DARS VAQTINI SAQLAB KEYINGISIGA O'TAMIZ!
                if(lessonTimesByRoomAndDay.size()==0){
                    lessonTimes.add(lessonTimeService.saveLessonTime(lessonTimeDto,group));
                    continue;
                }
                //AGAR DARS VAQTLARI MAVJUD BO'LSA ULARNI VAQTI BILAN TEKSHIRIB CHIQAMIZ!
                for (LessonTime lessonTime : lessonTimesByRoomAndDay) {
                    double fromTime = lessonTimeService.getTime(lessonTimeDto.getFromHour(),MinuteName.values()[lessonTimeDto.getFromMinute()]);
                    double toTime = lessonTimeService.getTime(lessonTimeDto.getToHour(),MinuteName.values()[lessonTimeDto.getToMinute()]);
                    if((fromTime>=lessonTime.getFromTime()&&fromTime<lessonTime.getToTime()) ||
                            toTime>=lessonTime.getFromTime()&&toTime<lessonTime.getToTime() || (fromTime<lessonTime.getFromTime()&&toTime>lessonTime.getToTime())){
                        return new ApiResponse(lessonTime.getDay().uz+" kuni "+lessonTime.getFromHour() + ":"+lessonTime.getFromMinute().minute + " dan " +lessonTime.getToHour() + ":"+lessonTime.getToMinute().minute + " gacha " + room.getRoomNumber()+" xonada " + lessonTime.getGroup().getName() + " guruhning darsi mavjud!",false);
                    }
                    lessonTimes.add(lessonTimeService.saveLessonTime(lessonTimeDto,group));
                }
            }
            lessonTimeRepo.deleteAllByGroupId(group.getId());
            lessonTimeRepo.saveAll(lessonTimes);
        }

        group.setName(groupDto.getName());
        group.setType(groupDto.getGroupType());
        group.setSubject(subject);
        group.setTeacher(teacher);
        group.setRoom(room);
        group.setPrice(groupDto.getPrice());
        groupRepo.save(group);
        String groupName = group.getName();
        activityService.addActivity(groupName,"", ActivityName.EDIT_GROUP,currentUser);
        return new ApiResponse("Guruh tahrirlandi!",true);
    }

    //---------------------------------
    // GURUHNI O'CHIRISH
    // --------------------------------
    @Override
    public ApiResponse deleteGroup(UUID groupId, User currentUser) {
        ZoneId zoneId = ZoneId.of("Asia/Samarkand");

        Optional<Group> optionalGroup = groupRepo.findById(groupId);
        if(optionalGroup.isEmpty()) return new ApiResponse("Bunday idlik guruh mavjud emas!",false);
        Group group = optionalGroup.get();
        if(!group.getEducationCenter().getId().equals(currentUser.getEducationCenter().getId())) return new ApiResponse("Bunday idlik guruh joriy uquv markazga tegishli emas!",false);

        if(group.getGroupStudents().size()>0&&ZonedDateTime.now(zoneId).isAfter(group.getPaymentStartsDate())){
            studentPaymentRepo.deleteAllByGroupPaymentGroupId(group.getId());
            groupPaymentRepo.deleteAllByGroupId(group.getId());
            teacherPaymentRepo.deleteAllByTeacherGroupPaymentGroupId(group.getId());
            teacherGroupPaymentRepo.deleteByGroupId(group.getId());
        }
        lessonTimeRepo.deleteAllByGroupId(group.getId());
        groupStudentRepo.deleteAllByGroupId(group.getId());
        groupRepo.delete(group);
        String groupName = group.getName();
        activityService.addActivity(groupName,"", ActivityName.DELETE_GROUP,currentUser);
        return new ApiResponse("Guruh o'chirildi!",true);
    }


    // -----------------------
    // GURUHGA TALABA QO'SHISH
    // -----------------------
    @Override
    public ApiResponse addStudentToGroup(UUID groupId, UUID studentId, User currentUser) {

        //GURUH NI TEKSHIRIB CHIQAMIZ
        Optional<Group> optionalGroup = groupRepo.findById(groupId);
        if(optionalGroup.isEmpty()) return new ApiResponse("Bunday idlik guruh mavjud emas!",false);
        Group group = optionalGroup.get();
        if(!group.getEducationCenter().getId().equals(currentUser.getEducationCenter().getId())) return new ApiResponse("Bunday idlik guruh joriy uquv markazga tegishli emas!",false);
        if(!(group.getStatus().equals(GroupStatusName.ACTIVE)||group.getStatus().equals(GroupStatusName.NEW))) return new ApiResponse("Guruh invalid!",false);

        // STUDENT NI TEKSHIRIB CHIQAMIZ
        Optional<Student> optionalStudent = studentRepo.findById(studentId);
        if(optionalStudent.isEmpty()) return new ApiResponse("Bunday idlik talaba mavjud emas!",false);
        Student student = optionalStudent.get();
        if(!student.getStudent().getEducationCenter().getId().equals(currentUser.getEducationCenter().getId())) return new ApiResponse("Bunday idlik talaba joriy uquv markazga tegishli emas!",false);
        if(!(student.getStatus().equals(StudentStatusName.ACTIVE)||student.getStatus().equals(StudentStatusName.NEW))) return new ApiResponse("Student invalid!",false);

        // AGAR GURUHDA USHBU IDLIK TALABA BO'LSA
        Optional<GroupStudent> optionalGroupStudent = groupStudentRepo.findByGroupIdAndStudentId(group.getId(), student.getId());
        if(optionalGroupStudent.isPresent()){
            return new ApiResponse("Ushbu talaba allaqachon guruhga qo'shilgan!",false);
        }

        // GURUH TALABASI OBYEKTINI OCHAMIZ
        GroupStudent groupStudent = GroupStudent.builder()
                .student(optionalStudent.get())
                .group(optionalGroup.get())
                .build();

        // AGAR GURUH ACTIVE HOLATDA BULSA
        if(group.getStatus().equals(GroupStatusName.ACTIVE)){
            groupStudent.setStatus(StudentStatusName.ACTIVE);
            student.setStatus(StudentStatusName.ACTIVE);
            studentRepo.save(student);
            groupStudentRepo.save(groupStudent);
            if(group.getMonthType().equals(GroupMonthType.DEFINITE)){
                List<StudentPayment> studentPayments = new ArrayList<>();
                List<TeacherPayment> teacherPayments = teacherPaymentRepo.findAllByTeacherGroupPaymentGroupIdAndMonthBetweenOrderByMonthAsc(groupId,group.getCurrentMonth()+1, group.getDuration());
                for(int i=group.getCurrentMonth();i<= group.getDuration();i++){
                    Optional<GroupPayment> optionalGroupPayment = groupPaymentRepo.findByMonthAndGroupId(i, group.getId());
                    StudentPayment studentPayment = StudentPayment.builder()
                            .studentId(student.getId())
                            .phoneNumber(student.getStudent().getPhoneNumber())
                            .studentFullName(student.getStudent().getFirstName()+" "+student.getStudent().getLastName())
                            .studentStatus(student.getStatus())
                            .status(PaymentStatusName.PROCESS)
                            .groupPayment(optionalGroupPayment.get())
                            .paymentAmount(group.getPrice())
                            .paidAmount(0)
                            .debtor(group.getPrice())
                            .build();
                    studentPayments.add(studentPayment);
                    if(i<=group.getCurrentMonth()) continue;
                    teacherPaymentChangeService.changeTeacherPayment(teacherPayments.get(i-group.getCurrentMonth()-1), group.getAgreement(),group.getPrice(), (long) group.getGroupStudents().size()+1);
                }
                teacherPaymentRepo.saveAll(teacherPayments);
                studentPaymentRepo.saveAll(studentPayments);
            }
            else {
                Optional<GroupPayment> optionalGroupPayment = groupPaymentRepo.findByMonthAndGroupId(group.getCurrentMonth(), group.getId());
                StudentPayment studentPayment = StudentPayment.builder()
                        .studentId(student.getId())
                        .phoneNumber(student.getStudent().getPhoneNumber())
                        .studentFullName(student.getStudent().getFirstName()+" "+student.getStudent().getLastName())
                        .studentStatus(student.getStatus())
                        .status(PaymentStatusName.PROCESS)
                        .groupPayment(optionalGroupPayment.get())
                        .paymentAmount(group.getPrice())
                        .paidAmount(0)
                        .debtor(group.getPrice())
                        .build();
                studentPaymentRepo.save(studentPayment);
            }
        }

        // AGAR GURUH NEW HOLATDA BULSA
        else if(group.getStatus().equals(GroupStatusName.NEW)){
            groupStudent.setStatus(StudentStatusName.NEW);
            groupStudentRepo.save(groupStudent);
        }
        group.setStudentsCount((long) group.getGroupStudents().size()+1);
        groupRepo.save(group);
        // ACTIVITY NI QUSHAMIZ
        String groupName = group.getName();
        String studentName = student.getStudent().getFirstName() + " " + student.getStudent().getLastName();
        activityService.addActivity(groupName,studentName,"", ActivityName.ADD_STUDENT_TO_GROUP,currentUser);

        return new ApiResponse("Guruhga talaba qo'shildi!",true);
    }

    //---------------------------------
    // TALABANI GURUHDAN CHETLASHTIRISH
    // --------------------------------
    @Override
    public ApiResponse removeStudentFromGroup(UUID groupStudentId, User currentUser) {
        Optional<GroupStudent> optionalGroupStudent = groupStudentRepo.findById(groupStudentId);
        if(optionalGroupStudent.isEmpty()) return new ApiResponse("Bunday idlik guruh talabasi mavjud emas!",false);
        GroupStudent groupStudent = optionalGroupStudent.get();
        if(!(groupStudent.getStatus().equals(StudentStatusName.ACTIVE)||groupStudent.getStatus().equals(StudentStatusName.NEW))) return new ApiResponse("Invalid GROUP_STUDENT",false);
        if(!(groupStudent.getGroup().getStatus().equals(GroupStatusName.ACTIVE)||groupStudent.getGroup().getStatus().equals(GroupStatusName.NEW))) return new ApiResponse("Invalid GROUP",false);

        if(!groupStudent.getGroup().getEducationCenter().getId().equals(currentUser.getEducationCenter().getId())) return new ApiResponse("Ushun student group joriy o'quv markazga tegishli emas!",false);

        Group group = groupStudent.getGroup();
        if(groupStudent.getStatus().equals(StudentStatusName.ACTIVE)){
            Student student = groupStudent.getStudent();
            if(group.getCurrentMonth()>1&&group.getCurrentMonth()<=group.getDuration()){
                List<StudentPayment> lastMonthStudentPayment  = studentPaymentRepo.findAllByGroupPaymentGroupIdAndStudentIdAndGroupPaymentMonthBetweenOrderByGroupPaymentMonthDesc(group.getId(), student.getId(),1,group.getCurrentMonth());
                for(int i=0;i<lastMonthStudentPayment.size();i++){
                    lastMonthStudentPayment.get(i).setStudentStatus(StudentStatusName.LEAVED);
                }
                studentPaymentRepo.saveAll(lastMonthStudentPayment);
            }
            List<TeacherPayment> teacherPayments = teacherPaymentRepo.findAllByTeacherGroupPaymentGroupIdAndMonthBetweenOrderByMonthAsc(group.getId(), group.getCurrentMonth() + 1, group.getDuration());

            if(group.getMonthType().equals(GroupMonthType.DEFINITE)){
                for(int i=group.getCurrentMonth();i<=group.getDuration();i++){
                    if(i<=group.getCurrentMonth()) continue;
                    teacherPaymentChangeService.changeTeacherPayment(teacherPayments.get(i-group.getCurrentMonth()-1), group.getAgreement(), group.getPrice(),(long) group.getGroupStudents().size()-1);
                }
                teacherPaymentRepo.saveAll(teacherPayments);
                studentPaymentRepo.deleteAllByGroupPaymentGroupIdAndStudentIdAndGroupPaymentMonthBetween(group.getId(), student.getId(),group.getCurrentMonth()+1,group.getDuration());
            }

        }
        group.setStudentsCount((long) group.getGroupStudents().size()-1);
        groupRepo.save(group);
        groupStudentRepo.delete(groupStudent);

        String groupName = group.getName();
        String studentName = groupStudent.getStudent().getStudent().getFirstName() + " " + groupStudent.getStudent().getStudent().getLastName();
        activityService.addActivity(groupName,studentName,"", ActivityName.REMOVE_STUDENT_FROM_GROUP,currentUser);
        return new ApiResponse("Talaba guruhdan chetlashtirildi!",true);
    }

    //---------------------------------
    // GURUHNI YOPISH
    // --------------------------------
    @Override
    public ApiResponse completeGroup(UUID groupId, User currentUser) {
        Optional<Group> optionalGroup = groupRepo.findById(groupId);
        if(optionalGroup.isEmpty()) return new ApiResponse("Bunday idlik guruh mavjud emas!",false);
        Group group = optionalGroup.get();
        if(!group.getEducationCenter().getId().equals(currentUser.getEducationCenter().getId())) return new ApiResponse("Bunday idlik guruh joriy uquv markazga tegishli emas!",false);
        if(group.getStatus().equals(GroupStatusName.NEW)||group.getStatus().equals(GroupStatusName.COMPLETED)) return new ApiResponse("Guruh invalid!",false);
        if(group.getMonthType().equals(GroupMonthType.DEFINITE)) return new ApiResponse("Guruh davomiyligi mavjud!",false);

        group.setStatus(GroupStatusName.FINISH);
        groupRepo.save(group);

        String groupName = group.getName();
        activityService.addActivity(groupName,"", ActivityName.FINISH_GROUP,currentUser);
        return new ApiResponse("Guruh yakunlandi!",true);
    }

    @Override
    public List<Group> getGroups(User user) {
        List<Group> groupList = groupRepo.findAllByEducationCenterId(user.getEducationCenter().getId());
        for (Group group : groupList) {
            group.setStudentsCount( group.getStudentsCount());
        }
        return groupList;
    }

    @Override
    public ApiResponse getGroup(User currentUser, UUID id) {
        Optional<Group> optionalGroup = groupRepo.findById(id);
        if(optionalGroup.isEmpty()) return new ApiResponse("Bunday idlik guruh mavjud emas!",false);
        Group group = optionalGroup.get();
        if(!group.getEducationCenter().getId().equals(currentUser.getEducationCenter().getId())) return new ApiResponse("Ushbu guruh joriy o'quv markazga tegishli emas!",false);
        group.setStudentsCount(group.getStudentsCount());
        return new ApiResponse(group,true);
    }

    @Override
    public ApiResponse getStudents(User user, UUID id) {
        Optional<Group> optionalGroup = groupRepo.findById(id);
        if(optionalGroup.isEmpty()) return new ApiResponse("Bunday idlik guruh mavjud emas!",false);
        Group group = optionalGroup.get();
        if(!group.getEducationCenter().getId().equals(user.getEducationCenter().getId())) return new ApiResponse("Ushbu guruh joriy o'quv markazga tegishli emas!",false);
        List<GroupStudent> groupStudents = groupStudentRepo.findAllByGroupId(group.getId());
//        List<Group> groupList = new ArrayList<>();
//        for (GroupStudent groupStudent : groupStudents) {
//            groupList.add(groupStudent.getGroup());
//        }
        return new ApiResponse(groupStudents,true);
    }

    @Override
    public ApiResponse getTopGroups(User user) {
        List<Object> topGroups = groupRepo.findTopGroupsByEducationCenterIdQuery(user.getEducationCenter().getId(), PageRequest.of(0, 3));
        return new ApiResponse(topGroups,true);
    }
}
