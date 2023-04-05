package uz.platform.forestyapp.payload.json;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalInfo {
    private UUID orderId;

    private Integer orderSum;

    private String site = "center.foresty.uz";

    public AdditionalInfo(UUID orderId, Integer orderSum) {
        this.orderId = orderId;
        this.orderSum = orderSum;
    }
}
