package com.kulun.energynet.requestbody;

public class ClockRequest {
    // map.put("bindId", String.valueOf(useBind.getId()));
    //        map.put("driverClockType", type+"");//0上班 1下班
    //        map.put("soc", String.valueOf(soc));
    //        map.put("carMile", String.valueOf(mile));
    private int bindId, driverClockType, soc, carMile;

    public int getBindId() {
        return bindId;
    }

    public void setBindId(int bindId) {
        this.bindId = bindId;
    }

    public int getDriverClockType() {
        return driverClockType;
    }

    public void setDriverClockType(int driverClockType) {
        this.driverClockType = driverClockType;
    }

    public int getSoc() {
        return soc;
    }

    public void setSoc(int soc) {
        this.soc = soc;
    }

    public int getCarMile() {
        return carMile;
    }

    public void setCarMile(int carMile) {
        this.carMile = carMile;
    }
}
