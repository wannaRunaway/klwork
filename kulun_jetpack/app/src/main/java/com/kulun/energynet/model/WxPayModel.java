package com.kulun.energynet.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * created by xuedi on 2019/8/26
 */
public class WxPayModel implements Serializable {

    /**
     {"code":0,"data":{"appid":"wx1238d44896f1a785","noncestr":"1692036619361148522","package":"Sign=WXPay","partnerid":"1602454373","prepayid":"wx01150136736279f386162e97df538f0000",
     "sign":"72B8DD0CF6351467344D3510EB420132","timestamp":"1614582096"}}
     */
    /**
     * package : Sign=WXPay
     * appid : wxfeda8ed1ffad13af
     * sign : 35D38A0F68ECC49B0A62443137AADDB4
     * partnerid : 1552253491
     * prepayid : wx260946363605301a211dcfb11798612400
     * noncestr : bmEWkk7rHbPL
     * timestamp : 1566783996
     */

    @SerializedName("package")
    private String packageX;
    private String appid;
    private String sign;
    private String partnerid;
    private String prepayid;
    private String noncestr;
    private String timestamp;

    public String getPackageX() {
        return packageX;
    }

    public void setPackageX(String packageX) {
        this.packageX = packageX;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public void setPrepayid(String prepayid) {
        this.prepayid = prepayid;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
