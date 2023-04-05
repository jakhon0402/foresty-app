package uz.platform.forestyapp.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import uz.platform.forestyapp.entity.User;
import uz.platform.forestyapp.payload.ApiResponse;
import uz.platform.forestyapp.payload.LoginDto;
import uz.platform.forestyapp.payload.RegisterDto;
import uz.platform.forestyapp.payload.SignInDto;

import java.util.UUID;

public interface AuthService extends UserDetailsService {
    ApiResponse register(RegisterDto registerDto);
    ApiResponse login(LoginDto loginDto);

    ApiResponse signIn(SignInDto signInDto);

    ApiResponse verifyEmailUser(String email,String emailCode);

    ApiResponse verifyEmail(String email,String emailCode);

    ApiResponse resendEmailCode(UUID userId, User currentUser);

    ApiResponse refreshToken(String token);

    ApiResponse accessToken(String token);

    ApiResponse logout(String token);

}
