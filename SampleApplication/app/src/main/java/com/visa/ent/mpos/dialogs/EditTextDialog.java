package com.visa.ent.mpos.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.visa.ent.mpos.R;
import com.visa.ent.mpos.fragments.ShoppingCartFragment;

/*
 * Copyright © 2016 CyberSource. All rights reserved.
 */

/**
 * Created by CyberSource on 5/19/2015.
 */
public class EditTextDialog extends DialogFragment /*implements TextView.OnEditorActionListener */{

    public static final String TIP_AMOUNT = "Tip Amount";
    public static final String ADD = "Add";
    public static final String CANCEL = "Cancel";
    private EditText tipEditText;
    private LinearLayout linearLayout;

    public EditTextDialog() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Show soft keyboard automatically
        if(tipEditText != null) {
            tipEditText.requestFocus();
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        setupTipLayout();
        return new AlertDialog.Builder(getActivity())
                .setTitle(TIP_AMOUNT)
                .setView(linearLayout)
                .setPositiveButton(ADD, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.putExtra(ShoppingCartFragment.EDIT_TEXT_CALL_PARAM,
                                tipEditText.getText().toString());
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                    }
                })
                .setNegativeButton(CANCEL, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, null);
                    }
                })
                .create();
    }

    private void setupTipLayout() {
        linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        tipEditText = new EditText(getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(32, 24, 32, 0);
        tipEditText.setHint("$0.00");
        tipEditText.setTextSize(24);
        tipEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        linearLayout.addView(tipEditText, params);
    }
}
