package uz.platform.forestyapp.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.platform.forestyapp.entity.enums.RoleName;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRes {
    private String accessToken;
    private String refreshToken;

}
