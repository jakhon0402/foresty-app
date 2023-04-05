package uz.platform.forestyapp.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ForestySubjectName {
    MATEMATIKA(ColorName.FOREST),
    FIZIKA(ColorName.LAVENDER),
    INGLIZ_TILI(ColorName.MAROON),
    BIOLOGIYA(ColorName.AMBER),
    GEOGRAFIYA(ColorName.OCEAN_BLUE),
    KIMYO(ColorName.MIDNIGHT),
    ONA_TILI_VA_ADABIYOT(ColorName.ORANGE),
    TARIX(ColorName.WALNUT);

    @JsonProperty
    private ColorName color;

    @JsonCreator
    ForestySubjectName(ColorName color) {
        this.color = color;
    }
}
