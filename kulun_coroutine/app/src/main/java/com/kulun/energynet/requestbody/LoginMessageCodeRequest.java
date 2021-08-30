package com.kulun.energynet.requestbody;

public class LoginMessageCodeRequest {
    //        map.put("phone", phone);
    //        map.put("sms_code", code);
    private String phone, sms_code;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSms_code() {
        return sms_code;
    }

    public void setSms_code(String sms_code) {
        this.sms_code = sms_code;
    }
}
