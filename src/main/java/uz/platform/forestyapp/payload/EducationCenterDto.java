package uz.platform.forestyapp.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import uz.platform.forestyapp.entity.CurrentPlan;

@Data
@AllArgsConstructor
public class EducationCenterDto {
    @Size(max=20)
    @NotBlank(message = "Bo'sh bo'lmasligi kerak!")
    @Pattern(regexp = "^[\\p{L}_ ]+$",message = "Harflardan iborat bo'lishi kerak!")
    private String name;

    @Pattern(regexp = "^[0-9]*$",message = "Faqat sonlardan iborat bo'lishi kerak!")
    @NotBlank
    @Size(min=9)
    private String phoneNumber;

    @Pattern(regexp = "^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){3,18}[a-zA-Z0-9]$",message = "Username faqat harflar yoki avval lotin harflari keyin raqamlardan iborat bo'lishi kerak!")
    @NotBlank
    @Size(min=3,max = 13)
    private String username;

    private CurrentPlanDto currentPlan;

    private AddressDto address;

    private UserDto owner;
}
