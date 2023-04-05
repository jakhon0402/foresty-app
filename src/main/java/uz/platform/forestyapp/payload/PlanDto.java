package uz.platform.forestyapp.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlanDto {
    @NotBlank(message = "Bo'sh bo'lmasligi kerak!")
    private String name;
    @NotNull
    private Long price;

    @NotNull
    private Long employeesLimit;
    @NotNull
    private Long teachersLimit;
    @NotNull
    private Long studentsLimit;
    @NotNull
    private Long groupsLimit;
    @NotNull
    private Long subjectsLimit;
    @NotNull
    private Long roomsLimit;
}
