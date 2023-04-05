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
import uz.platform.forestyapp.entity.Teacher;
import uz.platform.forestyapp.entity.User;
import uz.platform.forestyapp.payload.ApiResponse;
import uz.platform.forestyapp.payload.TeacherDto;
import uz.platform.forestyapp.payload.TeacherEditDto;
import uz.platform.forestyapp.security.CurrentUser;
import uz.platform.forestyapp.service.EducationCenterService;
import uz.platform.forestyapp.service.TeacherService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {

    @Autowired
    TeacherService teacherService;

    @Autowired
    EducationCenterService educationCenterService;

    @GetMapping("/{id}/groups")
    public HttpEntity<?> getTeacherGroups(@CurrentUser User user,@PathVariable("id")UUID id){
        ApiResponse apiResponse = teacherService.getTeacherGroups(user,id);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @GetMapping("/{id}/payment")
    public HttpEntity<?> getTeacherPayments(@CurrentUser User user,@PathVariable("id")UUID id){
        ApiResponse apiResponse = teacherService.getTeacherPayments(user,id);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @GetMapping
    public HttpEntity<?> getTeachers(@CurrentUser User user){
        List<Teacher> teachers = teacherService.getTeachers(user);
        return ResponseEntity.ok().body(teachers);
    }

    @GetMapping("/{id}")
    public HttpEntity<?> getTeacher(@PathVariable("id")UUID id,@CurrentUser User user){
        ApiResponse apiResponse = teacherService.getTeacher(id, user);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','OWNER','ADMIN','MODERATOR')")
    @PostMapping
    public HttpEntity<?> addTeacher(@Valid @RequestBody TeacherDto teacherDto, @CurrentUser User user){
        boolean checkPlanExpireDate = educationCenterService.checkPlanExpireDate(user.getEducationCenter().getId());
        if(!checkPlanExpireDate){
            return ResponseEntity.status(409).body("Ta'rif reja aktiv emas!");
        }
        ApiResponse apiResponse = teacherService.addTeacher(teacherDto,user);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','OWNER','ADMIN','MODERATOR')")
    @PutMapping("/{id}")
    public HttpEntity<?> editTeacher(@Valid @RequestBody TeacherEditDto teacherDto, @PathVariable("id")UUID id, @CurrentUser User user){
        boolean checkPlanExpireDate = educationCenterService.checkPlanExpireDate(user.getEducationCenter().getId());
        if(!checkPlanExpireDate){
            return ResponseEntity.status(409).body("Ta'rif reja aktiv emas!");
        }
        ApiResponse apiResponse = teacherService.editTeacher(teacherDto,user,id);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','OWNER','ADMIN')")
    @PostMapping("/block/{id}")
    public HttpEntity<?> blockTeacher(@PathVariable("id") UUID id, @CurrentUser User user){
        boolean checkPlanExpireDate = educationCenterService.checkPlanExpireDate(user.getEducationCenter().getId());
        if(!checkPlanExpireDate){
            return ResponseEntity.status(409).body("Ta'rif reja aktiv emas!");
        }
        ApiResponse apiResponse = teacherService.blockTeacher(id, user.getEducationCenter().getId());
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','OWNER','ADMIN','MODERATOR')")
    @PostMapping("/finishWork/{id}")
    public HttpEntity<?> finishTeacherWork(@PathVariable("id")UUID id,@CurrentUser User user){
        boolean checkPlanExpireDate = educationCenterService.checkPlanExpireDate(user.getEducationCenter().getId());
        if(!checkPlanExpireDate){
            return ResponseEntity.status(409).body("Ta'rif reja aktiv emas!");
        }
        ApiResponse apiResponse = teacherService.finishWork(id, user);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','OWNER','ADMIN')")
    @DeleteMapping("/{id}")
    public HttpEntity<?> deleteTeacher(@PathVariable("id")UUID id,@CurrentUser User user){
        boolean checkPlanExpireDate = educationCenterService.checkPlanExpireDate(user.getEducationCenter().getId());
        if(!checkPlanExpireDate){
            return ResponseEntity.status(409).body("Ta'rif reja aktiv emas!");
        }
        ApiResponse apiResponse = teacherService.deleteTeacher(id, user);
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
