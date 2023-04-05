package uz.platform.forestyapp.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CurrentPlanStatus {
    ACTIVE("Ta'rif reja aktiv","ACTIVE"),
    INACTIVE("Ta'rif reja aktiv emas!","INACTIVE");

    @JsonProperty
    private final String description;

    @JsonProperty
    private final String name;

    @JsonCreator
    CurrentPlanStatus(String description,String name) {
        this.description = description;
        this.name = name;
    }
}
