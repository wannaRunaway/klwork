package com.kulun.energynet.requestbody;

public class QuestionUploadRequest {
    //     String json = JsonSplice.leftparent+JsonSplice.yin+"exId"+JsonSplice.yinandmao+exId+JsonSplice.dou+
    //                JsonSplice.yin+"siteId"+JsonSplice.yinandmao+siteid+JsonSplice.dou+
    //                JsonSplice.yin+"picture"+JsonSplice.yinandmao+JsonSplice.yin+picture+JsonSplice.yinanddou+
    //                JsonSplice.yin+"content"+JsonSplice.yinandmao+JsonSplice.yin+mycontent+JsonSplice.yin+JsonSplice.rightparent;
    private int exId, siteId;
    private String picture, content;

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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
