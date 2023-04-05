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
import uz.platform.forestyapp.entity.Room;
import uz.platform.forestyapp.entity.User;
import uz.platform.forestyapp.payload.ApiResponse;
import uz.platform.forestyapp.payload.RoomDto;
import uz.platform.forestyapp.security.CurrentUser;
import uz.platform.forestyapp.service.EducationCenterService;
import uz.platform.forestyapp.service.RoomService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/room")
public class RoomController {
    @Autowired
    RoomService roomService;

    @Autowired
    EducationCenterService educationCenterService;

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','OWNER','ADMIN','MODERATOR','FINANCIER')")
    @GetMapping
    public List<Room> getRooms(@CurrentUser User user){
        UUID id = user.getEducationCenter().getId();
        return roomService.getAllRooms(id);
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','OWNER','ADMIN','MODERATOR')")
    @PostMapping
    public HttpEntity<?> addRoom(@Valid @RequestBody RoomDto roomDto,@CurrentUser User user){
        UUID educationCenterId = user.getEducationCenter().getId();
        boolean checkPlanExpireDate = educationCenterService.checkPlanExpireDate(educationCenterId);
        if(!checkPlanExpireDate){
            return ResponseEntity.status(409).body("Ta'rif reja aktiv emas!");
        }
        ApiResponse apiResponse = roomService.addRoom(roomDto, user);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','OWNER','ADMIN','MODERATOR')")
    @PutMapping("/{id}")
    public HttpEntity<?> editRoom(@PathVariable UUID id,@Valid @RequestBody RoomDto roomDto,@CurrentUser User user){
        UUID educationCenterId = user.getEducationCenter().getId();
        boolean checkPlanExpireDate = educationCenterService.checkPlanExpireDate(educationCenterId);
        if(!checkPlanExpireDate){
            return ResponseEntity.status(409).body("Ta'rif reja aktiv emas!");
        }
        ApiResponse apiResponse = roomService.editRoom(id,roomDto,user);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','OWNER','ADMIN','MODERATOR')")
    @DeleteMapping("/{id}")
    public HttpEntity<?> deleteRoom(@PathVariable UUID id,@CurrentUser User user){
        UUID educationCenterId = user.getEducationCenter().getId();
        boolean checkPlanExpireDate = educationCenterService.checkPlanExpireDate(educationCenterId);
        if(!checkPlanExpireDate){
            return ResponseEntity.status(409).body("Ta'rif reja aktiv emas!");
        }
        ApiResponse apiResponse = roomService.deleteRoom(id,user);
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
