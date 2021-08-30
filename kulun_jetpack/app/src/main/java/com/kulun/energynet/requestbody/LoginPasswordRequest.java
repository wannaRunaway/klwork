package com.kulun.energynet.requestbody;

public class LoginPasswordRequest {
//        hashMap.put("phone", phone);
//        hashMap.put("password", MD5.encode(code));
    private String phone, password;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
