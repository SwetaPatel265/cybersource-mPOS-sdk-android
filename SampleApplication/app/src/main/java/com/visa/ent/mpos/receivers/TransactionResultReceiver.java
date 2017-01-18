package com.visa.ent.mpos.receivers;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/*
 * Copyright © 2016 CyberSource. All rights reserved.
 */

/**
 * Created by CyberSource on 7/10/2015.
 */

public class TransactionResultReceiver extends ResultReceiver {

    private Receiver mReceiver;

    public TransactionResultReceiver(Handler handler) {
        super(handler);
        // TODO Auto-generated constructor stub
    }

    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }
}