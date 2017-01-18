package com.visa.ent.mpos.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Base64;
import android.util.Patterns;

import com.visainc.mpos.sdk.datamodel.VMposAuthentication;
import com.visainc.mpos.sdk.datamodel.VMposUserSession;
import com.visainc.mpos.sdk.datamodel.transaction.VMposTransactionObject;
import com.visainc.mpos.sdk.datamodel.transaction.fields.VMposAddress;
import com.visainc.mpos.sdk.devices.VMposReader;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/*
 * Copyright © 2016 CyberSource. All rights reserved.
 */

/**
 * Created by CyberSource on 5/26/2015.
 */
public class Utils {

    public static long stringDateToMillis(String stringDate){
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
        return date.getTime();
    }
}
