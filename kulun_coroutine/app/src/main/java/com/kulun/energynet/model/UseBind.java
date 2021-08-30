package com.kulun.energynet.model;

import java.io.Serializable;

public class UseBind implements Serializable {
    /*		"use_bind": {
             "id": 16024,
             "car_id": 12413,
             "car_type": "ER30",
             "business_type": 5,
             "plate_number": "浙AJ11P5",
             "bind_status": 1,
             "left_mile": 0,
             "month_mile": 0,
             "inuse": true,
             "battery_count": 2,
             "battery_status": true,
             "soc": 72,
             "first_exchange_sites": ""
         },*/
    private int id, car_id, bind_status, left_mile, month_mile, soc, battery_count, business_type;
    private String plate_number, car_type, first_exchange_sites;
    private boolean inuse, battery_status;

    public int getBusiness_type() {
        return business_type;
    }

    public void setBusiness_type(int business_type) {
        this.business_type = business_type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCar_id() {
        return car_id;
    }

    public void setCar_id(int car_id) {
        this.car_id = car_id;
    }

    public int getBind_status() {
        return bind_status;
    }

    public void setBind_status(int bind_status) {
        this.bind_status = bind_status;
    }

    public int getLeft_mile() {
        return left_mile;
    }

    public void setLeft_mile(int left_mile) {
        this.left_mile = left_mile;
    }

    public int getMonth_mile() {
        return month_mile;
    }

    public void setMonth_mile(int month_mile) {
        this.month_mile = month_mile;
    }

    public int getSoc() {
        return soc;
    }

    public void setSoc(int soc) {
        this.soc = soc;
    }

    public int getBattery_count() {
        return battery_count;
    }

    public void setBattery_count(int battery_count) {
        this.battery_count = battery_count;
    }

    public String getPlate_number() {
        return plate_number;
    }

    public void setPlate_number(String plate_number) {
        this.plate_number = plate_number;
    }

    public boolean isInuse() {
        return inuse;
    }

    public void setInuse(boolean inuse) {
        this.inuse = inuse;
    }

    public boolean isBattery_status() {
        return battery_status;
    }

    public void setBattery_status(boolean battery_status) {
        this.battery_status = battery_status;
    }

    public String getCar_type() {
        return car_type;
    }

    public void setCar_type(String car_type) {
        this.car_type = car_type;
    }

    public String getFirst_exchange_sites() {
        return first_exchange_sites;
    }

    public void setFirst_exchange_sites(String first_exchange_sites) {
        this.first_exchange_sites = first_exchange_sites;
    }
}
