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
import uz.platform.forestyapp.entity.User;
import uz.platform.forestyapp.payload.ApiResponse;
import uz.platform.forestyapp.payload.EmployeeDto;
import uz.platform.forestyapp.payload.EmployeeEditDto;
import uz.platform.forestyapp.security.CurrentUser;
import uz.platform.forestyapp.service.EducationCenterService;
import uz.platform.forestyapp.service.EmployeeService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {
    @Autowired
    EmployeeService employeeService;

    @Autowired
    EducationCenterService educationCenterService;

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','OWNER','ADMIN','MODERATOR')")
    @GetMapping
    public HttpEntity<?> getEmployees(@CurrentUser User user){
        return ResponseEntity.ok().body(employeeService.getEmployees(user));
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','OWNER','ADMIN','MODERATOR')")
    @GetMapping("/{id}")
    public HttpEntity<?> getEmployee(@CurrentUser User user,@PathVariable("id")UUID id){
        return ResponseEntity.ok().body(employeeService.getEmployee(user,id));
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','OWNER','ADMIN','MODERATOR')")
    @GetMapping("/{id}/payment")
    public HttpEntity<?> getEmployeePayments(@CurrentUser User user,@PathVariable("id")UUID id){
        return ResponseEntity.ok().body(employeeService.getEmployeePayments(user,id));
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','OWNER','ADMIN','MODERATOR')")
    @PostMapping
    public HttpEntity<?> addEmployee(@Valid @RequestBody EmployeeDto employeeDto, @CurrentUser User user){
        boolean checkPlanExpireDate = educationCenterService.checkPlanExpireDate(user.getEducationCenter().getId());
        if(!checkPlanExpireDate){
            return ResponseEntity.status(409).body("Ta'rif reja aktiv emas!");
        }
        ApiResponse apiResponse = employeeService.addEmployee(employeeDto,user);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','OWNER')")
    @PutMapping("/{id}")
    public HttpEntity<?> editEmployee(@Valid @RequestBody EmployeeEditDto employeeDto, @CurrentUser User user,@PathVariable("id")UUID id){
        boolean checkPlanExpireDate = educationCenterService.checkPlanExpireDate(user.getEducationCenter().getId());
        if(!checkPlanExpireDate){
            return ResponseEntity.status(409).body("Ta'rif reja aktiv emas!");
        }
        ApiResponse apiResponse = employeeService.editEmployee(employeeDto,user,id);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','OWNER','ADMIN')")
    @PostMapping("/block/{id}")
    public HttpEntity<?> blockEmployee(@PathVariable("id")UUID id,@CurrentUser User user){
        boolean checkPlanExpireDate = educationCenterService.checkPlanExpireDate(user.getEducationCenter().getId());
        if(!checkPlanExpireDate){
            return ResponseEntity.status(409).body("Ta'rif reja aktiv emas!");
        }
        ApiResponse apiResponse = employeeService.blockEmployee(id, user.getEducationCenter().getId());
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','OWNER','ADMIN','MODERATOR')")
    @PostMapping("/finishWork/{id}")
    public HttpEntity<?> finishEmployeeWork(@PathVariable("id")UUID id,@CurrentUser User user){
        boolean checkPlanExpireDate = educationCenterService.checkPlanExpireDate(user.getEducationCenter().getId());
        if(!checkPlanExpireDate){
            return ResponseEntity.status(409).body("Ta'rif reja aktiv emas!");
        }
        ApiResponse apiResponse = employeeService.finishWork(id, user);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','OWNER','ADMIN')")
    @DeleteMapping("/{id}")
    public HttpEntity<?> deleteEmployee(@PathVariable("id")UUID id,@CurrentUser User user){
        boolean checkPlanExpireDate = educationCenterService.checkPlanExpireDate(user.getEducationCenter().getId());
        if(!checkPlanExpireDate){
            return ResponseEntity.status(409).body("Ta'rif reja aktiv emas!");
        }
        ApiResponse apiResponse = employeeService.deleteEmployee(id, user);
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
