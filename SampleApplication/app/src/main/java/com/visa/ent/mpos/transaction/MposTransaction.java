package com.visa.ent.mpos.transaction;

import android.content.Context;
import android.preference.PreferenceManager;

import com.visa.ent.mpos.datamodel.CheckoutCartItem;
import com.visa.ent.mpos.datamodel.HistoryItem;
import com.visa.ent.mpos.utils.MathUtils;
import com.visainc.mpos.sdk.common.VMposCore;
import com.visainc.mpos.sdk.common.VMposCurrency;
import com.visainc.mpos.sdk.common.VMposSettings;

import com.visainc.mpos.sdk.datamodel.transaction.VMposTransactionObject;
import com.visainc.mpos.sdk.datamodel.transaction.fields.VMposCommerceIndicator;
import com.visainc.mpos.sdk.datamodel.transaction.fields.VMposItem;
import com.visainc.mpos.sdk.datamodel.transaction.fields.VMposPurchaseDetails;

import java.math.BigDecimal;
import java.util.ArrayList;

/*
 * Copyright © 2016 CyberSource. All rights reserved.
 */

/**
 * Created by CyberSource on 5/19/2015.
 */
public class MposTransaction {

    public static final String KEY_PREF_SEEK = "pref_key_tax_percentage";

    private static ArrayList<CheckoutCartItem> cartItemsList;
    private static ArrayList<HistoryItem> historyTransactionList;

    private static ArrayList<VMposTransactionObject> transactionObjects;
    private static VMposTransactionObject transactionObject;

    private static boolean isTipEnabled;
    private static boolean isTaxEnabled;

    private static double subtotalAmount = 0.00;
    private static double tipAmount = 0.00;

    private static Context mContext;
    private static MposTransaction ourInstance = new MposTransaction();

    public static MposTransaction getInstance() {
        return ourInstance;
    }

    private MposTransaction() {
        historyTransactionList = new ArrayList<>();
        cartItemsList = new ArrayList<>();
        transactionObjects = new ArrayList<>();
        VMposSettings.getInstance().setCurrency(VMposCurrency.USD);
        createNewTransactionObject();
    }

    public ArrayList<CheckoutCartItem> getCartItemsList() {
        return cartItemsList;
    }

    public double getSubtotalAmount() {
        return MathUtils.roundToTwoDecimal(subtotalAmount);
    }

    public void setSubtotalAmount(double subtotalAmount) {
        MposTransaction.subtotalAmount = MathUtils.roundToTwoDecimal(subtotalAmount);
    }

    public double getTipAmount() {
        return tipAmount;
    }

    public void setTipAmount(double tipAmount) {
        MposTransaction.tipAmount = tipAmount;
    }

    public void setIsTaxEnabled(boolean isTaxEnabled) {
        MposTransaction.isTaxEnabled = isTaxEnabled;
    }

    public boolean isIsTipEnabled() {
        return isTipEnabled;
    }

    public void setIsTipEnabled(boolean isTipEnabled) {
        MposTransaction.isTipEnabled = isTipEnabled;
    }

    public ArrayList<VMposTransactionObject> getTransactionObjects() {
        return transactionObjects;
    }

    public VMposTransactionObject getTransactionObject() {
        return transactionObject;
    }

    public void destroyTransactionObject() {
        createNewTransactionObject();
    }

    public void destroyCartItems() {
        cartItemsList = new ArrayList<>();
    }

    public static void createNewTransactionObject(){
        transactionObject = VMposTransactionObject.createTransactionObject();
        transactionObject.setPurchaseDetails(new VMposPurchaseDetails());
        transactionObject.getPurchaseDetails()
                .setCommerceIndicator(VMposCommerceIndicator.VMPOS_COMMERCE_INDICATOR_RETAIL);
        transactionObject.setTransactionCode("Android_Demo_App_"
                + VMposCore.getDeviceId());
    }

    public void setApplicationContext(Context context){
        this.mContext = context;
    }

    public void addItemToTransactionObject(CheckoutCartItem cartItem) {
        double individualTaxAmount = 0.00;
        if(isTaxEnabled)
            individualTaxAmount = getTaxAmountForItem(cartItem);
        //this.taxAmount += individualTaxAmount;
        VMposItem vMposItem = mapCartItemToTransactionItem(cartItem);
        vMposItem.setIndividualTaxAmount(BigDecimal.valueOf(individualTaxAmount));
        transactionObject.addItem(vMposItem);
    }

    public VMposItem mapCartItemToTransactionItem(CheckoutCartItem cartItem) {
        return new VMposItem(cartItem.getName(), BigDecimal.valueOf(cartItem.getPrice()));
    }

    private double getTaxAmountForItem(CheckoutCartItem cartItem) {
        double percentage = PreferenceManager.getDefaultSharedPreferences
                (mContext).getFloat(KEY_PREF_SEEK, (float)9.80);
        return MathUtils.calculateTaxAmount(percentage, cartItem.getPrice());
    }
    private double getTaxAmountForItem(VMposItem vMposItem) {
        double percentage = PreferenceManager.getDefaultSharedPreferences
                (mContext).getFloat(KEY_PREF_SEEK, (float)9.80);
        return MathUtils.calculateTaxAmount(percentage, vMposItem.getPrice().doubleValue());
    }


    public void removeItemFromTransactionObject(int position){
        transactionObject.removeItem(position);
    }

    public void updateIndividualItemsTax() {
        if(isTaxEnabled) {
            for(VMposItem item: transactionObject.getItems()){
                double individualTaxAmount = getTaxAmountForItem(item);
                item.setIndividualTaxAmount(BigDecimal.valueOf(individualTaxAmount));
            }
        }
        else {
            for(VMposItem item: transactionObject.getItems()){
                item.setIndividualTaxAmount(BigDecimal.ZERO);
            }
        }
    }

    public void mapTransactionToHistoryItem(){
        String transactionID = transactionObject.getTransactionId();
        String amount = String.valueOf(transactionObject.getAuthorizedAmount());
        String time = transactionObject.getTransactionTime();
        String date = transactionObject.getTransactionDate();
        HistoryItem historyItem = new HistoryItem(transactionID, amount, date, time, false);
        historyTransactionList.add(historyItem);
    }
}
