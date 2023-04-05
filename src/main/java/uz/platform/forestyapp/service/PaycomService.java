package uz.platform.forestyapp.service;

import net.minidev.json.JSONObject;
import uz.platform.forestyapp.payload.json.PaycomRequestForm;

public interface PaycomService {
    JSONObject payWithPaycom(PaycomRequestForm requestForm, String auth);
}
