package com.visa.ent.mpos.fragments;


import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cybersource.mpos.client.UISettings;
import com.visa.ent.mpos.R;
import com.visa.ent.mpos.preferences.SeekBarPreference;

/*
 * Copyright © 2016 CyberSource. All rights reserved.
 */

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SeekBarPreference seekBarPref;
    private EditTextPreference terminalIdPref;
    private EditTextPreference terminalIdAlternatePref;
    private EditTextPreference midPref;
    private ListPreference environmentPref;
    private EditTextPreference merchantIdPref;
    private EditTextPreference deviceIdPref;
    private EditTextPreference clientIdPref;
    private EditTextPreference clientSecretPref;
    private EditTextPreference usernamePref;
    private EditTextPreference passwordPref;
    private EditTextPreference tokenPref;
    private EditTextPreference spinnerColorPref;
    private EditTextPreference textLabelColorPref;
    private EditTextPreference detailLabelColorPref;
    private EditTextPreference textFieldColorPref;
    private EditTextPreference placeHolderColorPref;
    private EditTextPreference signatureColorPref;
    private EditTextPreference backgroundColorPref;
    private EditTextPreference imageURLPref;
    private ListPreference fontFamilyPref;
    private ListPreference fontStylePref;

    private static final String SEEK_PREF_KEY = "pref_key_tax_percentage";
    private static final String TERMINALID_PREF_KEY = "pref_key_terminalId";
    private static final String TERMINALIDALTERNATE_PREF_KEY = "pref_key_terminalIdAlternate";
    private static final String MID_PREF_KEY = "pref_key_mid";
    private static final String ENVIRONMENT_PREF_KEY = "pref_key_environment";
    private static final String MERCHANTID_PREF_KEY = "pref_key_merchantId";
    private static final String TOKEN_PREF_KEY = "pref_key_token";
    private static final String DEVICEID_PREF_KEY = "pref_key_deviceId";
    private static final String CLIENTID_PREF_KEY = "pref_key_clientId";
    private static final String CLIENTSECRET_PREF_KEY = "pref_key_clientSecret";
    private static final String USERNAME_PREF_KEY = "pref_key_username";
    private static final String PASSWORD_PREF_KEY = "pref_key_password";
    private static final String SPINNERCOLOR_PREF_KEY = "pref_key_spinner_color";
    private static final String TEXTLABELCOLOR_PREF_KEY = "pref_key_textLabel_color";
    private static final String DETAILLABELCOLOR_PREF_KEY = "pref_key_detailLabel_color";
    private static final String TEXTFIELDCOLOR_PREF_KEY = "pref_key_textField_color";
    private static final String PLACEHOLDERCOLOR_PREF_KEY = "pref_key_placeHolder_color";
    private static final String SIGNATURECOLOR_PREF_KEY = "pref_key_signature_color";
    private static final String BACKGROUNDCOLOR_PREF_KEY = "pref_key_background_color";
    private static final String IMAGEURL_PREF_KEY = "pref_key_image_url";
    private static final String FONTFAMILY_PREF_KEY = "pref_key_font_family";
    private static final String FONTSTYLE_PREF_KEY = "pref_key_font_style";

    public SettingsFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make sure default values are applied
        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        // Get widgets :
        seekBarPref = (SeekBarPreference) this.findPreference(SEEK_PREF_KEY);
        terminalIdPref = (EditTextPreference)this.findPreference(TERMINALID_PREF_KEY);
        terminalIdAlternatePref = (EditTextPreference)this.findPreference(TERMINALIDALTERNATE_PREF_KEY);
        midPref = (EditTextPreference)this.findPreference(MID_PREF_KEY);
        environmentPref = (ListPreference)this.findPreference(ENVIRONMENT_PREF_KEY);
        merchantIdPref = (EditTextPreference)this.findPreference(MERCHANTID_PREF_KEY);
        tokenPref = (EditTextPreference)this.findPreference(TOKEN_PREF_KEY);
        deviceIdPref = (EditTextPreference)this.findPreference(DEVICEID_PREF_KEY);
        clientIdPref = (EditTextPreference)this.findPreference(CLIENTID_PREF_KEY);
        clientSecretPref = (EditTextPreference)this.findPreference(CLIENTSECRET_PREF_KEY);
        usernamePref = (EditTextPreference)this.findPreference(USERNAME_PREF_KEY);
        passwordPref = (EditTextPreference)this.findPreference(PASSWORD_PREF_KEY);
        spinnerColorPref = (EditTextPreference)this.findPreference(SPINNERCOLOR_PREF_KEY);
        textLabelColorPref = (EditTextPreference)this.findPreference(TEXTLABELCOLOR_PREF_KEY);
        detailLabelColorPref = (EditTextPreference)this.findPreference(DETAILLABELCOLOR_PREF_KEY);
        textFieldColorPref = (EditTextPreference)this.findPreference(TEXTFIELDCOLOR_PREF_KEY);
        placeHolderColorPref = (EditTextPreference)this.findPreference(PLACEHOLDERCOLOR_PREF_KEY);
        signatureColorPref = (EditTextPreference)this.findPreference(SIGNATURECOLOR_PREF_KEY);
        backgroundColorPref = (EditTextPreference)this.findPreference(BACKGROUNDCOLOR_PREF_KEY);
        imageURLPref = (EditTextPreference)this.findPreference(IMAGEURL_PREF_KEY);
        fontFamilyPref = (ListPreference)this.findPreference(FONTFAMILY_PREF_KEY);
        fontStylePref = (ListPreference)this.findPreference(FONTSTYLE_PREF_KEY);

        // Set listener :
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        // Set seekbar summary :
        setSeekBarSummary();
        setTerminalSummary();
        setEnvironmentSummary();
        setUISettingsSummary();
        setUISettings();

    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        setSeekBarSummary();
        setTerminalSummary();
        setEnvironmentSummary();
        setUISettingsSummary();
        setUISettings();
    }

    private void setSeekBarSummary() {
        float percentage = PreferenceManager.getDefaultSharedPreferences
                (this.getActivity()).getFloat(SEEK_PREF_KEY, (float)9.8);
        seekBarPref.setSummary("" + percentage + this.getString(R.string.pref_tax_percentage_summary));
    }

    private void setTerminalSummary(){
        String terminalID = PreferenceManager.getDefaultSharedPreferences
                (this.getActivity()).getString(TERMINALID_PREF_KEY, "47");
        terminalIdPref.setSummary(terminalID);

        String terminalIDAlternate = PreferenceManager.getDefaultSharedPreferences
                (this.getActivity()).getString(TERMINALIDALTERNATE_PREF_KEY, "57");
        terminalIdAlternatePref.setSummary(terminalIDAlternate);

        String mid = PreferenceManager.getDefaultSharedPreferences
                (this.getActivity()).getString(MID_PREF_KEY, "");
        midPref.setSummary(mid);
    }
    private void setEnvironmentSummary() {
        String environment = PreferenceManager.getDefaultSharedPreferences
                (this.getActivity()).getString(ENVIRONMENT_PREF_KEY,"Select Environment");
        environmentPref.setSummary(environment);
        String merchantID = PreferenceManager.getDefaultSharedPreferences
                (this.getActivity()).getString(MERCHANTID_PREF_KEY,"");
        merchantIdPref.setSummary(merchantID);
        String token = PreferenceManager.getDefaultSharedPreferences
                (this.getActivity()).getString(TOKEN_PREF_KEY,"");
        tokenPref.setSummary(token);
        String deviceID = PreferenceManager.getDefaultSharedPreferences
                (this.getActivity()).getString(DEVICEID_PREF_KEY, "");
        deviceIdPref.setSummary(deviceID);
        String clientID = PreferenceManager.getDefaultSharedPreferences
                (this.getActivity()).getString(CLIENTID_PREF_KEY, "");
        clientIdPref.setSummary(clientID);
        String clientSecret = PreferenceManager.getDefaultSharedPreferences
                (this.getActivity()).getString(CLIENTSECRET_PREF_KEY, "");
        clientSecretPref.setSummary(clientSecret);
        String username = PreferenceManager.getDefaultSharedPreferences
                (this.getActivity()).getString(USERNAME_PREF_KEY, "");
        usernamePref.setSummary(username);
        String password = PreferenceManager.getDefaultSharedPreferences
                (this.getActivity()).getString(PASSWORD_PREF_KEY, "");
        passwordPref.setSummary(password);
    }

    private void setUISettingsSummary(){
        String spinnerColor = PreferenceManager.getDefaultSharedPreferences
                (this.getActivity()).getString(SPINNERCOLOR_PREF_KEY,"Spinner Color");
        spinnerColorPref.setSummary(spinnerColor);
        String textLabelColor = PreferenceManager.getDefaultSharedPreferences
                (this.getActivity()).getString(TEXTLABELCOLOR_PREF_KEY,"TextLabel Color");
        textLabelColorPref.setSummary(textLabelColor);
        String detailLabelColor = PreferenceManager.getDefaultSharedPreferences
                (this.getActivity()).getString(DETAILLABELCOLOR_PREF_KEY,"DetailLabel Color");
        detailLabelColorPref.setSummary(detailLabelColor);
        String textFieldColor = PreferenceManager.getDefaultSharedPreferences
                (this.getActivity()).getString(TEXTFIELDCOLOR_PREF_KEY,"TextField Color");
        textFieldColorPref.setSummary(textFieldColor);
        String placeHolderColor = PreferenceManager.getDefaultSharedPreferences
                (this.getActivity()).getString(PLACEHOLDERCOLOR_PREF_KEY,"PlaceHolder Color");
        placeHolderColorPref.setSummary(placeHolderColor);
        String signatureColor = PreferenceManager.getDefaultSharedPreferences
                (this.getActivity()).getString(SIGNATURECOLOR_PREF_KEY,"Signature Color");
        signatureColorPref.setSummary(signatureColor);
        String backgroundColor = PreferenceManager.getDefaultSharedPreferences
                (this.getActivity()).getString(BACKGROUNDCOLOR_PREF_KEY,"Background Color");
        backgroundColorPref.setSummary(backgroundColor);
        String imageURL = PreferenceManager.getDefaultSharedPreferences
                (this.getActivity()).getString(IMAGEURL_PREF_KEY,"Image URL");
        imageURLPref.setSummary(imageURL);
        String fontFamily = PreferenceManager.getDefaultSharedPreferences
                (this.getActivity()).getString(FONTFAMILY_PREF_KEY,"Font Family");
        fontFamilyPref.setSummary(fontFamily);
        String fontStyle = PreferenceManager.getDefaultSharedPreferences
                (this.getActivity()).getString(FONTSTYLE_PREF_KEY,"Font Style");
        fontStylePref.setSummary(fontStyle);
    }

    private void setUISettings(){
        UISettings uiSettings = UISettings.getInstance();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        String spinnerColor = sharedPreferences.getString(SPINNERCOLOR_PREF_KEY, "");
        if(!spinnerColor.startsWith("#")){
            spinnerColor = "#"+spinnerColor;
        }
        String textLabelColor = sharedPreferences.getString(TEXTLABELCOLOR_PREF_KEY, "");
        if(!textLabelColor.startsWith("#")){
            textLabelColor = "#"+textLabelColor;
        }
        String detailLabelColor = sharedPreferences.getString(DETAILLABELCOLOR_PREF_KEY, "");
        if(!detailLabelColor.startsWith("#")){
            detailLabelColor = "#"+detailLabelColor;
        }
        String textFieldColor = sharedPreferences.getString(TEXTFIELDCOLOR_PREF_KEY, "");
        if(!textFieldColor.startsWith("#")){
            textFieldColor = "#"+textFieldColor;
        }
        String placeHolderColor = sharedPreferences.getString(PLACEHOLDERCOLOR_PREF_KEY, "");
        if(!placeHolderColor.startsWith("#")){
            placeHolderColor = "#"+placeHolderColor;
        }
        String signatureColor = sharedPreferences.getString(SIGNATURECOLOR_PREF_KEY, "");
        if(!signatureColor.startsWith("#")){
            signatureColor = "#"+signatureColor;
        }
        String backgroundColor = sharedPreferences.getString(BACKGROUNDCOLOR_PREF_KEY, "");
        if(!backgroundColor.startsWith("#")){
            backgroundColor = "#"+backgroundColor;
        }
        String imageURL = sharedPreferences.getString(IMAGEURL_PREF_KEY,"");
        String fontFamily = sharedPreferences.getString(FONTFAMILY_PREF_KEY,"");
        String fontStyle = sharedPreferences.getString(FONTSTYLE_PREF_KEY,"");
        uiSettings.setBackgroundColor(backgroundColor);
        uiSettings.setSpinnerColor(spinnerColor);
        uiSettings.setTextLabelColor(textLabelColor);
        uiSettings.setDetailLabelColor(detailLabelColor);
        uiSettings.setTextFieldColor(textFieldColor);
        uiSettings.setPlaceHolderColor(placeHolderColor);
        uiSettings.setSignatureColor(signatureColor);
        Typeface fontFamilyValue = getFontFamilyTypeface(fontFamily);
        uiSettings.setFontFamily(fontFamilyValue);
        int fontStyleValue = getFontStyleTypeface(fontStyle);
        uiSettings.setFontStyle(fontStyleValue);
        //Need to clear the previous image from memory before downloading new.
        //If the image is present in memory, it will use the same instead of downloading from new URL.
        uiSettings.setTitleImage(null);
        //Option 1 URL: To download company logo from URL.
        uiSettings.setTitleImageURL(imageURL);
        //For image resource option.
        //Option 2 Existing resource: You can also set image from drawable instead of URL.
        //UISettings.getInstance().setTitleImageResource(R.drawable.wf_logo);
    }
    private Typeface getFontFamilyTypeface(String fontFamily){
        if(fontFamily != null && fontFamily.length()>0){
            switch(fontFamily){
                case "SERIF":
                    return Typeface.SERIF;
                case "SANS_SERIF":
                    return Typeface.SANS_SERIF;
                case "MONOSPACE":
                    return Typeface.MONOSPACE;
                default:
                    return Typeface.SERIF;
            }
        }
        return Typeface.SERIF;
    }

    private int getFontStyleTypeface(String fontStyle){
        if(fontStyle != null && fontStyle.length()>0){
            switch(fontStyle){
                case "BOLD":
                    return Typeface.BOLD;
                case "BOLD_ITALIC":
                    return Typeface.BOLD_ITALIC;
                case "ITALIC":
                    return Typeface.ITALIC;
                case "NORMAL":
                    return Typeface.NORMAL;
                default:
                    return Typeface.NORMAL;
            }
        }
        return Typeface.NORMAL;
    }
}
