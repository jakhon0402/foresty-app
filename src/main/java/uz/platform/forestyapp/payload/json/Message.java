package uz.platform.forestyapp.payload.json;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String en = "";
    private String ru = "";
    private String uz = "";
}
