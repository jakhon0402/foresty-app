package uz.platform.forestyapp.payload;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;

@Data
@AllArgsConstructor
public class StudentDto {
    @Pattern(regexp = "^[\\p{L}_' ]*$",message = "Harflardan iborat bo'lishi kerak!")
    @NotBlank(message = "Ism bo'sh masligi kerak!")
    @Size(max = 20)
    private String firstName;

    @Pattern(regexp = "^[\\p{L}_' ]*$",message = "Harflardan iborat bo'lishi kerak!")
    @NotBlank(message = "Familiya bo'sh bulmasligi kerak")
    @Size(max=20)
    private String lastName;

    @Pattern(regexp = "^[\\p{L}_' ]*$", message = "Harflardan iborat bo'lishi kerak!")
    private String fatherName;

    @Pattern(regexp = "^[0-9]*$",message = "Faqat sonlardan iborat bo'lishi kerak!")
    @NotBlank
    @Size(max=9)
    private String phoneNumber;

    @Pattern(regexp = "^$|^[0-9]*$",message = "Faqat sonlardan iborat bo'lishi kerak!")
    @Size(max=9)
    private String parentPhoneNumber;

    @Pattern(regexp = "^$|(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\n" +
            "\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\n" +
            "\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:\n" +
            "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])",message = "Yaroqsiz email kiritildi!")
    private String email;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date birthDate;

    @Valid
    private AddressDto address;
}
