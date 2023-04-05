package uz.platform.forestyapp.payload;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class TeacherEditDto {
    @Pattern(regexp = "^[\\p{L}_' ]*$",message = "Harflardan iborat bo'lishi kerak!")
    @NotBlank(message = "Ism bo'sh masligi kerak!")
    @Size(max = 20)
    private String firstName;

    @Pattern(regexp = "^[\\p{L}_' ]*$",message = "Harflardan iborat bo'lishi kerak!")
    @NotBlank(message = "Familiya bo'sh bulmasligi kerak")
    @Size(max=20)
    private String lastName;

    @Pattern(regexp = "^[\\p{L}_' ]*$", message = "Otasining ismi harflardan iborat bo'lishi kerak!")
    private String fatherName;

    @Pattern(regexp = "^[0-9]*$",message = "Faqat sonlardan iborat bo'lishi kerak!")
    @NotBlank(message = "Telefon raqam bo'sh bo'lmasligi kerak!")
    @Size(min = 9,max = 9)
    private String phoneNumber;

    @Pattern(regexp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\n" +
            "\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\n" +
            "\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:\n" +
            "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])",message = "Yaroqsiz email kiritildi!")
    private String email;

    @Pattern(regexp = "^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){3,18}[a-zA-Z0-9]$",message = "Username faqat harflar yoki avval lotin harflari keyin raqamlardan iborat bo'lishi kerak!")
    private String username;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$", message = "Yaroqsiz parol kiritildi!")
    private String newPassword;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date birthDate;

    @Valid
    private AddressDto address;

    private List<UUID> subjectIds;
}
