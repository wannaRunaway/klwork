package com.kulun.energynet.requestbody;

public class StatuploadModel {
    //       String json = JsonSplice.leftparent+JsonSplice.yin+"exId"+JsonSplice.yinandmao+exId+JsonSplice.dou+
    //                        JsonSplice.yin+"siteId"+JsonSplice.yinandmao+siteid+JsonSplice.dou+
    //                        JsonSplice.yin+"starNumber"+JsonSplice.yinandmao+starsNumber+JsonSplice.dou+
    //                        JsonSplice.yin+"content"+JsonSplice.yinandmao+JsonSplice.yin+content+JsonSplice.yin+JsonSplice.rightparent;
    private int exId, siteId, starNumber;
    private String content;

    public int getExId() {
        return exId;
    }

    public void setExId(int exId) {
        this.exId = exId;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public int getStarNumber() {
        return starNumber;
    }

    public void setStarNumber(int starNumber) {
        this.starNumber = starNumber;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
