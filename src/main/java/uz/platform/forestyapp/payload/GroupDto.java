package uz.platform.forestyapp.payload;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import uz.platform.forestyapp.entity.enums.GroupTypeName;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class GroupDto {
    @NotBlank(message = "Guruh nomi bo'sh bo'lmasligi kerak!")
    @Pattern(regexp = "^[\\p{L}_' ]*$",message = "Harflardan iborat bo'lishi kerak!")
    private String name;

    @EnumNamePattern(regexp = "^\\p{L}+$")
    private GroupTypeName groupType;

    @NotNull
    private UUID teacherId;

    @NotNull
    private UUID subjectId;

    @NotNull
    private UUID roomId;

    private Date startsDate;

    private Integer testDayCount;

    @Valid
    private List<LessonTimeDto> lessonTimes;

    private Integer agreement;

    private Integer duration;

    @NotNull
    private Long price;
}
