package uz.platform.forestyapp.payload;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoomDto {
    @NotNull(message = "Xona raqami bo'sh bo'lmasligi kerak!")
    private Long roomNumber;

    @Size(max = 20)
    private String roomName;

    @NotNull(message = "Qavat bo'sh bo'lmasligi kerak!")
    private Integer floor;
}
