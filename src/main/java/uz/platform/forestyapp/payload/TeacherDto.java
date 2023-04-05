package uz.platform.forestyapp.payload;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class TeacherDto {
    List<UUID> subjectIds;
    @Valid
    UserDto teacher;
}
