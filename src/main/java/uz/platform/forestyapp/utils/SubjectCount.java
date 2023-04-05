package uz.platform.forestyapp.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.platform.forestyapp.entity.Subject;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubjectCount {
    private Subject subject;
    private Long count;
}
