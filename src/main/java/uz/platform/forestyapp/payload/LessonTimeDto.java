package uz.platform.forestyapp.payload;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import uz.platform.forestyapp.entity.enums.MinuteName;
import uz.platform.forestyapp.entity.enums.WeekDayName;

@Data
@AllArgsConstructor
public class LessonTimeDto {

    @NotNull
    private Integer day;

    @NotNull
    private Integer fromHour;

    @NotNull
    private Integer fromMinute;

    @NotNull
    private Integer toHour;

    @NotNull
    private Integer toMinute;
}
