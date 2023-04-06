package uz.platform.forestyapp.controller;

import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uz.platform.forestyapp.payload.json.PaycomRequestForm;
import uz.platform.forestyapp.service.PaycomService;

@RequestMapping("/api/payme")
@RestController
public class PaymeController {
    @Autowired
    private PaycomService ipaycomService;

    @PostMapping
    JSONObject post(@RequestBody PaycomRequestForm requestForm,
                    @RequestHeader("Authorization") String authorization) {
        return ipaycomService.payWithPaycom(requestForm, authorization);
    }
}
