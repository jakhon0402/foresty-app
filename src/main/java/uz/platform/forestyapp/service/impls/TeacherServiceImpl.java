package uz.platform.forestyapp.service.impls;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.platform.forestyapp.entity.*;
import uz.platform.forestyapp.entity.enums.*;
import uz.platform.forestyapp.payload.ApiResponse;
import uz.platform.forestyapp.payload.TeacherDto;
import uz.platform.forestyapp.payload.TeacherEditDto;
import uz.platform.forestyapp.payload.UserDto;
import uz.platform.forestyapp.repository.*;
import uz.platform.forestyapp.repository.teacherPayment.TeacherGroupPaymentRepo;
import uz.platform.forestyapp.repository.teacherPayment.TeacherPaymentRepo;
import uz.platform.forestyapp.service.TeacherService;
import uz.platform.forestyapp.service.utils.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Service
@Transactional
public class TeacherServiceImpl implements TeacherService {
    @Autowired TeacherRepo teacherRepo;

    @Autowired UserRepo userRepo;

    @Autowired RoleRepo roleRepo;

    @Autowired SubjectRepo subjectRepo;

    @Autowired UserService userService;

    @Autowired AddressService addressService;

    @Autowired PlanLimitService planLimitService;

    @Autowired ActivityService activityService;

    @Autowired
    EmailService emailService;

    @Autowired GroupRepo groupRepo;

    @Autowired
    TeacherGroupPaymentRepo teacherGroupPaymentRepo;

    @Autowired
    TeacherPaymentRepo teacherPaymentRepo;

    @Autowired PasswordEncoder passwordEncoder;

    @Override
    public ApiResponse addTeacher(TeacherDto teacherDto, User currentUser) {
        ZoneId zoneId = ZoneId.of("Asia/Samarkand");

        if(planLimitService.checkPlanLimit(currentUser.getEducationCenter().getId(), PlanLimitName.TEACHER)){
            return new ApiResponse("O'qituvchilar soni limitdan oshib ketdi!",false);
        }

        UserDto userDto = teacherDto.getTeacher();

        if(userRepo.existsByUsername(userDto.getUsername())){
            return new ApiResponse("Bunday usernamelik user tizimda mavjud!",false);
        }

        Optional<Role> userRole = roleRepo.findByRoleName(RoleName.USER);

        if(userRepo.existsByEmailAndRoleIsNot(userDto.getEmail(),userRole.get())){
            return new ApiResponse("Bunday emaillik user tizimda mavjud!",false);
        }
        Optional<Role> optionalRole = roleRepo.findByRoleName(RoleName.TEACHER);
        if(optionalRole.isEmpty()){
            return new ApiResponse("Bunday role tizimda mavjud emas!",false);
        }
        List<Subject> subjects = new ArrayList<>();
        for (UUID subjectId : teacherDto.getSubjectIds()) {
            Optional<Subject> optionalSubject = subjectRepo.findById(subjectId);
            if(optionalSubject.isEmpty()){
                return new ApiResponse("Bunday idlik fan tizimda mavjud emas!",false);
            }
            Subject subject = optionalSubject.get();
            if(subject.isPrivate()){
                if(!subject.getEducationCenter().getId().equals(currentUser.getEducationCenter().getId())){
                    return new ApiResponse("Ushbu fanlardan biri joriy uquv markazga tegishli emas",false);
                }
            }

            subjects.add(subject);
        }
        User user = userService.saveUser(userDto,optionalRole.get(),null,currentUser.getEducationCenter());
        Teacher teacher = Teacher.builder()
                .subjects(subjects)
                .status(TeacherStatusName.ACTIVE)
                .rating(0)
                .teacher(user)
                .build();
        teacherRepo.save(teacher);
        String teacherFullName = user.getFirstName() + " " + user.getLastName();
        activityService.addActivity(teacherFullName,"", ActivityName.ADD_TEACHER,currentUser);
//        emailService.sendEmailCode(user.getEmail(),user.getEmailCode(),user.getLastName(),user.getFirstName());
        return new ApiResponse("O'qituvchi saqlandi. O'qituvchi emailga yuborilgan kod orqali emailni tasdiqlab keyin tizimga kirishi mumkin!",true);
    }

    @Override
    public ApiResponse blockTeacher(UUID teacherId, UUID educationCenterId) {
        Optional<Teacher> optionalTeacher = teacherRepo.findById(teacherId);
        if(optionalTeacher.isEmpty()){
            return new ApiResponse("Bunday idlik o'qituvchi tizimda mavjud emas!",false);
        }
        Teacher teacher = optionalTeacher.get();
        User user = teacher.getTeacher();
        if(userService.checkUser(user,educationCenterId)){
            return new ApiResponse("Bunday idlik o'qituvchi joriy uquv markazga tegishli emas!",false);
        }
        teacher.setStatus(TeacherStatusName.BLOCKED);
        userService.blockUser(user);
        teacherRepo.save(teacher);
        return new ApiResponse("O'qituvchi bloklandi!",true);
    }

    @Override
    public ApiResponse deleteTeacher(UUID teacherId, User currentUser) {
        Optional<Teacher> optionalTeacher = teacherRepo.findById(teacherId);
        if(optionalTeacher.isEmpty()){
            return new ApiResponse("Bunday idlik o'qituvchi tizimda mavjud emas!",false);
        }
        Teacher teacher = optionalTeacher.get();
        User user = teacher.getTeacher();
        if(userService.checkUser(user,currentUser.getEducationCenter().getId())){
            return new ApiResponse("Bunday idlik o'qituvchi joriy uquv markazga tegishli emas!",false);
        }
        List<Group> groups = groupRepo.findAllByTeacherId(teacher.getId());
        if(groups.size()>0){
            return new ApiResponse("O'qituvchi guruhlarga biriktirilgan!",false);
        }
        teacherPaymentRepo.deleteAllByTeacherGroupPaymentGroupTeacherId(teacher.getId());
        teacherGroupPaymentRepo.deleteAllByGroupTeacherId(teacher.getId());
        teacherRepo.delete(teacher);
        String teacherFullName = user.getFirstName() + " " + user.getLastName();
        activityService.addActivity(teacherFullName,"", ActivityName.DELETE_TEACHER,currentUser);
        return new ApiResponse("O'qituvchi tizimdan o'chirildi!",true);
    }

    @Override
    public ApiResponse finishWork(UUID teacherId, User currentUser) {
        Optional<Teacher> optionalTeacher = teacherRepo.findById(teacherId);
        if(optionalTeacher.isEmpty()){
            return new ApiResponse("Bunday idlik o'qituvchi tizimda mavjud emas!",false);
        }
        Teacher teacher = optionalTeacher.get();
        User user = teacher.getTeacher();
        if(userService.checkUser(user,currentUser.getEducationCenter().getId())){
            return new ApiResponse("Bunday idlik o'qituvchi joriy uquv markazga tegishli emas!",false);
        }
        List<Group> groups = groupRepo.findAllByTeacherId(teacher.getId());

        if(groups.size()>0){
            List<Group> groups1 = groupRepo.findAllByTeacherIdAndStatus(teacher.getId(), GroupStatusName.COMPLETED);
            if(groups1.size()!=groups.size()) return new ApiResponse("O'qituvchi guruhlarga biriktirilgan!",false);
        }
        teacher.setStatus(TeacherStatusName.FINISHED);
        user.setAccountNonExpired(false);
        teacherRepo.save(teacher);
        String teacherFullName = user.getFirstName() + " " + user.getLastName();
        activityService.addActivity(teacherFullName,"", ActivityName.FINISH_TEACHER_WORK,currentUser);
        return new ApiResponse("O'qituvchining ishini yakunladik!",true);
    }

    @Override
    public List<Teacher> getTeachers(User currentUser) {
        return teacherRepo.findAllByTeacherEducationCenterId(currentUser.getEducationCenter().getId());
    }

    @Override
    public ApiResponse getTeacher(UUID id, User currentUser) {
        Optional<Teacher> optionalTeacher = teacherRepo.findById(id);
        if(optionalTeacher.isEmpty()) return new ApiResponse("Bunday idlik o'qituvchi mavjud emas!",false);
        Teacher teacher = optionalTeacher.get();
        if(!teacher.getTeacher().getEducationCenter().getId().equals(currentUser.getEducationCenter().getId())) return new ApiResponse("Ushbu o'qituchi joriy o'quv markazfa tegishli emas!",false);
        return new ApiResponse(teacher,true);
    }

    @Override
    public ApiResponse getTeacherGroups(User user, UUID id) {
        Optional<Teacher> optionalTeacher = teacherRepo.findById(id);
        if(optionalTeacher.isEmpty()) return new ApiResponse("Bunday idlik o'qituvchi mavjud emas!",false);
        Teacher teacher = optionalTeacher.get();
        if(!teacher.getTeacher().getEducationCenter().getId().equals(user.getEducationCenter().getId())) return new ApiResponse("Ushbu o'qituchi joriy o'quv markazfa tegishli emas!",false);
        List<Group> teacherGroups = groupRepo.findAllByTeacherId(teacher.getId());
        return new ApiResponse(teacherGroups,true);
    }

    @Override
    public ApiResponse editTeacher(TeacherEditDto teacherDto, User user, UUID id) {
        ZoneId zoneId = ZoneId.of("Asia/Samarkand");
        Optional<Teacher> optionalTeacher = teacherRepo.findById(id);
        if(optionalTeacher.isEmpty()) return new ApiResponse("Bunday idlik o'qituvchi mavjud emas!",false);
        Teacher teacher = optionalTeacher.get();
        if(!teacher.getTeacher().getEducationCenter().getId().equals(user.getEducationCenter().getId())) return new ApiResponse("Ushbu o'qituvchi joriy o'quv markazga tegishli emas!",false);
        User teacherUser = teacher.getTeacher();
        teacherUser.setFirstName(teacherDto.getFirstName());
        teacherUser.setLastName(teacherDto.getLastName());
        teacherUser.setFatherName(teacherDto.getFatherName());
        teacherUser.setPhoneNumber(teacherDto.getPhoneNumber());

        if(teacherDto.getSubjectIds()!=null){
            List<Subject> subjects = new ArrayList<>();
            for (UUID subjectId : teacherDto.getSubjectIds()) {
                Optional<Subject> optionalSubject = subjectRepo.findById(subjectId);
                if(optionalSubject.isEmpty()){
                    return new ApiResponse("Bunday idlik fan tizimda mavjud emas!",false);
                }
                Subject subject = optionalSubject.get();
                if(subject.isPrivate()){
                    if(!subject.getEducationCenter().getId().equals(user.getEducationCenter().getId())){
                        return new ApiResponse("Ushbu fanlardan biri joriy uquv markazga tegishli emas",false);
                    }
                }

                subjects.add(subject);
            }
            teacher.setSubjects(subjects);
        }


        int counter = 0;
        String password=null;
        String email=teacherUser.getEmail();
        String username=null;
        if(teacherDto.getNewPassword()!=null){
            password=teacherDto.getNewPassword();
        } else counter+=1;

        if(teacherDto.getEmail()!=null){
            email=teacherDto.getEmail();
            boolean existsByEmail = userRepo.existsByEmailAndRoleRoleNameIsNotAndIdNot(email, RoleName.USER, teacherUser.getId());
            if(existsByEmail) return new ApiResponse("Bunday emaillik user tizimda mavjud!",false);
        } else counter+=1;
        if(teacherDto.getUsername()!=null){
            username=teacherDto.getUsername();
            boolean existsByUsernameAndIdNot = userRepo.existsByUsernameAndIdNot(username,  teacherUser.getId());
            if(existsByUsernameAndIdNot) return new ApiResponse("Bunday usernamelik user tizimda mavjud!",false);
        } else counter+=1;

        if(counter==3){
            teacherRepo.save(teacher);
            return new ApiResponse("O'qituvchi ma'lumotlari o'zgartirildi!",true);
        }
        teacherUser.setEmail(email);
        String emailCode = emailService.getRandomNumberString();

        if(password==null&&username==null){
            teacherUser.setEmailCode(emailCode);
            teacherUser.setEmailCodeCreatedAt(ZonedDateTime.now(zoneId));
            teacherUser.setEnabled(false);
            try {
                emailService.sendEmailCode(email,emailCode,teacherUser.getLastName(),teacherUser.getFirstName());
            } catch (MessagingException e) {
                return new ApiResponse("Error",false);
            }
            teacherRepo.save(teacher);
            return new ApiResponse("O'qituvchi ma'lumotlari o'zgartirildi!",true);
        }
        else if(username==null){
            teacherUser.setPassword(passwordEncoder.encode(password));
            teacherUser.setEmailCode(emailCode);
            teacherUser.setEmailCodeCreatedAt(ZonedDateTime.now(zoneId));
            try {
                emailService.sendEmailPasswordChange(email,teacherUser.getLastName(),teacherUser.getFirstName(),password);
            } catch (MessagingException e) {
                return new ApiResponse("Error!",false);
            }
            teacherRepo.save(teacher);
            return new ApiResponse("O'qituvchi ma'lumotlari o'zgartirildi!",true);

        }else if(password==null){
            teacherUser.setUsername(username);
            teacherUser.setEmailCode(emailCode);
            teacherUser.setEmailCodeCreatedAt(ZonedDateTime.now(zoneId));
            try {
                emailService.sendEmailUsernameChange(email,teacherUser.getLastName(),teacherUser.getFirstName(),username);
            } catch (MessagingException e) {
                return new ApiResponse("Error!",false);
            }
            teacherRepo.save(teacher);
            return new ApiResponse("O'qituvchi ma'lumotlari o'zgartirildi!",true);

        } else{
            teacherUser.setPassword(passwordEncoder.encode(password));
            teacherUser.setUsername(username);
            teacherUser.setEmailCode(emailCode);
            teacherUser.setEmailCodeCreatedAt(ZonedDateTime.now(zoneId));
            teacherUser.setEnabled(false);
            try {
                emailService.sendEmailCodeFirstLogIn(email,emailCode,teacherUser.getLastName(),teacherUser.getFirstName(),username,password);
            } catch (MessagingException e) {
                return new ApiResponse("Error!",false);
            }
            teacherRepo.save(teacher);
            return new ApiResponse("O'qituvchi ma'lumotlari o'zgartirildi!",true);

        }
    }

    @Override
    public ApiResponse getTeacherPayments(User user, UUID id) {
        Optional<Teacher> optionalTeacher = teacherRepo.findById(id);
        if(optionalTeacher.isEmpty()) return new ApiResponse("Bunday idlik o'qituvchi mavjud emas!",false);
        Teacher teacher = optionalTeacher.get();
        if(!teacher.getTeacher().getEducationCenter().getId().equals(user.getEducationCenter().getId())) return new ApiResponse("Ushbu o'qituchi joriy o'quv markazfa tegishli emas!",false);
        List<TeacherPayment> teacherPayments = teacherPaymentRepo.findAllByTeacherGroupPaymentGroupTeacherIdAndStatusOrderByCreatedAt(teacher.getId(),PaymentStatusName.PAID);
        return new ApiResponse(teacherPayments,true);
    }
}
