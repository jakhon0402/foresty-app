package uz.platform.forestyapp.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignInDto {
    @NotBlank(message = "Email bo'sh bo'lmasligi kerak!")
    private String email;

    @NotBlank(message = "Parol bo'sh bo'lmasligi kerak!")
    private String password;
}
