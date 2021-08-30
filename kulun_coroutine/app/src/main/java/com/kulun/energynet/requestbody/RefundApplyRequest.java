package com.kulun.energynet.requestbody;

public class RefundApplyRequest {
    private double amount;
    private String reason;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
