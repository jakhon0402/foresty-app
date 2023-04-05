package uz.platform.forestyapp.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ColorName {
    MAROON("Maroon","#800000","#b36666","rgb(128,0,0)"),
    ORANGE("Orange","#ff7400","#ffac66","rgb(255,116,0)"),
    AMBER("Amber","#febd00","#fed766","rgb(254,189,0)"),
    OCEAN_BLUE("Ocean blue","#2645e0","#67ade7","rgb(38,69,224)"),
    FOREST("Forest","#228b22","#66ca97","rgb(34,139,34)"),
    LAVENDER("Lavender","#9e73c7","#c5abdd","rgb(158,115,199)"),
    WALNUT("Walnut","#6f432a","#a98e7f","rgb(111,67,42)"),
    MIDNIGHT("Midnight","#1c1d54","#7d7ba6","rgb(28,29,84)");


    @JsonProperty
    private String colorName;

    @JsonProperty
    private String hexCode;

    @JsonProperty
    private String lightHexCode;

    @JsonProperty
    private String rgbCode;

    @JsonCreator
    ColorName(String colorName,String hexCode,String lightHexCode,String rgbCode) {
        this.colorName = colorName;
        this.hexCode = hexCode;
        this.lightHexCode = lightHexCode;
        this.rgbCode = rgbCode;
    }
}
