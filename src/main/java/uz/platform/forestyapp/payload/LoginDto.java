package uz.platform.forestyapp.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class LoginDto {
    @NotBlank(message = "Username bo'sh bo'lmasligi kerak!")
    private String username;

    @NotBlank(message = "Parol bo'sh bo'lmasligi kerak!")
    private String password;
}
