package com.kulun.energynet.model;

import java.io.Serializable;

public class QuestionshowModel implements Serializable {
    //   if (json.has("content")){
    //                if (json.has("handleContent")){
    //                    binding.fankui.setText("反馈:"+json.get("handleContent").getAsString());
    //                }
    //                if (json.has("status")){
    //                }
    private String content, handleContent;
    private int status;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHandleContent() {
        return handleContent;
    }

    public void setHandleContent(String handleContent) {
        this.handleContent = handleContent;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
