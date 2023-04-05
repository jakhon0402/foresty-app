package uz.platform.forestyapp.service.impls;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.platform.forestyapp.entity.RefreshToken;
import uz.platform.forestyapp.entity.Role;
import uz.platform.forestyapp.entity.User;
import uz.platform.forestyapp.entity.enums.RoleName;
import uz.platform.forestyapp.payload.ApiResponse;
import uz.platform.forestyapp.payload.LoginDto;
import uz.platform.forestyapp.payload.RegisterDto;
import uz.platform.forestyapp.payload.SignInDto;
import uz.platform.forestyapp.payload.response.LoginRes;
import uz.platform.forestyapp.repository.RefreshTokenRepo;
import uz.platform.forestyapp.repository.RoleRepo;
import uz.platform.forestyapp.repository.UserRepo;
import uz.platform.forestyapp.security.JwtProvider;
import uz.platform.forestyapp.service.AuthService;
import uz.platform.forestyapp.service.utils.EmailService;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    UserRepo userRepo;

    @Autowired
    RoleRepo roleRepo;

   @Autowired
   EmailService emailService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    RefreshTokenRepo refreshTokenRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepo.findByUsername(username);
        if(optionalUser.isEmpty()){
            return userRepo.findByEmail(username).orElseThrow(()->new UsernameNotFoundException("Ushbu user topilmadi!"));
        }
        return optionalUser.get();
    }

    @Transactional
    @Override
    public ApiResponse register(RegisterDto registerDto) {
        ZoneId zoneId = ZoneId.of("Asia/Samarkand");
        Optional<Role> optionalRole = roleRepo.findByRoleName(RoleName.USER);
        if(optionalRole.isEmpty()){
            return new ApiResponse("User roli mavjud emas!",false);
        }
        Optional<User> optionalUser = userRepo.findByEmailAndRole(registerDto.getEmail(),optionalRole.get());
        if(!optionalUser.isPresent()){
            if(!registerDto.getPassword().equals(registerDto.getRePassword())){
                return new ApiResponse("Parollar mos emas",false);
            }
            String emailCode = emailService.getRandomNumberString();
            User user = new User();
            user.setFirstName(registerDto.getFirstName());
            user.setLastName(registerDto.getLastName());
            user.setEmail(registerDto.getEmail());
            user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
            user.setRole(optionalRole.get());
            user.setEmailCode(emailCode);
            user.setEmailCodeCreatedAt(ZonedDateTime.now(zoneId));
            try {
                emailService.sendEmailCodeToUser(user.getEmail(),emailCode, user.getLastName(), user.getFirstName());
            } catch (MessagingException e) {
                return new ApiResponse("Error",false);
            }
            userRepo.save(user);
            return new ApiResponse("User saqlandi iltimos email orqali o'zingizni tasdiqlang!",true);
        }
        User registeredUser = optionalUser.get();
        if(registeredUser.isEnabled()){
            return new ApiResponse("Bunday emaillik user mavjud !",false);
        }
        String emailCode = emailService.getRandomNumberString();
        registeredUser.setFirstName(registerDto.getFirstName());
        registeredUser.setLastName(registerDto.getLastName());
        registeredUser.setEmail(registerDto.getEmail());
        registeredUser.setEmailCode(emailCode);
        registeredUser.setEmailCodeCreatedAt(ZonedDateTime.now(zoneId));
        registeredUser.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        try {
            emailService.sendEmailCodeToUser(registeredUser.getEmail(),emailCode,registeredUser.getLastName(), registeredUser.getFirstName());
        } catch (MessagingException e) {
            return new ApiResponse("Error",false);
        }
        userRepo.save(registeredUser);
        return new ApiResponse("User saqlandi. Iltimos email kod orqali o'zingizni tasdiqlang!",true);
    }

    @Override
    public ApiResponse login(LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDto.getUsername(),
                    loginDto.getPassword()));
            User user = (User) authentication.getPrincipal();
            if(user.getRole().getRoleName()==RoleName.USER || user.getRole().getRoleName() == RoleName.SUPER_ADMIN){
                throw new BadCredentialsException("Error");
            }
            RefreshToken refreshTkn = RefreshToken.builder()
                    .user(user).build();
            String accessToken = jwtProvider.generateAccessToken(user.getUsername());
            RefreshToken savedRefreshToken = refreshTokenRepo.save(refreshTkn);

            String refreshToken = jwtProvider.generateRefreshToken(user.getUsername(),savedRefreshToken.getId());


            return new ApiResponse("Tizimga muvaffaqiyatli kirildi!",true,new LoginRes(accessToken,refreshToken));
        }
        catch (BadCredentialsException badCredentialsException){
            return new ApiResponse("Login yoki parol noto'g'ri!",false);
        }
    }

    @Override
    public ApiResponse signIn(SignInDto signInDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    signInDto.getEmail(),
                    signInDto.getPassword()));
            User user = (User) authentication.getPrincipal();
            Optional<Role> userRole = roleRepo.findByRoleName(RoleName.USER);
            if(!user.getRole().equals(userRole.get())){
                return new ApiResponse("Xatolik",false);
            }
            String token = jwtProvider.generateAccessToken(user.getUsername());

            return new ApiResponse("Bearer " + token,true);

        }
        catch (BadCredentialsException badCredentialsException){
            return new ApiResponse("Xatolik",false);
        }
    }

    @Transactional
    @Override
    public ApiResponse verifyEmailUser(String email, String emailCode) {
        ZoneId zoneId = ZoneId.of("Asia/Samarkand");
        Optional<Role> userRole = roleRepo.findByRoleName(RoleName.USER);

        Optional<User> optionalUser = userRepo.findByEmailAndRole(email,userRole.get());
        if(optionalUser.isEmpty()){
            return new ApiResponse("Bunday emaillik user mavjud emas!",false);
        }
        User user = optionalUser.get();
        if(user.isEnabled()){
            return new ApiResponse("User allaqachon tasdiqlangan!",false);
        }
        if(ZonedDateTime.now(zoneId).isAfter(user.getEmailCodeCreatedAt().plusMinutes(5))){
            return new ApiResponse("Kod yaroqsiz!",false);
        }
        if(!user.getEmailCode().equals(emailCode)){
            return new ApiResponse("Kod mos emas!",false);
        }
        user.setEmailCode(null);
        user.setEnabled(true);
        userRepo.save(user);
        return new ApiResponse("Email tasdiqlandi!",true);
    }

    @Transactional
    @Override
    public ApiResponse verifyEmail(String email, String emailCode) {
        ZoneId zoneId = ZoneId.of("Asia/Samarkand");
        Optional<Role> userRole = roleRepo.findByRoleName(RoleName.USER);

        List<User> allByEmail = userRepo.findAllByEmail(email);
        if(allByEmail.size()==0){
            return new ApiResponse("Bunday usernamelik user mavjud emas!",false);
        }
        User user = null;
        for (User user1 : allByEmail) {
            if(!user1.getRole().equals(userRole.get())){
                user = user1;
            }
        }

        if(user == null){
            return new ApiResponse("Bunday usernamelik user mavjud emas!",false);
        }

        if(user.isEnabled()){
            return new ApiResponse("User allaqachon tasdiqlangan!",false);
        }
        if(ZonedDateTime.now(zoneId).isAfter(user.getEmailCodeCreatedAt().plusMinutes(5))){
            return new ApiResponse("Kod yaroqsiz!",false);
        }
        if(!user.getEmailCode().equals(emailCode)){
            return new ApiResponse("Kod mos emas!",false);
        }
        user.setEmailCode(null);
        user.setEnabled(true);
        userRepo.save(user);
        return new ApiResponse("Email tasdiqlandi!",true);
    }

    @Transactional
    @Override
    public ApiResponse resendEmailCode(UUID userId,User currentUser) {
        ZoneId zoneId = ZoneId.of("Asia/Samarkand");
        Optional<User> optionalUser = userRepo.findById(userId);
        if(optionalUser.isEmpty()) return new ApiResponse("Bunday idlik user mavjud emas!",false);

        User user = optionalUser.get();
        if(!user.getEducationCenter().getId().equals(currentUser.getEducationCenter().getId())) return new ApiResponse("Ushbu user joriy o'quv markazga tegishli emas!",false);
        RoleName roleName = user.getRole().getRoleName();
        if(roleName.equals(RoleName.OWNER)||roleName.equals(RoleName.USER)) return new ApiResponse("Ushbu rollik user yarqosiz!",false);
        if(user.isEnabled()) return new ApiResponse("User allaqachon tasdiqlangan!",false);
        String emailCode = emailService.getRandomNumberString();
        user.setEmailCode(emailCode);
        user.setEmailCodeCreatedAt(ZonedDateTime.now(zoneId));
        try {
            emailService.sendEmailCode(user.getEmail(),emailCode, user.getLastName(), user.getFirstName());
        } catch (MessagingException e) {
            return new ApiResponse("Error",false);
        }
        userRepo.save(user);
        return new ApiResponse("Email kod qayta yuborildi!",true);
    }

    @Transactional
    @Override
    public ApiResponse refreshToken(String token) {
        if(jwtProvider.validateRefreshToken(token)&& refreshTokenRepo.existsById(jwtProvider.getRefreshTokenIdFromRefreshToken(token))){
            RefreshToken oldRefreshToken = refreshTokenRepo.findById(jwtProvider.getRefreshTokenIdFromRefreshToken(token)).get();

            refreshTokenRepo.deleteById(oldRefreshToken.getId());
            User user = oldRefreshToken.getUser();
            RefreshToken newRefreshToken = new RefreshToken();
            newRefreshToken.setUser(user);
            refreshTokenRepo.save(newRefreshToken);

            String accessToken = jwtProvider.generateAccessToken(user.getUsername());
            String newRefreshTokenString = jwtProvider.generateRefreshToken(user.getUsername(),newRefreshToken.getId());

            return new ApiResponse("Tizimga muvaffaqiyatli kirildi!",true,new LoginRes(accessToken,newRefreshTokenString));
        }
        throw new BadCredentialsException("Invalid token");
    }

    @Transactional
    @Override
    public ApiResponse accessToken(String token) {
        if(jwtProvider.validateRefreshToken(token)&& refreshTokenRepo.existsById(jwtProvider.getRefreshTokenIdFromRefreshToken(token))){
            RefreshToken oldRefreshToken = refreshTokenRepo.findById(jwtProvider.getRefreshTokenIdFromRefreshToken(token)).get();

            User user = oldRefreshToken.getUser();

            String accessToken = jwtProvider.generateAccessToken(user.getUsername());

            return new ApiResponse("Tizimga muvaffaqiyatli kirildi!",true,new LoginRes(accessToken,token));
        }
        return new ApiResponse("Failed!",false);
    }

    @Transactional
    @Override
    public ApiResponse logout(String token) {
        if(jwtProvider.validateRefreshToken(token)&& refreshTokenRepo.existsById(jwtProvider.getRefreshTokenIdFromRefreshToken(token))){
            RefreshToken oldRefreshToken = refreshTokenRepo.findById(jwtProvider.getRefreshTokenIdFromRefreshToken(token)).get();

            refreshTokenRepo.deleteById(oldRefreshToken.getId());

            return new ApiResponse("Logged out!",true);
        }
        throw new BadCredentialsException("Invalid token");
    }

}
