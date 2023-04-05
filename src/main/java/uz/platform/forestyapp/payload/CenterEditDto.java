package uz.platform.forestyapp.payload;

import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CenterEditDto {
    @Pattern(regexp = "^[\\p{L}_' ]+$",message = "Harflardan iborat bo'lishi kerak!")
    @NotBlank(message = "Ism bo'sh masligi kerak!")
    @Size(max = 50)
    private String name;

    @Pattern(regexp = "^[a-zA-Z_]+$",message = "Harflardan iborat bo'lishi kerak!")
    @NotBlank(message = "Username bo'sh masligi kerak!")
    @Size(max = 11)
    private String username;

    @Pattern(regexp = "^[0-9]+$",message = "Faqat sonlardan iborat bo'lishi kerak!")
    @NotBlank(message = "Telefon raqam bo'sh bo'lmasligi kerak!")
    @Size(min = 9,max = 9)
    private String phoneNumber;

    @Pattern(regexp = "^(http|https):\\/\\/(?:[\\w-]+\\.)+[a-z]{2,}(?:\\/[\\w-.\\/?%&=]*)?$",message = "Invalid url!")
    private String telegram;

    @Pattern(regexp = "^(http|https):\\/\\/(?:[\\w-]+\\.)+[a-z]{2,}(?:\\/[\\w-.\\/?%&=]*)?$",message = "Invalid url!")
    private String instagram;

    @Pattern(regexp = "^(http|https):\\/\\/(?:[\\w-]+\\.)+[a-z]{2,}(?:\\/[\\w-.\\/?%&=]*)?$",message = "Invalid url!")
    private String twitter;

    @Pattern(regexp = "^(http|https):\\/\\/(?:[\\w-]+\\.)+[a-z]{2,}(?:\\/[\\w-.\\/?%&=]*)?$",message = "Invalid url!")
    private String youtube;

    @Pattern(regexp = "^(http|https):\\/\\/(?:[\\w-]+\\.)+[a-z]{2,}(?:\\/[\\w-.\\/?%&=]*)?$",message = "Invalid url!")
    private String facebook;

    @Pattern(regexp = "^(http|https):\\/\\/(?:[\\w-]+\\.)+[a-z]{2,}(?:\\/[\\w-.\\/?%&=]*)?$",message = "Invalid url!")
    private String website;

    @Pattern(regexp = "^[A-Za-z0-9\\s '.,!?-]*$",message = "Invalid description!")
    private String description;

    @Valid
    private AddressDto address;

}
