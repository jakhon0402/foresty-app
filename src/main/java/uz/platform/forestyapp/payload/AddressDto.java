package uz.platform.forestyapp.payload;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import uz.platform.forestyapp.entity.enums.RegionName;

@Data
@AllArgsConstructor
public class AddressDto {
    @EnumNamePattern(regexp = "^\\p{L}+$")
    private RegionName region;

    @Pattern(regexp = "^[\\p{L}_',. ]+$",message = "Harflardan iborat bo'lishi kerak!")
    @Size(max=30)
    private String districtOrCity;

    @Pattern(regexp = "^$|^[A-Za-z0-9 '_.-]*[A-Za-z0-9 '_.-][A-Za-z0-9 _-]*$",message = "Harflar va sonlardan iborat bo'lishi kerak!")
    @Size(max=30)
    private String street;
}
