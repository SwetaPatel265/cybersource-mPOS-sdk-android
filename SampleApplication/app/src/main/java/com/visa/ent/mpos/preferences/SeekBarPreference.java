package com.visa.ent.mpos.preferences;

/**
 * Created by CyberSource on 5/15/2015.
 */
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/*
 * Copyright © 2016 CyberSource. All rights reserved.
 */

public class SeekBarPreference extends DialogPreference implements OnSeekBarChangeListener, View.OnClickListener
{
    private static final String androidns="http://schemas.android.com/apk/res/android";
    public static final String DIALOG_MESSAGE = "dialogMessage";
    public static final String DEFAULT_VALUE_DECIMAL = "defaultValueDecimal";
    public static final String TEXT = "text";
    public static final String DEFAULT_VALUE = "defaultValue";
    public static final String MAX_DECIMAL = "maxDecimal";
    public static final String MAX = "max";
    private static String TAG_MAIN_SEEKBAR = "mianSeekBar";
    private static String TAG_DECIMAL_SEEKBAR = "decimalSeekBar";

    private SeekBar mSeekBar;
    private TextView mSplashText,mValueText;
    private Context mContext;

    private String mDialogMessage, mSuffix;
    private int mDefaultDecimal, mMaxDecimal, mValueDecimal = 0;

    private SeekBar mSeekBarDecimal;
    private int mDefault, mMax, mValue = 0;
    private static final float DEFAULT_PERCENTAGE = (float)9.8;

    private float finalProgress;

    public SeekBarPreference(Context context, AttributeSet attrs) {

        super(context,attrs);
        mContext = context;

        // Get string value for dialogMessage :
        int mDialogMessageId = attrs.getAttributeResourceValue(androidns, DIALOG_MESSAGE, 0);
        if(mDialogMessageId == 0) mDialogMessage = attrs.getAttributeValue(androidns, DIALOG_MESSAGE);
        else mDialogMessage = mContext.getString(mDialogMessageId);

        // Get string value for suffix (text attribute in xml file) :
        int mSuffixId = attrs.getAttributeResourceValue(androidns, TEXT, 0);
        if(mSuffixId == 0) mSuffix = attrs.getAttributeValue(androidns, TEXT);
        else mSuffix = mContext.getString(mSuffixId);

        // Get default and max seekbar values :
        mDefault = attrs.getAttributeIntValue(androidns, DEFAULT_VALUE, 0);
        mMax = attrs.getAttributeIntValue(androidns, MAX, 19);

        // Get default and max decimal seekbar values :
        mDefaultDecimal = attrs.getAttributeIntValue(androidns, DEFAULT_VALUE_DECIMAL, 8);
        mMaxDecimal = attrs.getAttributeIntValue(androidns, MAX_DECIMAL, 9);
    }

    // DialogPreference methods :
    @Override
    protected View onCreateDialogView() {

        LinearLayout.LayoutParams params;
        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(6,6,6,6);

        mSplashText = new TextView(mContext);
        mSplashText.setPadding(30, 10, 30, 10);
        if (mDialogMessage != null)
            mSplashText.setText(mDialogMessage);
        //layout.addView(mSplashText);

        mValueText = new TextView(mContext);
        mValueText.setGravity(Gravity.CENTER_HORIZONTAL);
        mValueText.setTextSize(40);
        params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 25, 0, 10);
        layout.addView(mValueText, params);
        setupMainSeekBar(layout);
        setupDecimalSeekBar(layout);

        return layout;
    }

    private void setupMainSeekBar(LinearLayout layout) {
        LinearLayout.LayoutParams params;
        mSeekBar = new SeekBar(mContext);
        mSeekBar.setTag(TAG_MAIN_SEEKBAR);
        mSeekBar.setOnSeekBarChangeListener(this);
        params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 50);
        layout.addView(mSeekBar, params);

        if (shouldPersist())
            floatToIntSeekBarValues();

        mSeekBar.setMax(mMax);
        mSeekBar.setProgress(mValue);
    }

    private void setupDecimalSeekBar(LinearLayout layout) {
        LinearLayout.LayoutParams params;
        mSeekBarDecimal = new SeekBar(mContext);
        mSeekBarDecimal.setTag(TAG_DECIMAL_SEEKBAR);
        mSeekBarDecimal.setOnSeekBarChangeListener(this);
        params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.addView(mSeekBarDecimal, params);

        mSeekBarDecimal.setMax(mMaxDecimal);
        mSeekBarDecimal.setProgress(mValueDecimal);
    }

    private void floatToIntSeekBarValues() {
        finalProgress = getPersistedFloat(DEFAULT_PERCENTAGE);
        String finalProgressString = String.valueOf(finalProgress);
        String[] components = finalProgressString.split("\\.");
        mValue = Integer.parseInt(components[0]);
        mValueDecimal = Integer.parseInt(components[1]);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        mSeekBar.setMax(mMax);
        mSeekBar.setProgress(mValue);

        mSeekBarDecimal.setMax(mMaxDecimal);
        mSeekBarDecimal.setProgress(mValueDecimal);
    }

    @Override
    protected void onSetInitialValue(boolean restore, Object defaultValue)
    {
        super.onSetInitialValue(restore, defaultValue);

        if(restore) {
            if(shouldPersist())
                floatToIntSeekBarValues();
            else{
                mValue = 9;
                mValueDecimal = 8;
            }
        }
        else {
            mValue = 9;
            mValueDecimal = 8;
        }
    }

    // OnSeekBarChangeListener methods :
    @Override
    public void onProgressChanged(SeekBar seek, int value, boolean fromTouch)
    {
        String seekBarTag = null;
        if(seek != null){
            seekBarTag = (String) seek.getTag();
        }

        int mainProgress = 0;
        float decimalProgress = (float)0.0;
        if(seekBarTag != null) {
            if (seekBarTag.equals(TAG_MAIN_SEEKBAR)) {
                mainProgress = value;
                if(mSeekBarDecimal != null)
                    decimalProgress = getFloatForProgress(mSeekBarDecimal.getProgress());
            } else if (seekBarTag.equals(TAG_DECIMAL_SEEKBAR)) {
                decimalProgress = getFloatForProgress(value);
                if(mSeekBar != null)
                    mainProgress = mSeekBar.getProgress();
            }
        }

        finalProgress = mainProgress + decimalProgress;
        String t = String.valueOf(finalProgress);
        mValueText.setText(mSuffix == null ? t : t.concat(mSuffix));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seek) {}
    @Override
    public void onStopTrackingTouch(SeekBar seek) {}

    public void setMax(int max) { mMax = max; }
    public int getMax() { return mMax; }

    public void setProgress(int progress) {
        mValue = progress;
        if (mSeekBar != null)
            mSeekBar.setProgress(progress);
    }
    public int getProgress() { return mValue; }

    // Set the positive button listener and onClick action :
    @Override
    public void showDialog(Bundle state) {

        super.showDialog(state);

        Button positiveButton = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (shouldPersist()) {
            mValue = mSeekBar.getProgress();
            mValueDecimal = mSeekBarDecimal.getProgress();
            persistFloat(finalProgress);
            //persistInt(mSeekBar.getProgress());
            callChangeListener(Float.valueOf(finalProgress));
            //callChangeListener(Integer.valueOf(mSeekBar.getProgress()));
        }

        ((AlertDialog) getDialog()).dismiss();
    }

    private float getFloatForProgress(int value){
        return value / (float)10.0;
    }
}
