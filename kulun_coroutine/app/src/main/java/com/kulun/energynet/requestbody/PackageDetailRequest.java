package com.kulun.energynet.requestbody;

public class PackageDetailRequest {
    //     map.put("activityId", String.valueOf(activityid));
    //        map.put("bindId", String.valueOf(bindId));
    private int activityId, bindId;

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
}
