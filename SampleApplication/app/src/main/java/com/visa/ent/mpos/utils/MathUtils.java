package com.visa.ent.mpos.utils;

import android.content.Context;
import android.preference.PreferenceManager;

import java.text.DecimalFormat;

/*
 * Copyright © 2016 CyberSource. All rights reserved.
 */

/**
 * Created by CyberSource on 5/21/2015.
 */
public class MathUtils {

    public static double calculateTaxAmount(double percentage, double subtotal){
        double percentageDouble = percentage/100.00;
        return percentageDouble * subtotal;
    }

    public static double roundToTwoDecimal(double value){
        return (double) Math.round(value * 100) / 100;
    }

    public static String getTwoDecimalInString(double value){
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(value);
    }

}
