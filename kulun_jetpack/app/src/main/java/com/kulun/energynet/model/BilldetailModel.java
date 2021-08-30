package com.kulun.energynet.model;

import java.util.List;

public class BilldetailModel {
 //2021 - 02 - 26 13: 55: 57.251 15872 - 15916 / com.kulun.energynet D / xuedi: response: 返回: {
 //	"code": 0,
 //	"data": {
 //		"exId": 2791553,
 //		"site": "会岸路站",
 //		"siteId": 4,
 //		"orderNo": "202101260002791553",
 //		"questioned": true,
 //		"commented": false,
 //		"detail": [{
 //		}],
 //		"refundDetail": {
 //		}
 //	}
 //}
    private List<BillDetail> detail;
    private boolean questioned, commented;
    private int siteId, exId, status;
    private String site,orderNo;
    private double amount;
    private RefundDetail refundDetail;

    public RefundDetail getRefundDetail() {
        return refundDetail;
    }

    public void setRefundDetail(RefundDetail refundDetail) {
        this.refundDetail = refundDetail;
    }

    public List<BillDetail> getDetail() {
        return detail;
    }

    public void setDetail(List<BillDetail> detail) {
        this.detail = detail;
    }

    public boolean isQuestioned() {
        return questioned;
    }

    public void setQuestioned(boolean questioned) {
        this.questioned = questioned;
    }

    public boolean isCommented() {
        return commented;
    }

    public void setCommented(boolean commented) {
        this.commented = commented;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public int getExId() {
        return exId;
    }

    public void setExId(int exId) {
        this.exId = exId;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
