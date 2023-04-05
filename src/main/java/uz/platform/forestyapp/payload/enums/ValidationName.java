package uz.platform.forestyapp.payload.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;


@AllArgsConstructor
public enum ValidationName {
    USERNAME("^([A-z][0-9]+)$","Username faqat harflar yoki avval lotin harflari keyin raqamlardan iborat bo'lishi kerak!"),
    PASSWORD("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$","Yaroqsiz parol kiritildi!"),
    EMAIL("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\\\.[A-Za-z0-9-]+)*(\\\\.[A-Za-z]{2,})$","Yaroqsiz email kiritilgan!"),
    NUMBERS("^[0-9]*$","Faqat sonlardan iborat bo'lishi kerak!"),
    LETTERS("^\\p{L}+$","Harflardan iborat bo'lishi kerak!"),
    CAPITAL_LETTERS("^[A-Z]+$","Bosh harflardan iborat bo'lishi kerak!");

    @Getter
    public final String regex;

    @Getter
    public final String message;


}
