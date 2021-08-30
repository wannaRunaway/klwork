package com.kulun.energynet.requestbody;

public class ResetpasswordRequest {
    // map.put("phone", myphones);
    //        map.put("sms_code", code);
    //        map.put("password", MD5.encode(passwordNew));
    private String phone, sms_code, password;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
