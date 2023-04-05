package uz.platform.forestyapp.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PlanLimitName {
    EMPLOYEE("Xodimlar soni",""),
    TEACHER("O'qituvchilar soni",""),
    STUDENT("O'quvchilar soni",""),
    GROUP("Guruhlar soni",""),
    SUBJECT("Fanlar soni",""),
    ROOM("Xonalar soni","");

    @JsonProperty
    private String name;
    @JsonProperty
    private String description;

    @JsonCreator
    PlanLimitName(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
