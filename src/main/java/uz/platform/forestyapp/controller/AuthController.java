package uz.platform.forestyapp.controller;

import jakarta.annotation.security.RolesAllowed;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import uz.platform.forestyapp.entity.User;
import uz.platform.forestyapp.payload.ApiResponse;
import uz.platform.forestyapp.payload.LoginDto;
import uz.platform.forestyapp.payload.RegisterDto;
import uz.platform.forestyapp.payload.SignInDto;
import uz.platform.forestyapp.security.JwtProvider;
import uz.platform.forestyapp.service.AuthService;

import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signIn")
    public HttpEntity<?> signIn(@RequestBody SignInDto signInDto){
        ApiResponse apiResponse = authService.signIn(signInDto);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @PostMapping("/login")
    public HttpEntity<?> login(@RequestBody LoginDto loginDto){
        ApiResponse apiResponse = authService.login(loginDto);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @PostMapping("/signUp")
    public HttpEntity<?> signUp(@RequestBody RegisterDto registerDto){
        ApiResponse apiResponse = authService.register(registerDto);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @GetMapping("/verifyEmail")
    public HttpEntity<?> verifyEmail(@RequestParam String email,@RequestParam String emailCode){
        ApiResponse apiResponse = authService.verifyEmail(email, emailCode);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @GetMapping("/verifyEmailUser")
    public HttpEntity<?> verifyEmailUser(@RequestParam String email,@RequestParam String emailCode){
        ApiResponse apiResponse = authService.verifyEmailUser(email, emailCode);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @PostMapping("/accessToken")
    public HttpEntity<?> getAccessToken(@RequestParam("token")String token){
        ApiResponse apiResponse = authService.accessToken(token);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @PostMapping("/logout")
    public HttpEntity<?> logout(@RequestParam("token")String refreshToken){
        ApiResponse apiResponse = authService.logout(refreshToken);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }



//    @GetMapping("/verify")
//    public ApiResponse verify(){
//        String emailCode = getRandomNumberString();
//        boolean isSentEmail = authService.sendEmailCode("jakhon99dev@gmail.com", emailCode);
//        if(!isSentEmail){
//            return new ApiResponse("Emailga yuborishda xatolik!",false);
//        }
//        return new ApiResponse("Kod yuborildi!",true);
//    }

}
