package uz.platform.forestyapp.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PaymentStatusName {
    PROCESS("Jarayonda","PROCESS"),
    PAID("To'langan","PAID"),
    UNPAID("To'lanmagan","UNPAID");

    @JsonProperty
    private String uz;

    @JsonProperty
    private String name;

    @JsonCreator
    PaymentStatusName(String uz,String name) {
        this.uz = uz;
        this.name = name;
    }
}
