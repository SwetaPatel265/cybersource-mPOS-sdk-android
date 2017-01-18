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

public class PayTransactionHeadlessFrangment extends Fragment {

    public static final String ARG_RESULT_RECEIVER = "result_receiver";

    private TransactionResultReceiver mReceiver;

    public PayTransactionHeadlessFrangment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mReceiver = getArguments().getParcelable(ARG_RESULT_RECEIVER);
        }
        setRetainInstance(true);
        startServiceWithReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return null;
    }

    private void startServiceWithReceiver() {
        Intent msgIntent = new Intent(getActivity(), TransactionIntentService.class);
        msgIntent.putExtra(TransactionIntentService.SERVICE_ACTION_TAG, BaseActivity.PAYMENT_ACTION);
        msgIntent.putExtra(TransactionIntentService.SERVICE_RESULT_RECEIVER, mReceiver);
        getActivity().startService(msgIntent);
    }
}
