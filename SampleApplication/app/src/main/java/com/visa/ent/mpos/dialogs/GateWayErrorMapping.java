package com.visa.ent.mpos.dialogs;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.visa.ent.mpos.BaseNavigationActivity;


/*
 * Copyright © 2016 CyberSource. All rights reserved.
 */

/**
 * Maps and SDK error to a proper UI message.
 * 
 * Created by CyberSource.
 */
public class GateWayErrorMapping {

	public static AlertDialog showDialogByErrorCode(BaseNavigationActivity activity) {
        return getAlertDialog(activity, "I am a title", "I am trying to pretend that I am a message");
	}

    private static AlertDialog getAlertDialog(BaseNavigationActivity activity, String title, String message){
        return new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}