package com.visa.ent.mpos.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;

import com.cybersource.mpos.client.Manager;
import com.cybersource.mpos.client.ManagerDelegate;
import com.cybersource.mpos.client.PaymentError;
import com.cybersource.mpos.client.PaymentRequest;
import com.cybersource.mpos.client.PaymentResponse;
import com.cybersource.mpos.client.PurchaseTotal;
import com.cybersource.mpos.client.ReceiptRequest;
import com.cybersource.mpos.client.RefundRequest;
import com.cybersource.mpos.client.Settings;
import com.cybersource.mpos.client.Transaction;
import com.cybersource.mpos.client.TransactionSearchQuery;
import com.cybersource.mpos.client.TransactionSearchResult;
import com.cybersource.mpos.client.VoidRequest;
import com.visa.ent.mpos.BaseActivity;
import com.visa.ent.mpos.transaction.MposTransaction;
import com.visa.ent.mpos.utils.PRSAPIConnection;
import com.visa.ent.mpos.utils.PaymentUtil;
import com.visa.ent.mpos.utils.SearchUtil;
import com.visa.ent.mpos.utils.TokenDelegate;
import com.visa.ent.mpos.utils.TokenParameters;
import com.visainc.mpos.sdk.datamodel.transaction.VMposTransactionObject;
import java.math.BigDecimal;

/*
 * Copyright © 2016 CyberSource. All rights reserved.
 */

/**
 * Created by CyberSource on 7/10/2015.
 */
public class TransactionIntentService extends IntentService {

    public static final String SERVICE_ACTION_TAG = "transaction_action";
    public static final String SERVICE_RESULT_TAG = "success_result";
    public static final String SERVICE_RESULT_RECEIVER = "service_result_receiver";
    public static final String SERVICE_REFUND_TRANSACTIONID = "refund_transactionID";
    public static final String SERVICE_VOID_TRANSACTIONID = "void_transactionID";
    public static final String SERVICE_SEARCH_TRANSACTIONID = "search_transactionID";
    public static final String SERVICE_REFUND_AMOUNT = "refund_amount";
    public static final String SERVICE_VOID_AMOUNT = "void_amount";
    public static final int SERVICE_RESULT_PAYMENT_CODE = 100;
    public static final int SERVICE_RESULT_REFUND_CODE = 200;
    public static final int SERVICE_RESULT_SEARCH_CODE = 300;
    public static final int SERVICE_RESULT_SEARCH_DETAIL_CODE = 400;
    public static final int SERVICE_RESULT_VOID_CODE = 500;
    public static final int SERVICE_RESULT_SEARCH_NEXT_CODE = 600;
    private String oAuthToken;
    private String deviceID;
    private String merchantID;
    private PaymentRequest paymentReqObject;
    private RefundRequest refundReqObject;
    private VoidRequest voidRequestObject;
    private String environment;
    private String terminalID;
    private String terminalIDAlternate;
    private String mid;
    private final String TERMINALID_KEY="pref_key_terminalId";
    private final String TERMINALID_ALTERNATE_KEY="pref_key_terminalIdAlternate";
    private final String MID_KEY="pref_key_mid";
    private static final String ENVIRONMENT_PREF_KEY = "pref_key_environment";
    private static final String MERCHANTID_PREF_KEY = "pref_key_merchantId";
    private static final String DEVICEID_PREF_KEY = "pref_key_deviceId";
    private static final String CLIENTID_PREF_KEY = "pref_key_clientId";
    private static final String USERNAME_PREF_KEY = "pref_key_username";
    private static final String PASSWORD_PREF_KEY = "pref_key_password";
    private final String DEVICEID_KEY="pref_key_deviceId";
    private final String MERCHANTID_KEY="pref_key_merchantId";
    Manager manager;
    Intent currentIntent;
    private ReceiptRequest receiptRequestObject;
    private ResultReceiver resultReceiver;
    private String transactionType;
    private VMposTransactionObject transactionObject;
    private TransactionSearchQuery searchQuery;
    SharedPreferences sharedPreferences;

    public TransactionIntentService() {
        super("TransactionIntentService");
    }

    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }
    private void generateOAuthToken(){
        TokenParameters tokenParameter = new TokenParameters();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        tokenParameter.setClientID(sharedPreferences.getString(CLIENTID_PREF_KEY, ""));
        tokenParameter.setMerchantID(sharedPreferences.getString(MERCHANTID_PREF_KEY, ""));
        tokenParameter.setDeviceID(sharedPreferences.getString(DEVICEID_PREF_KEY, ""));
        tokenParameter.setGrantType("password");
        if(environment!= null){
            if(environment.equalsIgnoreCase("TEST")){
                tokenParameter.setURL(TokenParameters.TEST_GENERATE_TOKEN_URL);
            }else if(environment.equalsIgnoreCase("LIVE")){
                tokenParameter.setURL(TokenParameters.PROD_GENERATE_TOKEN_URL);
            }
        }
        tokenParameter.setUsername(sharedPreferences.getString(USERNAME_PREF_KEY,""));
        tokenParameter.setPassword(sharedPreferences.getString(PASSWORD_PREF_KEY,""));
        tokenParameter.setPlatform("1");
        PRSAPIConnection.connection(tokenParameter, tokenDelegate);
    }

    private final TokenDelegate tokenDelegate = new TokenDelegate() {
        @Override
        public void tokenGenerationFinished(String token) {
            if(token.length()>0) {
                oAuthToken = token;
                PaymentUtil.getInstance().setoAuthToken(token);
                performTransactions(currentIntent);
            }else{
                resultReceiver = currentIntent.getParcelableExtra(SERVICE_RESULT_RECEIVER);
                transactionType = currentIntent.getStringExtra(SERVICE_ACTION_TAG);
                sendToResultReciever(false);
            }
        }
    };

    @Override
    protected void onHandleIntent(Intent intent) {
        currentIntent = intent;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        environment = sharedPreferences.getString(ENVIRONMENT_PREF_KEY, "TEST");
        terminalID = sharedPreferences.getString(TERMINALID_KEY, "47");
        terminalIDAlternate = sharedPreferences.getString(TERMINALID_ALTERNATE_KEY, "57");
        mid = sharedPreferences.getString(MID_KEY, "");
        merchantID = sharedPreferences.getString(MERCHANTID_KEY,"");
        deviceID = sharedPreferences.getString(DEVICEID_KEY,"");
        if(environment.equalsIgnoreCase("TEST")) {
            manager = getCASManager(deviceID);
            generateOAuthToken();
        } else if(environment.equalsIgnoreCase("LIVE")){
            manager = getProdManager(deviceID);
            generateOAuthToken();
        }
    }


    private void performTransactions(Intent intent){
        resultReceiver = intent.getParcelableExtra(SERVICE_RESULT_RECEIVER);
        transactionType = intent.getStringExtra(SERVICE_ACTION_TAG);
        if(transactionType!= null) {
            if (transactionType.equals(BaseActivity.PAYMENT_ACTION)) {
                transactionObject = MposTransaction.getInstance().getTransactionObject();
                performTransaction();
            }
            else if (transactionType.equals(BaseActivity.REFUND_ACTION)) {
                String transactionID = intent.getStringExtra(SERVICE_REFUND_TRANSACTIONID);
                String amount = intent.getStringExtra(SERVICE_REFUND_AMOUNT);
                performRefund(transactionID, amount);
            }else if(transactionType.equals(BaseActivity.VOID_ACTION)){
                String transactionID = intent.getStringExtra(SERVICE_VOID_TRANSACTIONID);
                String amount = intent.getStringExtra(SERVICE_VOID_AMOUNT);
                performVoid(transactionID, amount);
            }
            else if(transactionType.equals(BaseActivity.SEARCH_ACTION)){
                searchQuery = SearchUtil.getInstance().getSearchQuery();
                if(searchQuery != null){
                    performSearch();
                }
            }else if(transactionType.equals(BaseActivity.SEARCH_ACTION_DETAIL)){
                String transactionID = intent.getStringExtra(SERVICE_SEARCH_TRANSACTIONID);
                if(transactionID != null){
                    performSearchDetail(transactionID);
                }
            }else if(transactionType.equals(BaseActivity.SEARCH_ACTION_NEXT)){
                performSearchNext();
            }else if(transactionType.equals(BaseActivity.EMAIL_RECEIPT_ACTION)){
                receiptRequestObject = SearchUtil.getInstance().getReceiptRequest();
                receiptRequestObject.setAccessToken(oAuthToken);
                if(receiptRequestObject != null){
                    sendEmailReceipt();
                }
            }
        }
    }

    private void sendToResultReciever(Boolean success){
        Bundle resultData = new Bundle();
        resultData.putBoolean(SERVICE_RESULT_TAG, success);
        if (transactionType.equals(BaseActivity.PAYMENT_ACTION)) {
            if(resultReceiver != null)
                resultReceiver.send(SERVICE_RESULT_PAYMENT_CODE, resultData);
        }
        else if (transactionType.equals(BaseActivity.REFUND_ACTION)) {
            if(resultReceiver != null)
                resultReceiver.send(SERVICE_RESULT_REFUND_CODE, resultData);
        }
        else if (transactionType.equals(BaseActivity.VOID_ACTION)) {
            if(resultReceiver != null)
                resultReceiver.send(SERVICE_RESULT_VOID_CODE, resultData);
        }else if(transactionType.equals(BaseActivity.SEARCH_ACTION)){
            if(resultReceiver != null)
                resultReceiver.send(SERVICE_RESULT_SEARCH_CODE, resultData);
        }else if(transactionType.equals(BaseActivity.SEARCH_ACTION_DETAIL)){
            if(resultReceiver != null)
                resultReceiver.send(SERVICE_RESULT_SEARCH_DETAIL_CODE, resultData);
        }else if(transactionType.equals(BaseActivity.SEARCH_ACTION_NEXT)){
            if(resultReceiver != null)
                resultReceiver.send(SERVICE_RESULT_SEARCH_NEXT_CODE, resultData);
        }
    }

    /**
     * Perform the actual Sale-transaction against the CyberSource Gateway
     */
    public void performTransaction(){
        preparePaymentRequestObject();
        manager.performPayment(paymentReqObject, this, managerDelegate);
    }
    private Manager getCASManager(String deviceID){
        Settings settings = new Settings(Settings.Environment.ENV_TEST,deviceID,terminalID,terminalIDAlternate,mid);
        return new Manager(settings);
    }
    private Manager getProdManager(String deviceID){
        Settings settings = new Settings(Settings.Environment.ENV_LIVE,deviceID,terminalID,terminalIDAlternate,mid);
        return new Manager(settings);
    }


    public void performSearch(){
        manager.performTransactionSearch(searchQuery, oAuthToken, managerDelegate);
    }

    public void performSearchDetail(String transactionID){
        manager.getTransactionDetail(transactionID,oAuthToken, managerDelegate);
    }

    public void performSearchNext(){
        TransactionSearchResult transactionSearchResult = SearchUtil.getInstance().getTransactionSearchResult();
        manager.nextTransactionSearchResult(transactionSearchResult, oAuthToken, managerDelegate);
    }

    public void sendEmailReceipt(){
        manager.sendReceipt(receiptRequestObject, managerDelegate);
    }


    /**
     * Create Payment Request object with fields.
     * MerchantID, AccessToken, PurchaseTotal(Currency,GrandTotal), MerchantReferenceCode
     *
     */
    private void preparePaymentRequestObject(){
        paymentReqObject = new PaymentRequest();
        paymentReqObject.setMerchantId(merchantID);
        paymentReqObject.setAccessToken(oAuthToken);
        PurchaseTotal purchaseTotal = new PurchaseTotal();
        purchaseTotal.setCurrency("USD");
        purchaseTotal.setGrandTotalAmount(transactionObject.getTotalAmount());
        paymentReqObject.setPurchaseTotal(purchaseTotal);
        paymentReqObject.setMerchantReferenceCode(Long.toString(System.currentTimeMillis()));
        if(PaymentUtil.getInstance().getEntryMode() == PaymentRequest.PaymentRequestEntryMode.PaymentRequestEntryModeCardReader) {
            paymentReqObject.setEntryMode(PaymentRequest.PaymentRequestEntryMode.PaymentRequestEntryModeCardReader);
        }else{
            paymentReqObject.setEntryMode(PaymentRequest.PaymentRequestEntryMode.PaymentRequestEntryModeKeyEntry);
        }
    }

    private void prepareRefundRequestObject(String transactionID, BigDecimal amount){
        refundReqObject = new RefundRequest();
        refundReqObject.setMerchantID(merchantID);
        refundReqObject.setAccessToken(oAuthToken);
        refundReqObject.setCurrency("USD");
        refundReqObject.setAmount(amount);
        refundReqObject.setMerchantReferenceCode(Long.toString(System.currentTimeMillis()));
        refundReqObject.setTransactionID(transactionID);
    }

    private void prepareVoidRequestObject(String transationID, BigDecimal amount){
        voidRequestObject = new VoidRequest();
        voidRequestObject.setMerchantID(merchantID);
        voidRequestObject.setAccessToken(oAuthToken);
        voidRequestObject.setCurrency("USD");
        voidRequestObject.setAmount(amount);
        voidRequestObject.setMerchantReferenceCode(Long.toString(System.currentTimeMillis()));
        voidRequestObject.setTransactionID(transationID);
    }

    /**
     * Manager delegate that will receive all the callbacks from the Gateway
     */
    private final ManagerDelegate managerDelegate = new ManagerDelegate() {
        @Override
        public void performPaymentDidFinish(PaymentResponse paymentResponse, PaymentError paymentError) {
            //Perform action
            if(paymentError != null){
                sendToResultReciever(false);
            }else{
                PaymentUtil.getInstance().setPaymentRequest(paymentReqObject);
                PaymentUtil.getInstance().setPaymentResponse(paymentResponse);
                sendToResultReciever(true);
            }
        }

        @Override
        public void refundDidFinish(PaymentResponse paymentResponse, PaymentError paymentError){
            if(paymentError != null){
                sendToResultReciever(false);
            }else{
                sendToResultReciever(true);
            }
        }

        @Override
        public void voidDidFinish(PaymentResponse paymentResponse, PaymentError paymentError){
            if(paymentError != null){
                sendToResultReciever(false);
            }else{
                sendToResultReciever(true);
            }
        }

        @Override
        public void performTransactionSearchDidFinish(TransactionSearchResult transactionSearchResult, PaymentError paymentError){
            if(paymentError != null){
                sendToResultReciever(false);
            }else {
                SearchUtil.getInstance().setTransactionSearchResult(transactionSearchResult);
                if (transactionType.equals(BaseActivity.SEARCH_ACTION)) {
                    SearchUtil.getInstance().setSearchResult(transactionSearchResult.getTransactions());
                } else if(transactionType.equals(BaseActivity.SEARCH_ACTION_NEXT)){
                    SearchUtil.getInstance().addNextSearchResult(transactionSearchResult.getTransactions());
                }
                sendToResultReciever(true);
            }
        }

        @Override
        public void getTransactionDetailDidFinish(Transaction transaction, PaymentError paymentError){
            if(paymentError != null){
                sendToResultReciever(false);
            }
            else{
                SearchUtil.getInstance().setSearchResponseDetail(transaction);
                sendToResultReciever(true);
            }
        }

        @Override
        public void sendReceiptDidFinish(){

        }

    };

    /**
     * gateway transaction to perform a REFUND transaction
     */
    public void performRefund(String transactionID, String amount){
        BigDecimal totalAmount = new BigDecimal(amount);
        prepareRefundRequestObject(transactionID, totalAmount);
        manager.performRefund(refundReqObject,managerDelegate);
    }

    public void performVoid(String transactionID, String amount){
        BigDecimal totalAmount = new BigDecimal(amount);
        prepareVoidRequestObject(transactionID, totalAmount);
        manager.performVoid(voidRequestObject,managerDelegate);
    }

}
