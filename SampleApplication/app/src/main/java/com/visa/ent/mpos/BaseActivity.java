package com.visa.ent.mpos;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.visa.ent.mpos.receivers.TransactionResultReceiver;
import com.visa.ent.mpos.services.TransactionIntentService;
import com.visa.ent.mpos.transaction.MposTransaction;
import java.math.BigDecimal;

/*
 * Copyright © 2016 CyberSource. All rights reserved.
 */

/**
 * Created by CyberSource on 4/28/2015.
 */
public abstract class
        BaseActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener
        , TransactionResultReceiver.Receiver {
    public static final String KEY_PREF_SIGNATURE = "pref_key_signature";
    public static final String KEY_PREF_TAX = "pref_key_tax";
    public static final String KEY_PREF_TIP = "pref_key_tip";
    public static final String KEY_PREF_SEEK = "pref_key_tax_percentage";

    public static final String PAYMENT_ACTION = "payment";
    public static final String REFUND_ACTION = "refund";
    public static final String SEARCH_ACTION = "search";
    public static final String SEARCH_ACTION_DETAIL = "searchDetail";
    public static final String SEARCH_ACTION_NEXT = "searchNext";
    public static final String EMAIL_RECEIPT_ACTION = "emailReceipt";
    public static final String VOID_ACTION = "void";

    protected static boolean isSignatureEnabled;
    protected static boolean isTaxEnabled;
    protected static boolean isTipEnabled;

    protected static Toolbar toolbar;
    protected static SharedPreferences sharedPreferences;

    protected static TransactionResultReceiver mReceiver;
    private final int MY_PERMISSIONS_REQUEST_AUDIO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setUpSettingsPreferences();
        MposTransaction.getInstance().setApplicationContext(getApplicationContext());
        grantPermissions();
    }

    private void grantPermissions(){
        if (ContextCompat.checkSelfPermission(BaseActivity.this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(BaseActivity.this,
                        new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS},
                        MY_PERMISSIONS_REQUEST_AUDIO);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_AUDIO:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getBaseContext(),"Thank you for granting permissions.", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getBaseContext(),"Audio permissions are required for Card Reader to work.",Toast.LENGTH_LONG).show();
                }

            }

    }

    private void setUpSettingsPreferences() {
        // Make sure default values of settings are applied
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //get the initial values for settings from the shared preferences
        isSignatureEnabled = sharedPreferences.getBoolean(KEY_PREF_SIGNATURE, true);
        isTaxEnabled = sharedPreferences.getBoolean(KEY_PREF_TAX, true);
        MposTransaction.getInstance().setIsTaxEnabled(isTaxEnabled);

        isTipEnabled = sharedPreferences.getBoolean(KEY_PREF_TIP, false);
        MposTransaction.getInstance().setIsTipEnabled(isTipEnabled);
    }


    protected abstract int getLayoutResource();
    protected abstract int getMenuResource();
    protected abstract void restoreActionBar();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        int menuResourceId = getMenuResource();
        if(menuResourceId >= 0) {
            getMenuInflater().inflate(menuResourceId, menu);
            restoreActionBar();
            return true;
        }
        else if(menuResourceId == -1) {
            return false;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                //overridePendingTransition();
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        if (key.equals(KEY_PREF_SIGNATURE)) {
            isSignatureEnabled = sharedPreferences.getBoolean(KEY_PREF_SIGNATURE, true);
            //Preference connectionPref = sharedPreference.findPreference(key);
            // Set summary to be the user-description for the selected value
            //connectionPref.setSummary(sharedPreferences.getString(key, ""));
        }
        else if (key.equals(KEY_PREF_TAX)) {
            updateTaxEnabled(sharedPreferences);
        }
        else if (key.equals(KEY_PREF_SEEK)) {
            MposTransaction.getInstance().updateIndividualItemsTax();
        }
        else if (key.equals(KEY_PREF_TIP)) {
            updateTipEnabled(sharedPreferences);
        }
    }

    private void updateTaxEnabled(SharedPreferences sharedPreferences) {
        isTaxEnabled = sharedPreferences.getBoolean(KEY_PREF_TAX, true);
        MposTransaction.getInstance().setIsTaxEnabled(isTaxEnabled);
        MposTransaction.getInstance().updateIndividualItemsTax();
    }

    private void updateTipEnabled(SharedPreferences sharedPreferences) {
        isTipEnabled = sharedPreferences.getBoolean(KEY_PREF_TIP, false);
        MposTransaction.getInstance().setIsTipEnabled(isTipEnabled);
        if(isTipEnabled)
            MposTransaction.getInstance().getTransactionObject()
                    .getPurchaseDetails().setTipAmount(
                    BigDecimal.valueOf(MposTransaction.getInstance().getTipAmount()));
        else
            MposTransaction.getInstance().getTransactionObject()
                    .getPurchaseDetails().setTipAmount(BigDecimal.ZERO);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        boolean success = resultData.getBoolean(TransactionIntentService.SERVICE_RESULT_TAG);
        if(resultCode == TransactionIntentService.SERVICE_RESULT_PAYMENT_CODE){
            if (this instanceof AuthorizingActivity)
                ((AuthorizingActivity) this).updateUI(success);
        }
        else if(resultCode == TransactionIntentService.SERVICE_RESULT_REFUND_CODE){
            if (this instanceof BaseNavigationActivity)
                ((BaseNavigationActivity) this).updateSearchDetailRefund(success);
        }else if(resultCode == TransactionIntentService.SERVICE_RESULT_VOID_CODE){
            //Toast.makeText(getApplicationContext(),"Void completed",Toast.LENGTH_SHORT).show();
            if(this instanceof BaseNavigationActivity)
                ((BaseNavigationActivity) this).updateSearchDetailVoid(success);
        }
        else if(resultCode == TransactionIntentService.SERVICE_RESULT_SEARCH_CODE){
            if(this instanceof BaseNavigationActivity)
                ((BaseNavigationActivity) this).updateSearch(success);
        }else if(resultCode == TransactionIntentService.SERVICE_RESULT_SEARCH_DETAIL_CODE){
            if(this instanceof BaseNavigationActivity)
                ((BaseNavigationActivity) this).updateSearchDetail(success);
        }else if(resultCode == TransactionIntentService.SERVICE_RESULT_SEARCH_NEXT_CODE) {
            if (this instanceof BaseNavigationActivity)
                ((BaseNavigationActivity) this).updateSearchNext(success);
        }
    }


    /**
     * This method checks if given class exists based on its name. If it does
     * exist it also checks if conforms to a given class type (VMposReaderDriver).
     *
     * @param className the name of the class to be checked
     * @param clazz type for comparison
     * @return {@code true} when class exists and it is of a given type, {code
     *         false} otherwise
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static boolean checkIfClassExists(String className, Class clazz) {
        try {
            // check if class exists and if it is instance of given class
            if (clazz.isAssignableFrom(Class.forName(className))) {
                return true;
            }
        } catch (ClassNotFoundException e) {
            // class not found - no need to handle such situation
            e.printStackTrace();
        }

        return false;
    }

    protected void registerResultReceiver() {
        mReceiver = new TransactionResultReceiver(new Handler());
        mReceiver.setReceiver(this);
    }
}
