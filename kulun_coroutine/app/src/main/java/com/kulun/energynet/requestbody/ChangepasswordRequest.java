package com.kulun.energynet.requestbody;

public class ChangepasswordRequest {
    //map.put("password", MD5.encode(oldpassword));
    //        map.put("new_password", MD5.encode(newpassword));
    private String password, new_password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNew_password() {
        return new_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }
}
