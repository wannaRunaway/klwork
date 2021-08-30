package com.kulun.energynet.model;

import java.io.Serializable;
import java.util.List;

public class ConsumeListModel implements Serializable {
    //   List<BillDetail> list = Mygson.getInstance().fromJson(json.get("detail").getAsJsonArray(), new TypeToken<List<BillDetail>>() {
    //                        }.getType());
    //                        String name = json.get("siteName").getAsString();
    //                        String fare = json.get("realFare").getAsString();
    //                        int recordId = json.get("recordId").getAsInt();
    private String siteName, realFare;
    private int recordId;
    private List<BillDetail> detail;

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getRealFare() {
        return realFare;
    }

    public void setRealFare(String realFare) {
        this.realFare = realFare;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public List<BillDetail> getDetail() {
        return detail;
    }

    public void setDetail(List<BillDetail> detail) {
        this.detail = detail;
    }
}
