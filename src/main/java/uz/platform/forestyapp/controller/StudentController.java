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
import uz.platform.forestyapp.entity.Student;
import uz.platform.forestyapp.entity.User;
import uz.platform.forestyapp.payload.ApiResponse;
import uz.platform.forestyapp.payload.StudentDto;
import uz.platform.forestyapp.payload.TeacherEditDto;
import uz.platform.forestyapp.security.CurrentUser;
import uz.platform.forestyapp.service.EducationCenterService;
import uz.platform.forestyapp.service.StudentService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    @Autowired
    StudentService studentService;


    @Autowired
    EducationCenterService educationCenterService;


    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','OWNER','ADMIN','MODERATOR','FINANCIER')")
    @GetMapping
    public HttpEntity<?> getStudents(@CurrentUser User user){
        List<Student> students = studentService.getStudents(user);
        return ResponseEntity.ok().body(students);
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','OWNER','ADMIN','MODERATOR','FINANCIER')")
    @GetMapping("/{id}")
    public HttpEntity<?> getStudent(@PathVariable("id") UUID id, @CurrentUser User user){
        ApiResponse apiResponse = studentService.getStudent(id, user);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','OWNER','ADMIN','MODERATOR','FINANCIER')")
    @GetMapping("/{id}/groups")
    public HttpEntity<?> getStudentGroups(@PathVariable("id") UUID id, @CurrentUser User user){
        ApiResponse apiResponse = studentService.getStudentGroups(user, id);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','OWNER','ADMIN','MODERATOR','FINANCIER')")
    @GetMapping("/{id}/payment")
    public HttpEntity<?> getStudentPayments(@PathVariable("id") UUID id, @CurrentUser User user){
        ApiResponse apiResponse = studentService.getStudentPayments(user, id);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','OWNER','ADMIN','MODERATOR','FINANCIER')")
    @PostMapping
    public HttpEntity<?> addStudent(@Valid @RequestBody StudentDto studentDto, @CurrentUser User user){
        boolean checkPlanExpireDate = educationCenterService.checkPlanExpireDate(user.getEducationCenter().getId());
        if(!checkPlanExpireDate){
            return ResponseEntity.status(409).body("Ta'rif reja aktiv emas!");
        }
        ApiResponse apiResponse = studentService.addStudent(studentDto,user);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','OWNER','ADMIN','MODERATOR')")
    @PutMapping("/{id}")
    public HttpEntity<?> editStudent(@Valid @RequestBody StudentDto studentDto, @PathVariable("id")UUID id, @CurrentUser User user){
        boolean checkPlanExpireDate = educationCenterService.checkPlanExpireDate(user.getEducationCenter().getId());
        if(!checkPlanExpireDate){
            return ResponseEntity.status(409).body("Ta'rif reja aktiv emas!");
        }
        ApiResponse apiResponse = studentService.editStudent(id,studentDto,user);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','OWNER','ADMIN')")
    @DeleteMapping("/{id}")
    public HttpEntity<?> deleteStudent(@PathVariable("id")UUID id,@CurrentUser User user){
        boolean checkPlanExpireDate = educationCenterService.checkPlanExpireDate(user.getEducationCenter().getId());
        if(!checkPlanExpireDate){
            return ResponseEntity.status(409).body("Ta'rif reja aktiv emas!");
        }
        ApiResponse apiResponse = studentService.deleteStudent(id, user);
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
