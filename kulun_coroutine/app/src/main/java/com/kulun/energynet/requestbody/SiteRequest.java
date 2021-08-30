package com.kulun.energynet.requestbody;

public class SiteRequest {
    //   String json = JsonSplice.leftparent + JsonSplice.yin + "longitude" + JsonSplice.yinandmao + longitude + JsonSplice.dou +
    //                JsonSplice.yin + "latitude" + JsonSplice.yinandmao + latitude + JsonSplice.dou +
    //                JsonSplice.yin + "type" + JsonSplice.yinandmao + type + JsonSplice.dou +
    //                JsonSplice.yin + "city_Id" + JsonSplice.yinandmao + cityId + JsonSplice.dou +
    //                JsonSplice.yin + "search" + JsonSplice.yinandmao + JsonSplice.yin + search + JsonSplice.yin + JsonSplice.rightparent;
    private double longitude,latitude;
    private int city_Id, type, use, aId;
    private String search;

    public int getaId() {
        return aId;
    }

    public void setaId(int aId) {
        this.aId = aId;
    }

    public int getUse() {
        return use;
    }

    public void setUse(int use) {
        this.use = use;
    }

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCity_Id() {
        return city_Id;
    }

    public void setCity_Id(int city_Id) {
        this.city_Id = city_Id;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
