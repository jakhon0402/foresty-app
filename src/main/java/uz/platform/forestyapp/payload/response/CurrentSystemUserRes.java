package uz.platform.forestyapp.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uz.platform.forestyapp.entity.Attachment;
import uz.platform.forestyapp.entity.EducationCenter;
import uz.platform.forestyapp.entity.enums.RoleName;

@Data
@Builder
@AllArgsConstructor
public class CurrentSystemUserRes {
    private String firstName;
    private String lastName;
    private EducationCenter educationCenter;
    private RoleName role;
    private Attachment avatar;
}
