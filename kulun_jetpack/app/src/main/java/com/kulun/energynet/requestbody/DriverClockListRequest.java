package com.kulun.energynet.requestbody;

public class DriverClockListRequest {
    //     map.put("bindId", String.valueOf(useBind.getId()));
    //        map.put("page", String.valueOf(pageNo));
    private int bindId, page;

    public int getBindId() {
        return bindId;
    }

    public void setBindId(int bindId) {
        this.bindId = bindId;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
