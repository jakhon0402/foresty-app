package uz.platform.forestyapp.entity.enums;

public enum RoleName {
    SUPER_ADMIN("Project owner"),
    OWNER("Uquv markaz egasi"),
    ADMIN("Barcha huquqlarga ega xodim"),
    MODERATOR("Moliya ishlaridan tashqari barcha huquqlarga ega xodim"),
    FINANCIER("To'lovlarni boshqaruvchi xodim"),
    TEACHER("O'quv markaz o'qituvchisi"),
    STUDENT("O'quv markaz o'quvchisi"),
    USER("Oddiy foydalanuvchi");

    private String description;

    RoleName(String description) {
        this.description=description;
    }
}
