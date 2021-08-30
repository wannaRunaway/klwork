package com.kulun.energynet.requestbody;

public class AddAppointmentRequest {
    //   map.put("site_id", String.valueOf(station));
    //        map.put("car_id", String.valueOf(carid));
    private int site_id, car_id;

    public int getSite_id() {
        return site_id;
    }

    public void setSite_id(int site_id) {
        this.site_id = site_id;
    }

    public int getCar_id() {
        return car_id;
    }

    public void setCar_id(int car_id) {
        this.car_id = car_id;
    }
}
