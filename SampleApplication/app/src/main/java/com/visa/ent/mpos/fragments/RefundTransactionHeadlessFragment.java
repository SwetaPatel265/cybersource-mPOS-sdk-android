package com.visa.ent.mpos.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.visa.ent.mpos.BaseActivity;
import com.visa.ent.mpos.receivers.TransactionResultReceiver;
import com.visa.ent.mpos.services.TransactionIntentService;


/*
 * Copyright © 2016 CyberSource. All rights reserved.
 */

public class RefundTransactionHeadlessFragment extends Fragment {

    public static final String ARG_RESULT_RECEIVER = "result_receiver";
    public static final String ARG_TRANSACTIONID = "transactionID";
    public static final String ARG_AMOUNT = "amount";

    private TransactionResultReceiver mReceiver;
    private String transactionID;
    private String amount;

    public RefundTransactionHeadlessFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mReceiver = getArguments().getParcelable(ARG_RESULT_RECEIVER);
            transactionID = getArguments().getString(ARG_TRANSACTIONID);
            amount = getArguments().getString(ARG_AMOUNT);
        }
        setRetainInstance(true);
        startServiceWithReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return null;
    }

    public void startServiceWithReceiver() {
        Intent msgIntent = new Intent(getActivity(), TransactionIntentService.class);
        msgIntent.putExtra(TransactionIntentService.SERVICE_ACTION_TAG, BaseActivity.REFUND_ACTION);
        msgIntent.putExtra(TransactionIntentService.SERVICE_RESULT_RECEIVER, mReceiver);
        msgIntent.putExtra(TransactionIntentService.SERVICE_REFUND_TRANSACTIONID, transactionID);
        msgIntent.putExtra(TransactionIntentService.SERVICE_REFUND_AMOUNT, amount);
        getActivity().startService(msgIntent);
    }

    public void setTransactionId(String transactionID){
        this.transactionID = transactionID;
    }
    public void setAmount(String amount){
        this.amount = amount;
    }
}
