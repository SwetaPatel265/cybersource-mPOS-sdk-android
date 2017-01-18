package com.visa.ent.mpos.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/*
 * Copyright © 2016 CyberSource. All rights reserved.
 */

/**
 * Created by CyberSource on 4/21/2015.
 */
public class YesNoDialog extends DialogFragment {

    public static final String ARG_TITLE = "title";
    public static final String ARG_MESSAGE = "message";
    public static final String ARG_POSITIVE = "positive";
    public static final String ARG_NEGATIVE = "negative";
    public static final String ARG_FROM_FRAGMENT = "from_fragment";

    private OnYesNODialogInteractionListener mActivityListener;

    public YesNoDialog(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Bundle args = getArguments();
        String title = args.getString(ARG_TITLE, "");
        String message = args.getString(ARG_MESSAGE, "");
        String positiveButton = args.getString(ARG_POSITIVE, "Yes");
        String negativeButton = args.getString(ARG_NEGATIVE, "No");
        boolean isFromFragment = args.getBoolean(ARG_FROM_FRAGMENT, true);

        if(isFromFragment) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
                        }
                    })
                    .setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, null);
                        }
                    })
                    .create();
        }

        else {
            return new AlertDialog.Builder(getActivity())
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mActivityListener.onDialogButtonClicked(getTargetRequestCode(),
                                    Activity.RESULT_OK, null);
                        }
                    })
                    .setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mActivityListener.onDialogButtonClicked(getTargetRequestCode(),
                                    Activity.RESULT_CANCELED, null);
                        }
                    })
                    .create();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mActivityListener = (OnYesNODialogInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnYesNODialogInteractionListener");
        }
    }
    public interface OnYesNODialogInteractionListener {
        public void onDialogButtonClicked(int requestCode, int resultCode, Intent data);
    }

}
