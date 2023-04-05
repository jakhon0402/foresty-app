package uz.platform.forestyapp.service.impls;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.platform.forestyapp.entity.*;
import uz.platform.forestyapp.entity.enums.*;
import uz.platform.forestyapp.payload.ApiResponse;
import uz.platform.forestyapp.payload.StudentDto;
import uz.platform.forestyapp.payload.response.StudentRes;
import uz.platform.forestyapp.repository.*;
import uz.platform.forestyapp.repository.studentPayment.GroupPaymentRepo;
import uz.platform.forestyapp.repository.studentPayment.StudentPaymentRepo;
import uz.platform.forestyapp.repository.teacherPayment.TeacherGroupPaymentRepo;
import uz.platform.forestyapp.repository.teacherPayment.TeacherPaymentRepo;
import uz.platform.forestyapp.service.StudentService;
import uz.platform.forestyapp.service.utils.*;
import uz.platform.forestyapp.utils.PaginationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    @Autowired
    StudentRepo studentRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    RoleRepo roleRepo;

    @Autowired
    GroupRepo groupRepo;

    @Autowired
    SubjectRepo subjectRepo;

    @Autowired
    UserService userService;

    @Autowired
    AddressService addressService;

    @Autowired
    ActivityService activityService;

    @Autowired
    PlanLimitService planLimitService;

    @Autowired
    GroupStudentRepo groupStudentRepo;

    @Autowired
    GroupPaymentRepo groupPaymentRepo;

    @Autowired
    StudentPaymentRepo studentPaymentRepo;

    @Autowired
    TeacherPaymentRepo teacherPaymentRepo;

    @Autowired
    TeacherGroupPaymentRepo teacherGroupPaymentRepo;

    @Autowired
    TeacherPaymentChangeService teacherPaymentChangeService;


    @Override
    public List<Student> getStudents(User currentUser) {
//        Pageable pageable = PageRequest.of(page,size);
//        return StudentRes.builder()
//                .students(studentRepo.findAllByStudentEducationCenterId(educationCenterId,pageable))
//                .currentPage(page)
//                .pagesCount(PaginationUtils.calculatePagesCount(size.longValue(),studentRepo.count()))
//                .build();
        return studentRepo.findAllByStudentEducationCenterId(currentUser.getEducationCenter().getId());
    }

    @Override
    public ApiResponse getStudent(UUID id, User currentUser) {
        Optional<Student> optionalStudent = studentRepo.findById(id);
        if(optionalStudent.isEmpty()) return new ApiResponse("Bunday idlik o'quvchi mavjud emas!",false);
        Student student = optionalStudent.get();
        if(!student.getStudent().getEducationCenter().getId().equals(currentUser.getEducationCenter().getId())) return new ApiResponse("Ushbu o'qvuchi joriy o'quv markazfa tegishli emas!",false);
        return new ApiResponse(student,true);
    }

    @Override
    public ApiResponse getStudentGroups(User currentUser, UUID studentId) {
        Optional<Student> optionalStudent = studentRepo.findById(studentId);
        if(optionalStudent.isEmpty()) return new ApiResponse("Bunday idlik talaba mavjud emas!",false);
        Student student = optionalStudent.get();
        if(!student.getStudent().getEducationCenter().getId().equals(currentUser.getEducationCenter().getId())) return new ApiResponse("Ushbu o'quvchi joriy o'quv markazga tegishli emas!",false);
        List<GroupStudent> groupStudents = groupStudentRepo.findAllByStudentId(student.getId());
//        List<Group> groupList = new ArrayList<>();
//        for (GroupStudent groupStudent : groupStudents) {
//            groupList.add(groupStudent.getGroup());
//        }
        return new ApiResponse(groupStudents,true);
    }

    @Override
    public ApiResponse graduateStudent(UUID studentId, User currentUser) {
        return null;
    }

    @Override
    public ApiResponse addStudent(StudentDto studentDto, User currentUser) {
        if(planLimitService.checkPlanLimit(currentUser.getEducationCenter().getId(), PlanLimitName.STUDENT)){
            return new ApiResponse("O'quvchilar soni limitdan oshib ketdi!",false);
        }
        Optional<Role> userRole = roleRepo.findByRoleName(RoleName.USER);
        String email = studentDto.getEmail();
        if(email!=null){
            if(userRepo.existsByEmailAndRoleIsNot(studentDto.getEmail(),userRole.get())){
                return new ApiResponse("Bunday emaillik user tizimda mavjud!",false);
            }
        }
        Optional<Role> optionalRole = roleRepo.findByRoleName(RoleName.STUDENT);
        if(optionalRole.isEmpty()){
            return new ApiResponse("Bunday role tizimda mavjud emas!",false);
        }
        Address address = addressService.saveAddress(studentDto.getAddress());
        User user = User.builder()
                .firstName(studentDto.getFirstName())
                .lastName(studentDto.getLastName())
                .fatherName(studentDto.getFatherName())
                .phoneNumber(studentDto.getPhoneNumber())
                .email(email)
                .role(optionalRole.get())
                .birthDate(studentDto.getBirthDate())
                .educationCenter(currentUser.getEducationCenter())
                .address(address)
                .build();

        Student student = Student.builder()
                .parentPhoneNumber(studentDto.getParentPhoneNumber())
                .student(user)
                .status(StudentStatusName.NEW)
                .build();
        studentRepo.save(student);
        String studentFullName = user.getFirstName() + " " + user.getLastName();
        activityService.addActivity(studentFullName,"", ActivityName.ADD_STUDENT,currentUser);
        return new ApiResponse("Yangi o'quvchi saqlandi!",true);
    }

    @Override
    public ApiResponse editStudent(UUID studentId, StudentDto studentDto, User currentUser) {
        Optional<Student> optionalStudent = studentRepo.findById(studentId);
        if(optionalStudent.isEmpty()){
            return new ApiResponse("Bunday idlik o'quvchi tizimda mavjud emas!",false);
        }
        Student student = optionalStudent.get();
        User user = optionalStudent.get().getStudent();
        if(!user.getEducationCenter().getId().equals(currentUser.getEducationCenter().getId())){
            return new ApiResponse("Ushbu o'quvchi joriy o'quv markazga tegishli emas!",false);
        }
        Address address = addressService.editAddress(studentDto.getAddress(), user.getAddress());

        user.setAddress(address);
        user.setFirstName(studentDto.getFirstName());
        user.setLastName(studentDto.getLastName());
        user.setFatherName(studentDto.getFatherName());
        user.setPhoneNumber(studentDto.getPhoneNumber());
        user.setEmail(studentDto.getEmail());
        user.setBirthDate(studentDto.getBirthDate());
        student.setStudent(user);
        student.setParentPhoneNumber(studentDto.getParentPhoneNumber());
        studentRepo.save(student);
        String studentFullName = user.getFirstName() + " " + user.getLastName();
        activityService.addActivity(studentFullName,"", ActivityName.EDIT_STUDENT,currentUser);
        return new ApiResponse("O'quvchi tahrirlandi!",true);
    }

    @Override
    public ApiResponse deleteStudent(UUID studentId, User currentUser) {
        Optional<Student> optionalStudent = studentRepo.findById(studentId);
        if(optionalStudent.isEmpty()) return new ApiResponse("Bunday idlik talaba mavjud emas!",false);
        Student student = optionalStudent.get();
        User user = student.getStudent();
        if(!user.getEducationCenter().getId().equals(currentUser.getEducationCenter().getId()))  return new ApiResponse("Ushbu o'quvchi joriy o'quv markazga tegishli emas!",false);

        List<Group> groups = groupStudentRepo.findGroupsByStudentId(student.getId());
        if(groups.size()>0){
            for (Group group : groups) {
                group.setStudentsCount((long) group.getGroupStudents().size()-1);
            }
            groupRepo.saveAll(groups);
        }

        List<GroupStudent> groupStudents = groupStudentRepo.findAllByStudentId(studentId);
        if(groupStudents.size()>0){
            for (GroupStudent groupStudent : groupStudents) {
                Group group = groupStudent.getGroup();
                if(group.getStatus().equals(GroupStatusName.NEW)) continue;

                List<TeacherPayment> teacherPayments = teacherPaymentRepo.findAllByTeacherGroupPaymentGroupIdAndMonthBetweenOrderByMonthAsc(group.getId(), group.getCurrentMonth() + 1, group.getDuration());

                if(group.getMonthType().equals(GroupMonthType.DEFINITE)){
                    for(int i=group.getCurrentMonth();i<=group.getDuration();i++){
                        if(i<=group.getCurrentMonth()) continue;
                        teacherPaymentChangeService.changeTeacherPayment(teacherPayments.get(i-group.getCurrentMonth()-1), group.getAgreement(), group.getPrice(),(long) group.getGroupStudents().size()-1);
                    }
                    teacherPaymentRepo.saveAll(teacherPayments);
                    studentPaymentRepo.deleteAllByGroupPaymentGroupIdAndStudentIdAndGroupPaymentMonthBetween(group.getId(), studentId,group.getCurrentMonth()+1,group.getDuration());
                }



                if(group.getCurrentMonth()==1) continue;
                List<StudentPayment> lastMonthStudentPayment = studentPaymentRepo.findAllByGroupPaymentGroupIdAndStudentIdAndGroupPaymentMonthBetweenOrderByGroupPaymentMonthDesc(group.getId(), studentId,1,group.getCurrentMonth());
                if(lastMonthStudentPayment.isEmpty()) continue;
                for(int i=0;i<lastMonthStudentPayment.size();i++){
                    StudentPayment studentPayment = lastMonthStudentPayment.get(i);
                    studentPayment.setStudentId(null);
                    studentPayment.setStudentStatus(StudentStatusName.DELETED);
                }
                studentPaymentRepo.saveAll(lastMonthStudentPayment);
            }
        }
        groupStudentRepo.deleteAll(groupStudents);
        studentRepo.delete(student);
        String studentFullName = user.getFirstName() + " " + user.getLastName();
        activityService.addActivity(studentFullName,"", ActivityName.DELETE_STUDENT,currentUser);
        return new ApiResponse("Talaba tizimdan o'chirildi!",true);
    }

    @Override
    public ApiResponse getStudentPayments(User user, UUID id) {
        Optional<Student> optionalStudent = studentRepo.findById(id);
        if(optionalStudent.isEmpty()) return new ApiResponse("Bunday idlik o'quvchi mavjud emas!",false);
        Student student = optionalStudent.get();
        if(!student.getStudent().getEducationCenter().getId().equals(user.getEducationCenter().getId())) return new ApiResponse("Ushbu o'qvuchi joriy o'quv markazfa tegishli emas!",false);
        List<StudentPayment> studentPayments = studentPaymentRepo.findAllByStudentIdAndStatusOrderByCreatedAt(student.getId(),PaymentStatusName.PAID);
        return new ApiResponse(studentPayments,true);
    }
}
