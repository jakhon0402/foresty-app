package uz.platform.forestyapp.payload;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@Data
@AllArgsConstructor
public class EmployeeDto {

    @NotNull
    private long salary;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date jobStartsDate;

    @Valid
    private UserDto employee;

    @NotNull
    private Integer testDaysCount;

    @Pattern(regexp = "^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){3,18}[a-zA-Z0-9]$",message = "Bosh harflardan iborat bo'lishi kerak!")
    @NotBlank
    private String role;

}
