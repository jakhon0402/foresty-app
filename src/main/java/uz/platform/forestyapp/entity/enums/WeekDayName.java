package uz.platform.forestyapp.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum WeekDayName {
    MONDAY("Dushanba","Du.",0),
    TUESDAY("Seshanba","Se.",1),
    WEDNESDAY("Chorshanba","Chor.",2),
    THURSDAY("Payshanba","Pa.",3),
    FRIDAY("Juma","Ju.",4),
    SATURDAY("Shanba","Sha.",5),
    SUNDAY("Yakshanba","Yak.",6);

    @JsonProperty
    public String uz;

    @JsonProperty
    public String abb;

    @JsonProperty
    public Integer index;

    @JsonCreator
    WeekDayName(String uz,String abb,Integer index) {
        this.uz = uz;this.abb=abb;this.index=index;
    }
}
