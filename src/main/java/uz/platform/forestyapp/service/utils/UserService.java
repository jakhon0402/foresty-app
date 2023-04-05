package uz.platform.forestyapp.service.utils;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.platform.forestyapp.entity.Address;
import uz.platform.forestyapp.entity.EducationCenter;
import uz.platform.forestyapp.entity.Role;
import uz.platform.forestyapp.entity.User;
import uz.platform.forestyapp.payload.UserDto;
import uz.platform.forestyapp.repository.RoleRepo;
import uz.platform.forestyapp.repository.UserRepo;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@Transactional
public class UserService {
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    EmailService emailService;

    @Autowired
    UserRepo userRepo;

    @Autowired
    RoleRepo roleRepo;

    public User saveUser(UserDto userDto, Role role, Address address, EducationCenter educationCenter){
        ZoneId zoneId = ZoneId.of("Asia/Samarkand");
        String emailCode = emailService.getRandomNumberString();
        return User.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .fatherName(userDto.getFatherName())
                .phoneNumber(userDto.getPhoneNumber())
                .role(role)
                .birthDate(userDto.getBirthDate())
                .email(userDto.getEmail())
                .username(userDto.getUsername())
                .educationCenter(educationCenter)
                .password(passwordEncoder.encode(userDto.getPassword()))
                .emailCode(emailCode)
                .emailCodeCreatedAt(ZonedDateTime.now(zoneId))
                .address(address)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();
    }

    public boolean checkUser(User user,UUID educationCenterId){
        return !user.getEducationCenter().getId().equals(educationCenterId);
    }

    public User blockUser(User user){
        user.setAccountNonLocked(false);
        return userRepo.save(user);
    }

}
