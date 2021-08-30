package com.kulun.energynet.requestbody;

public class PackageRequest {
    private int activityId, bindId, buyMonths;
    private boolean buyCurrMonth;
    private String startDate;
    private double amount;

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public int getBindId() {
        return bindId;
    }

    public void setBindId(int bindId) {
        this.bindId = bindId;
    }

    public int getBuyMonths() {
        return buyMonths;
    }

    public void setBuyMonths(int buyMonths) {
        this.buyMonths = buyMonths;
    }

    public boolean isBuyCurrMonth() {
        return buyCurrMonth;
    }

    public void setBuyCurrMonth(boolean buyCurrMonth) {
        this.buyCurrMonth = buyCurrMonth;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
