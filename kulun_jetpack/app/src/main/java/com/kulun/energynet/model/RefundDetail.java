package com.kulun.energynet.model;

import java.io.Serializable;
import java.util.List;

public class RefundDetail implements Serializable {
    //			"id": 6251,
    //			"status": 0,
    //			"amount": "1.00",
    //			"process": [
    //				["退款申请", "2021年02月19日 17:16:27", ""],
    //				["审核中", "2021年02月26日 13:55:57", ""]
    //			],
    //			"detail": [{
    //				"name": "账单类型",
    //				"value": "退款"
    //			}, {
    //				"name": "退款进度",
    //				"value": ""
    //			}]
    private int id, status;
    private String amount;
    private List<List<String>> process;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }


    public List<List<String>> getProcess() {
        return process;
    }

    public void setProcess(List<List<String>> process) {
        this.process = process;
    }
}
