package com.visa.ent.mpos.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;

/*
 * Copyright © 2016 CyberSource. All rights reserved.
 */

/**
 * Helps to create dialogs in application.
 * 
 * Created by CyberSource.
 */
public class DialogFactory {

	public static Dialog createInfoDialog(Context context, int title, String message, int positiveLabel,
			int negativeLabel, OnClickListener positiveClickListener, OnClickListener negativeClickListener,
			boolean isCancelable) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		if (title != 0) {
			dialog.setTitle(title);
		}
		if (message != null) {
			dialog.setMessage(message);
		}
		if (positiveClickListener != null) {
			dialog.setPositiveButton(positiveLabel, positiveClickListener);
		}
		if (negativeClickListener != null) {
			dialog.setNegativeButton(negativeLabel, negativeClickListener);
		}
		dialog.setCancelable(isCancelable);
		return dialog.create();
	}

	public static Dialog createListDialog(Context context, int title, String[] list,
			OnClickListener positiveClickListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setItems(list, positiveClickListener);

		return builder.create();
	}

	/**
	 * creates a progress dialog.
	 * 
	 * @param context the context
	 * @return the dialog
	 */
	public static Dialog createProgressDialog(Context context, String message, boolean cancelable) {
		ProgressDialog progressDialog;
		progressDialog = new ProgressDialog(context);
		progressDialog.setCancelable(cancelable);
		progressDialog.setMessage(message);

		return progressDialog;
	}
}