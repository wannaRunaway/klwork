package com.kulun.energynet.model;

import java.io.Serializable;

public class TboxModel implements Serializable {
    // if (json.has("vin")) {
    // if (json.has("total_miles")) {
    // if (json.has("car_type")) {
    private String vin;
    private int car_type;
    private double total_miles;

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public int getCar_type() {
        return car_type;
    }

    public void setCar_type(int car_type) {
        this.car_type = car_type;
    }

    public double getTotal_miles() {
        return total_miles;
    }

    public void setTotal_miles(double total_miles) {
        this.total_miles = total_miles;
    }
}
