package uz.platform.forestyapp.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum MinuteName {
    ZERO("00",0),
    FIFTEEN("15",1),
    HALF("30",2),
    FOURTYFIVE("45",3);

    @JsonProperty
    public String minute;

    @JsonProperty
    public Integer index;

    @JsonCreator
    MinuteName(String minute,Integer index) {
        this.minute = minute;
        this.index=index;
    }
}
