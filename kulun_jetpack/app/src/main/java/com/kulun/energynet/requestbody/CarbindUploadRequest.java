package com.kulun.energynet.requestbody;

public class CarbindUploadRequest {
    // String json = JsonSplice.leftparent + JsonSplice.yin + "plate" + JsonSplice.yinandmao + JsonSplice.yin + carplate + JsonSplice.yinanddou +
    //                JsonSplice.yin + "miles" + JsonSplice.yinandmao + mile + JsonSplice.dou +
    //                JsonSplice.yin + "photos" + JsonSplice.yinandmao + JsonSplice.yin + string + JsonSplice.yin + JsonSplice.rightparent;
    private String plate, photos;
    private int miles;

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getPhotos() {
        return photos;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
    }

    public int getMiles() {
        return miles;
    }

    public void setMiles(int miles) {
        this.miles = miles;
    }
}
