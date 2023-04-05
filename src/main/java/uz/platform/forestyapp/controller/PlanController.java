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
import uz.platform.forestyapp.payload.PlanDto;
import uz.platform.forestyapp.service.PlanService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/plan")
public class PlanController {
    @Autowired
    PlanService planService;

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @PostMapping
    public HttpEntity<?> addPlan(@Valid @RequestBody PlanDto planDto){
        ApiResponse apiResponse = planService.addPlan(planDto);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @PutMapping("/{id}")
    public HttpEntity<?> editPlan(@Valid @RequestBody PlanDto planDto, @PathVariable("id")UUID id){
        ApiResponse apiResponse = planService.editPlan(planDto,id);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }


    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @GetMapping
    public HttpEntity<?> getPlans(){
        return ResponseEntity.ok().body(planService.getPlans());
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
