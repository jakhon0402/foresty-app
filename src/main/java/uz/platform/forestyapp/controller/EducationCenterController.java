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
import uz.platform.forestyapp.payload.ApiResponse;
import uz.platform.forestyapp.payload.EducationCenterDto;
import uz.platform.forestyapp.service.EducationCenterService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/educationCenter")
public class EducationCenterController {
    @Autowired
    EducationCenterService educationCenterService;

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @PostMapping
    public HttpEntity<?> addEducationCenter(@Valid @RequestBody EducationCenterDto educationCenterDto){
        ApiResponse apiResponse = educationCenterService.addEducationCenter(educationCenterDto);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @GetMapping
    public HttpEntity<?> getEducationCenters(){
        return ResponseEntity.ok().body(educationCenterService.getEducationCenters());
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
