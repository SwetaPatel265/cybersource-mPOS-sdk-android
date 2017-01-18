package com.visa.ent.mpos.utils;

import com.cybersource.mpos.client.PaymentRequest;
import com.cybersource.mpos.client.PaymentResponse;

/*
 * Copyright © 2016 CyberSource. All rights reserved.
 */

/**
 * Created by CyberSource on 9/6/2016.
 */
public class PaymentUtil {

    private static PaymentUtil paymentUtil;
    private PaymentRequest paymentRequest;
    private PaymentResponse paymentResponse;
    private String oAuthToken;
    private PaymentRequest.PaymentRequestEntryMode entryMode;

    public PaymentRequest.PaymentRequestEntryMode getEntryMode() {
        return entryMode;
    }

    public void setEntryMode(PaymentRequest.PaymentRequestEntryMode entryMode) {
        this.entryMode = entryMode;
    }

    public String getoAuthToken() {
        return oAuthToken;
    }

    public void setoAuthToken(String oAuthToken) {
        this.oAuthToken = oAuthToken;
    }

    public static PaymentUtil getInstance(){
        if(paymentUtil == null){
            paymentUtil = new PaymentUtil();
        }
        return paymentUtil;
    }
    private PaymentUtil(){}

    public PaymentRequest getPaymentRequest() {
        return paymentRequest;
    }

    public void setPaymentRequest(PaymentRequest paymentRequest) {
        this.paymentRequest = paymentRequest;
    }

    public PaymentResponse getPaymentResponse() {
        return paymentResponse;
    }

    public void setPaymentResponse(PaymentResponse paymentResponse) {
        this.paymentResponse = paymentResponse;
    }
}
