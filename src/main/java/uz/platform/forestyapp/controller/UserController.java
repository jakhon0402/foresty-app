package uz.platform.forestyapp.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.platform.forestyapp.entity.EducationCenter;
import uz.platform.forestyapp.entity.User;
import uz.platform.forestyapp.payload.ApiResponse;
import uz.platform.forestyapp.payload.CenterEditDto;
import uz.platform.forestyapp.payload.UserEditDto;
import uz.platform.forestyapp.payload.response.HomeRes;
import uz.platform.forestyapp.security.CurrentUser;
import uz.platform.forestyapp.service.AuthService;
import uz.platform.forestyapp.service.UserService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    AuthService authService;

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','OWNER','ADMIN','MODERATOR','FINANCIER')")
    @GetMapping("/center")
    public HttpEntity<?> getCenterData(@CurrentUser User user){
        EducationCenter centerData = userService.getCenterData(user);
        return ResponseEntity.ok().body(centerData);
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','OWNER','ADMIN')")
    @PutMapping("/center")
    public HttpEntity<?> editEducationCenter(@CurrentUser User user,@Valid @RequestBody CenterEditDto centerEditDto){
        ApiResponse apiResponse = userService.editCenterData(user,centerEditDto);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);

    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','OWNER','ADMIN')")
    @PostMapping("/center/logo")
    public HttpEntity<?> changeCenterLogo(@CurrentUser User user,@RequestParam("file")MultipartFile file) throws IOException {
        ApiResponse apiResponse = userService.changeCenterLogo(user,file);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);

    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','OWNER','ADMIN')")
    @PostMapping("/center/header")
    public HttpEntity<?> changeCenterHeader(@CurrentUser User user,@RequestParam("file")MultipartFile file) throws IOException {
        ApiResponse apiResponse = userService.changeCenterHeader(user,file);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);

    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','OWNER','ADMIN','MODERATOR','FINANCIER','TEACHER')")
    @GetMapping("/currentUser/system")
    public HttpEntity<?> getSystemUser(@CurrentUser User user){
        return ResponseEntity.ok().body(user);
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','OWNER','ADMIN','MODERATOR','FINANCIER')")
    @GetMapping("/home")
    public HttpEntity<?> getHomeData(@CurrentUser User user){
        HomeRes homeData = userService.getHomeData(user);
        return ResponseEntity.ok().body(homeData);
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','OWNER','ADMIN')")
    @PostMapping("/resendEmailCode/{id}")
    public HttpEntity<?> resendEmailCode(@CurrentUser User user, @PathVariable("id")UUID id){
        ApiResponse apiResponse = authService.resendEmailCode(id, user);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','OWNER','ADMIN','MODERATOR','FINANCIER')")
    @PostMapping("/editProfile")
    public HttpEntity<?> editProfile(@CurrentUser User user,@RequestBody UserEditDto userEditDto){
        ApiResponse apiResponse = userService.editProfile(user, userEditDto);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','OWNER','ADMIN','MODERATOR','FINANCIER')")
    @PostMapping("/updateAvatar")
    public HttpEntity<?> updateAvatar(@CurrentUser User user, @RequestParam("file")MultipartFile file) throws IOException {
        ApiResponse apiResponse = userService.updateAvatar(user, file);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

}
