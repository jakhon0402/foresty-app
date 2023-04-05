package uz.platform.forestyapp.service.impls;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.platform.forestyapp.entity.Employee;
import uz.platform.forestyapp.entity.EmployeePayment;
import uz.platform.forestyapp.entity.Role;
import uz.platform.forestyapp.entity.User;
import uz.platform.forestyapp.entity.enums.*;
import uz.platform.forestyapp.payload.ApiResponse;
import uz.platform.forestyapp.payload.EmployeeDto;
import uz.platform.forestyapp.payload.EmployeeEditDto;
import uz.platform.forestyapp.payload.UserDto;
import uz.platform.forestyapp.repository.*;
import uz.platform.forestyapp.service.utils.*;
import uz.platform.forestyapp.service.EmployeeService;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    EmployeeRepo employeeRepo;

    @Autowired
    EmployeePaymentRepo employeePaymentRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    RoleRepo roleRepo;

    @Autowired
    DocumentRepo documentRepo;

    @Autowired
    UserService userService;

    @Autowired
    AddressService addressService;

    @Autowired
    PlanLimitService planLimitService;

    @Autowired
    ActivityService activityService;

    @Autowired
    EmailService emailService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public List<Employee> getEmployees(User user) {
        return employeeRepo.findAllByEmployeeEducationCenterId(user.getEducationCenter().getId());
    }

    @Override
    public ApiResponse getEmployee(User user, UUID id) {
        Optional<Employee> optionalEmployee = employeeRepo.findById(id);
        if(optionalEmployee.isEmpty()) return new ApiResponse("Bunday idlik xodim mavjud emas!",false);
        Employee employee = optionalEmployee.get();
        if(!employee.getEmployee().getEducationCenter().getId().equals(user.getEducationCenter().getId())) return new ApiResponse("Ushbu xodim joriy o'quv markazfa tegishli emas!",false);
        return new ApiResponse(employee,true);
    }

    @Override
    public ApiResponse addEmployee(EmployeeDto employeeDto,User currentUser) {
        if(planLimitService.checkPlanLimit(currentUser.getEducationCenter().getId(), PlanLimitName.EMPLOYEE)){
            return new ApiResponse("Xodimlar soni limitdan kupayib ketdi!",false);
        }
        ZoneId zoneId = ZoneId.of("Asia/Samarkand");

        UserDto userDto = employeeDto.getEmployee();
        String role = employeeDto.getRole();
        RoleName roleName;
        if(Objects.equals(role, RoleName.ADMIN.name())){
            roleName = RoleName.ADMIN;
        }
        else if(Objects.equals(role, RoleName.MODERATOR.name())){
            roleName = RoleName.MODERATOR;
        }
        else if(Objects.equals(role, RoleName.FINANCIER.name())){
            roleName = RoleName.FINANCIER;
        }
        else {
            return new ApiResponse("Bunday role tizimda mavjud emas!",false);
        }
        Optional<Role> optionalRole = roleRepo.findByRoleName(roleName);
        if(optionalRole.isEmpty()){
            return new ApiResponse("Bunday role tizimda mavjud emas!",false);
        }

        if(userRepo.existsByUsername(userDto.getUsername())){
            return new ApiResponse("Bunday usernamelik user tizimda mavjud!",false);
        }

        Optional<Role> userRole = roleRepo.findByRoleName(RoleName.USER);

        if(userRepo.existsByEmailAndRoleIsNot(userDto.getEmail(),userRole.get())){
            return new ApiResponse("Bunday emaillik user tizimda mavjud!",false);
        }

        if(employeeDto.getTestDaysCount()>90||employeeDto.getTestDaysCount()<=0) return new ApiResponse("Sinov muddati noto'g'ri kiritildi!",false);

        ZonedDateTime jobStartsDate = ZonedDateTime.ofInstant(employeeDto.getJobStartsDate().toInstant(), zoneId);
        if(ZonedDateTime.now(zoneId).isAfter(jobStartsDate)) return new ApiResponse("Ish boshlanish sanasi noto'g'ri kiritildi!",false);

        User user = userService.saveUser(userDto,optionalRole.get(),null,currentUser.getEducationCenter());
        Employee employee = Employee.builder()
                .testDaysCount(employeeDto.getTestDaysCount())
                .employee(user)
                .status(EmployeeStatusName.NEW)
                .jobStartsDate(jobStartsDate)
                .currentSalary(employeeDto.getSalary())
                .workedMonthCount(0L)
                .build();
        try {
            emailService.sendEmailCodeFirstLogIn(user.getEmail(),user.getEmailCode(),user.getLastName(),user.getFirstName(), user.getUsername(), userDto.getPassword());
        } catch (MessagingException e) {
            return new ApiResponse("Error!",false);
        }
        employeeRepo.save(employee);
        String employeeFullName = user.getFirstName() + " " + user.getLastName();
        activityService.addActivity(employeeFullName,"", ActivityName.ADD_EMPLOYEE,currentUser);
        return new ApiResponse("Xodim saqlandi. Xodim emailga yuborilgan kod orqali emailni tasdiqlab keyin tizimga kirishi mumkin!",true);
    }

    @Override
    public ApiResponse blockEmployee(UUID employeeId, UUID educationCenterId) {
        Optional<Employee> optionalEmployee = employeeRepo.findById(employeeId);
        if(optionalEmployee.isEmpty()){
            return new ApiResponse("Bunday idlik xodim tizimda mavjud emas!",false);
        }
        Employee employee = optionalEmployee.get();
        User user = employee.getEmployee();
        if(userService.checkUser(user,educationCenterId)){
            return new ApiResponse("Bunday idlik xodim joriy uquv markazga tegishli emas!",false);
        }
        employee.setStatus(EmployeeStatusName.BLOCKED);
        userService.blockUser(user);
        employeeRepo.save(employee);
        return new ApiResponse("Xodim bloklandi!",true);
    }

    @Override
    public ApiResponse deleteEmployee(UUID employeeId, User currentUser) {
        Optional<Employee> optionalEmployee = employeeRepo.findById(employeeId);
        if(optionalEmployee.isEmpty()){
            return new ApiResponse("Bunday idlik xodim tizimda mavjud emas!",false);
        }
        Employee employee = optionalEmployee.get();
        User user = employee.getEmployee();
        if(userService.checkUser(user,currentUser.getEducationCenter().getId())){
            return new ApiResponse("Bunday idlik xodim joriy uquv markazga tegishli emas!",false);
        }
        if(user.getAddress()!=null){
            addressService.deleteAddress(user.getAddress());
        }
        employeeRepo.delete(employee);

        String employeeFullName = user.getFirstName() + " " + user.getLastName();
        activityService.addActivity(employeeFullName,"", ActivityName.DELETE_EMPLOYEE,currentUser);
        return new ApiResponse("Xodim o'chirildi!",true);

    }

    @Override
    public ApiResponse finishWork(UUID employeeId, User currentUser) {
        Optional<Employee> optionalEmployee = employeeRepo.findById(employeeId);
        if(optionalEmployee.isEmpty()){
            return new ApiResponse("Bunday idlik xodim tizimda mavjud emas!",false);
        }
        Employee employee = optionalEmployee.get();
        User user = employee.getEmployee();
        if(userService.checkUser(user,currentUser.getEducationCenter().getId())){
            return new ApiResponse("Bunday idlik xodim joriy uquv markazga tegishli emas!",false);
        }
        employee.setStatus(EmployeeStatusName.FINISH);
        user.setAccountNonExpired(false);
        employeeRepo.save(employee);
        String employeeFullName = user.getFirstName() + " " + user.getLastName();
        activityService.addActivity(employeeFullName,"", ActivityName.FINISH_EMPLOYEE_WORK,currentUser);
        return new ApiResponse("Xodim ishini yakunladik!",true);
    }

    @Override
    public ApiResponse editEmployee(EmployeeEditDto employeeDto, User user,UUID id) {
        ZoneId zoneId = ZoneId.of("Asia/Samarkand");
        Optional<Employee> optionalEmployee = employeeRepo.findById(id);
        if(optionalEmployee.isEmpty()) return new ApiResponse("Bunday idlik xodim mavjud emas!",false);
        Employee employee = optionalEmployee.get();
        if(!employee.getEmployee().getEducationCenter().getId().equals(user.getEducationCenter().getId())) return new ApiResponse("Ushbu xodim joriy o'quv markazga tegishli emas!",false);
        if(employeeDto.getSalary()<0||employeeDto.getSalary()>99_999_999) return new ApiResponse("Invalid salary!",false);
        if(employeeDto.getJobStartsDate()!=null){
            if(ZonedDateTime.now().isAfter(employee.getJobStartsDate())){
                return new ApiResponse("Ish boshlanish sanasi vaqti o'tib bo'lgan!",false);
            }
            employee.setJobStartsDate(ZonedDateTime.ofInstant(employeeDto.getJobStartsDate().toInstant(),zoneId));
        }
        if(employeeDto.getTestDaysCount()!=null){
            if(ZonedDateTime.now().isAfter(employee.getJobStartsDate().plusDays(employee.getTestDaysCount()))){
                return new ApiResponse("Sinov muddati vaqti o'tib bo'lgan!",false);
            }
            employee.setTestDaysCount(employeeDto.getTestDaysCount());
        }
        Optional<Role> optionalRole = roleRepo.findByRoleName(employeeDto.getRole());
        if(optionalRole.isEmpty()) return new ApiResponse("Bunday role mavjud emas!",false);
        employee.setCurrentSalary(employeeDto.getSalary());
        User employeeUser = employee.getEmployee();
        employeeUser.setFirstName(employeeDto.getFirstName());
        employeeUser.setLastName(employeeDto.getLastName());
        employeeUser.setFatherName(employeeDto.getFatherName());
        employeeUser.setBirthDate(employeeDto.getBirthDate());
        employeeUser.setRole(optionalRole.get());
        int counter = 0;
        String password=null;
        String email=employeeUser.getEmail();
        String username=null;
        if(employeeDto.getPassword()!=null){
            password=employeeDto.getPassword();
        } else counter+=1;

        if(employeeDto.getEmail()!=null){
            email=employeeDto.getEmail();
            boolean existsByEmail = userRepo.existsByEmailAndRoleRoleNameIsNotAndIdNot(email, RoleName.USER, employeeUser.getId());
            if(existsByEmail) return new ApiResponse("Bunday emaillik user tizimda mavjud!",false);

        } else counter+=1;
        if(employeeDto.getUsername()!=null){
            username=employeeDto.getUsername();
            boolean existsByUsernameAndIdNot = userRepo.existsByUsernameAndIdNot(username,  employeeUser.getId());
            if(existsByUsernameAndIdNot) return new ApiResponse("Bunday usernamelik user tizimda mavjud!",false);
        } else counter+=1;

        if(counter==3){
            employeeRepo.save(employee);
            return new ApiResponse("Xodim ma'lumotlari o'zgartirildi!",true);
        }
        employeeUser.setEmail(email);
        String emailCode = emailService.getRandomNumberString();

        if(password==null&&username==null){
            employeeUser.setEmailCode(emailCode);
            employeeUser.setEmailCodeCreatedAt(ZonedDateTime.now(zoneId));
            employeeUser.setEnabled(false);
            try {
                emailService.sendEmailCode(email,emailCode,employeeUser.getLastName(),employeeUser.getFirstName());
            } catch (MessagingException e) {
                return new ApiResponse("Error",false);
            }
            employeeRepo.save(employee);
            return new ApiResponse("Xodim ma'lumotlari o'zgartirildi!",true);
        }
        else if(username==null){
            employeeUser.setPassword(passwordEncoder.encode(password));
            employeeUser.setEmailCode(emailCode);
            employeeUser.setEmailCodeCreatedAt(ZonedDateTime.now(zoneId));
            try {
                emailService.sendEmailPasswordChange(email,employeeUser.getLastName(),employeeUser.getFirstName(),password);
            } catch (MessagingException e) {
                return new ApiResponse("Error!",false);
            }
            employeeRepo.save(employee);
            return new ApiResponse("Xodim ma'lumotlari o'zgartirildi!",true);

        }else if(password==null){
            employeeUser.setUsername(username);
            employeeUser.setEmailCode(emailCode);
            employeeUser.setEmailCodeCreatedAt(ZonedDateTime.now(zoneId));
            try {
                emailService.sendEmailUsernameChange(email,employeeUser.getLastName(),employeeUser.getFirstName(),username);
            } catch (MessagingException e) {
                return new ApiResponse("Error!",false);
            }
            employeeRepo.save(employee);
            return new ApiResponse("Xodim ma'lumotlari o'zgartirildi!",true);

        } else{
            employeeUser.setPassword(passwordEncoder.encode(password));
            employeeUser.setUsername(username);
            employeeUser.setEmailCode(emailCode);
            employeeUser.setEmailCodeCreatedAt(ZonedDateTime.now(zoneId));
            employeeUser.setEnabled(false);
            try {
                emailService.sendEmailCodeFirstLogIn(email,emailCode,employeeUser.getLastName(),employeeUser.getFirstName(),username,password);
            } catch (MessagingException e) {
                return new ApiResponse("Error!",false);
            }
            employeeRepo.save(employee);
            return new ApiResponse("Xodim ma'lumotlari o'zgartirildi!",true);

        }

    }

    @Override
    public Object getEmployeePayments(User user, UUID id) {
        Optional<Employee> optionalEmployee = employeeRepo.findById(id);
        if(optionalEmployee.isEmpty()) return new ApiResponse("Bunday idlik xodim mavjud emas!",false);
        Employee employee = optionalEmployee.get();
        if(!employee.getEmployee().getEducationCenter().getId().equals(user.getEducationCenter().getId())) return new ApiResponse("Ushbu xodim joriy o'quv markazfa tegishli emas!",false);
        List<EmployeePayment> employeePayments = employeePaymentRepo.findAllByEmployeeIdAndStatus(employee.getId(), PaymentStatusName.PAID);
        return new ApiResponse(employeePayments,true);
    }
}
