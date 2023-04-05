package uz.platform.forestyapp.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CurrentPlanDto {
    private UUID planId;

   @NonNull
    private Integer expireDate;
}
