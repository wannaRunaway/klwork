package com.kulun.energynet.requestbody;

public class ReservationRequest {
    private double longitude, latitude;
    private int battery_cnt, city_id;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getBattery_cnt() {
        return battery_cnt;
    }

    public void setBattery_cnt(int battery_cnt) {
        this.battery_cnt = battery_cnt;
    }

    public int getCity_id() {
        return city_id;
    }

    public void setCity_id(int city_id) {
        this.city_id = city_id;
    }
}
