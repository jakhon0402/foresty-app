package uz.platform.forestyapp.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import uz.platform.forestyapp.entity.enums.ColorName;

@Data
@AllArgsConstructor
public class SubjectDto {
    @NotBlank(message = "Fan nomi bo'sh bo'lmasligi kerak!")
    private String name;

    private ColorName color;
}
