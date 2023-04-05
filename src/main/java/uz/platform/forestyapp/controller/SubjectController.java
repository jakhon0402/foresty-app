package uz.platform.forestyapp.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import uz.platform.forestyapp.entity.Subject;
import uz.platform.forestyapp.entity.User;
import uz.platform.forestyapp.payload.ApiResponse;
import uz.platform.forestyapp.payload.SubjectDto;
import uz.platform.forestyapp.security.CurrentUser;
import uz.platform.forestyapp.service.EducationCenterService;
import uz.platform.forestyapp.service.SubjectService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/subject")
public class SubjectController {

    @Autowired
    SubjectService subjectService;

    @Autowired
    EducationCenterService educationCenterService;

    @GetMapping("/top")
    public HttpEntity<?> getTopSubjects(@CurrentUser User user){
        ApiResponse apiResponse = subjectService.getTopSubjects(user);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @GetMapping("/platform")
    public HttpEntity<?> getForestySubjects(){
        List<Subject> forestySubjects = subjectService.getForestySubjects();
        return ResponseEntity.ok().body(forestySubjects);
    }

    @GetMapping("/private")
    public HttpEntity<?> getPrivateSubjects(@CurrentUser User user){
        List<Subject> privateSubjects = subjectService.getPrivateSubjects(user.getEducationCenter().getId());
        return ResponseEntity.ok().body(privateSubjects);
    }

    @GetMapping
    public HttpEntity<?> getAllSubjects(@CurrentUser User user){
        List<Subject> allSubjects = subjectService.getPrivateSubjects(user.getEducationCenter().getId());
        allSubjects.addAll(subjectService.getForestySubjects());
        return ResponseEntity.ok().body(allSubjects);
    }

    @PostMapping
    public HttpEntity<?> addSubject(@Valid @RequestBody SubjectDto subjectDto, @CurrentUser User user){
        boolean checkPlanExpireDate = educationCenterService.checkPlanExpireDate(user.getEducationCenter().getId());
        if(!checkPlanExpireDate){
            return ResponseEntity.status(409).body("Ta'rif reja aktiv emas!");
        }
        ApiResponse apiResponse = subjectService.addSubject(subjectDto, user);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @PutMapping("/{id}")
    public HttpEntity<?> editSubject(@PathVariable("id")UUID id, @Valid @RequestBody SubjectDto subjectDto, @CurrentUser User user){
        boolean checkPlanExpireDate = educationCenterService.checkPlanExpireDate(user.getEducationCenter().getId());
        if(!checkPlanExpireDate){
            return ResponseEntity.status(409).body("Ta'rif reja aktiv emas!");
        }
        ApiResponse apiResponse = subjectService.editSubject(id,subjectDto, user);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @DeleteMapping("/{id}")
    public HttpEntity<?> deleteSubject(@PathVariable("id")UUID id, @CurrentUser User user){
        boolean checkPlanExpireDate = educationCenterService.checkPlanExpireDate(user.getEducationCenter().getId());
        if(!checkPlanExpireDate){
            return ResponseEntity.status(409).body("Ta'rif reja aktiv emas!");
        }
        ApiResponse apiResponse = subjectService.deleteSubject(id, user);
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
