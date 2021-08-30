package com.kulun.energynet.requestbody;

public class PayRequest {
    private int type, pay_type, pay_mode;
    private String amount;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPay_type() {
        return pay_type;
    }

    public void setPay_type(int pay_type) {
        this.pay_type = pay_type;
    }

    public int getPay_mode() {
        return pay_mode;
    }

    public void setPay_mode(int pay_mode) {
        this.pay_mode = pay_mode;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
