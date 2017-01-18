package com.visa.ent.mpos.datamodel;

/*
 * Copyright © 2016 CyberSource. All rights reserved.
 */

/**
 * Created by CyberSource on 4/24/2015.
 */
public class HistoryItem {
    private String transactionId;
    private String price;
    private String date;
    private String time;
    private boolean refunded;
    private String status;

    public HistoryItem(String transactionId, String price, String date, String time, boolean refunded, String status){
        this.transactionId = transactionId;
        this.price = price;
        this.date = date;
        this.time = time;
        this.refunded = refunded;
        this.status = status;
    }
    public HistoryItem(String transactionId, String price, String date, String time, boolean refunded){
        this.transactionId = transactionId;
        this.price = price;
        this.date = date;
        this.time = time;
        this.refunded = refunded;
        this.status = "";
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isRefunded() {
        return refunded;
    }

    public void setRefunded(boolean refunded) {
        this.refunded = refunded;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    @Override
    public boolean equals(Object obj) {
        HistoryItem item = (HistoryItem) obj;
        if(item.getTransactionId().compareTo(this.getTransactionId()) == 0)
            return true;

        return false;
    }
}
