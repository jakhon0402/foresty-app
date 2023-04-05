package uz.platform.forestyapp.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ActivityName {
    ADD_EMPLOYEE("yangi xodim qo'shdi.",CategoryName.EMPLOYEE),
    EDIT_EMPLOYEE("xodimni o'zgartirdi.",CategoryName.EMPLOYEE),
    DELETE_EMPLOYEE("xodimni o'chirdi.",CategoryName.EMPLOYEE),
    FINISH_EMPLOYEE_WORK("xodimning ishini tugatdi.",CategoryName.EMPLOYEE),

    ADD_TEACHER("yangi o'qituvchi qo'shdi.",CategoryName.TEACHER),
    EDIT_TEACHER("o'qituvchini o'zgartirdi.",CategoryName.TEACHER),
    DELETE_TEACHER("o'qituvchini o'chirdi.",CategoryName.TEACHER),
    FINISH_TEACHER_WORK("o'qituvchining ishini tugatdi.",CategoryName.TEACHER),

    ADD_STUDENT("yangi o'quvchi qo'shdi.",CategoryName.STUDENT),
    EDIT_STUDENT("o'quvchini o'zgartirdi.",CategoryName.STUDENT),
    DELETE_STUDENT("o'quvchini o'chirdi.",CategoryName.STUDENT),
    GRADUATE_STUDENT("o'quvchini o'chirdi.",CategoryName.STUDENT),

    ADD_GROUP("yangi guruh qo'shdi.",CategoryName.GROUP),
    ADD_STUDENT_TO_GROUP("guruhga yangi o'quvchi qo'shdi.",CategoryName.GROUP),
    REMOVE_STUDENT_FROM_GROUP("guruhdan o'quvchini chetlashtirdi.",CategoryName.GROUP),

    EDIT_GROUP("guruh o'zgartirdi.",CategoryName.GROUP),
    DELETE_GROUP("guruh o'chirdi.",CategoryName.GROUP),
    FINISH_GROUP("guruhni to'xtatdi.",CategoryName.GROUP),

    COMPLETE_GROUP("guruhni tugatdi.",CategoryName.GROUP),

    RECEIVE_PAYMENT("to'lov qabul qildi.",CategoryName.PAYMENT),
    PAYMENT_EMPLOYEE("xodimga to'lov amalga oshirdi.",CategoryName.PAYMENT),
    PAYMENT_TEACHER("o'qituvchiga to'lov amalga oshirdi.",CategoryName.PAYMENT),

    ADD_ROOM("yangi xona qo'shdi.",CategoryName.ROOM),
    EDIT_ROOM("xonani o'zgartirdi.",CategoryName.ROOM),
    DELETE_ROOM("guruh o'chirdi.",CategoryName.ROOM),

    ADD_SUBJECT("yangi fan qo'shdi.",CategoryName.SUBJECT),
    EDIT_SUBJECT("fan o'zgartirdi.",CategoryName.SUBJECT),
    DELETE_SUBJECT("fan o'chirdi.",CategoryName.SUBJECT);

    @JsonProperty
    private final String text;

    @JsonProperty
    private final CategoryName categoryName;

    @JsonCreator
    ActivityName(String text, CategoryName categoryName) {
        this.text = text;
        this.categoryName = categoryName;
    }
}
