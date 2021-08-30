package com.kulun.energynet.requestbody;

public class SmsRequest {
    private int sms_type;
    private String phone;

    public int getSms_type() {
        return sms_type;
    }

    public void setSms_type(int sms_type) {
        this.sms_type = sms_type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
