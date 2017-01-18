package com.visa.ent.mpos;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cybersource.mpos.client.PaymentRequest;
import com.visa.ent.mpos.services.TransactionIntentService;
import com.visa.ent.mpos.transaction.MposTransaction;
import com.visa.ent.mpos.utils.MathUtils;
import com.visa.ent.mpos.utils.PaymentUtil;


/*
 * Copyright © 2016 CyberSource. All rights reserved.
 */

public class OrderDetailsActivity extends BaseActivity {

    private TextView subTotalTextView;
    private TextView taxTitleView;
    private TextView taxTextView;
    private TextView tipTitleView;
    private TextView tipTextView;
    private TextView grandTotalTextView;
    private Button payManualButton;
    private Button payCardReaderButton;
    //private TextView cardTypeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUI();
        refreshUI();
    }

    private void setupUI(){
        subTotalTextView = (TextView) findViewById(R.id.subtotal_text_view);
        taxTitleView = (TextView) findViewById(R.id.tax_title_view);
        taxTextView = (TextView) findViewById(R.id.tax_text_view);
        tipTitleView = (TextView) findViewById(R.id.tip_title_view);
        tipTextView = (TextView) findViewById(R.id.tip_text_view);
        grandTotalTextView = (TextView) findViewById(R.id.grand_total_text_view);

        payManualButton = (Button) findViewById(R.id.pay_with_manual_button);
        payManualButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PaymentUtil.getInstance().setEntryMode(PaymentRequest.PaymentRequestEntryMode.PaymentRequestEntryModeKeyEntry);
                Intent intent = new Intent(OrderDetailsActivity.this, AuthorizingActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.anim_translate_from_right,
                        R.anim.anim_translate_to_left);
            }
        });

        payCardReaderButton = (Button) findViewById(R.id.pay_with_reader_button);
        payCardReaderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PaymentUtil.getInstance().setEntryMode(PaymentRequest.PaymentRequestEntryMode.PaymentRequestEntryModeCardReader);
                Intent intent = new Intent(OrderDetailsActivity.this, AuthorizingActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.anim_translate_from_right,
                        R.anim.anim_translate_to_left);
            }
        });

    }

    public void refreshUI(){
        double subtotal = MposTransaction.getInstance().getTransactionObject()
                .getSubtotalAmount().doubleValue();
        double taxAmount = MposTransaction.getInstance().getTransactionObject()
                .getTotalTax().doubleValue();
        double tipAmount = MposTransaction.getInstance().getTransactionObject()
                .getPurchaseDetails().getTipAmount().doubleValue();
        double grandTotal = MposTransaction.getInstance().getTransactionObject()
                .getTotalAmount().doubleValue();
        int disableColor = getResources().getColor(R.color.lighter_gray);
        int enableColor = getResources().getColor(R.color.darker_gray);
        subTotalTextView.setText(getString(R.string.dollar) + MathUtils.getTwoDecimalInString(subtotal));
        if(isTaxEnabled) {
            taxTextView.setText(getString(R.string.dollar) + MathUtils.getTwoDecimalInString(taxAmount));
            taxTitleView.setTextColor(enableColor);
            taxTextView.setTextColor(enableColor);
        }
        else{
            taxTextView.setText(getString(R.string.zero_amount));
            taxTitleView.setTextColor(disableColor);
            taxTextView.setTextColor(disableColor);
        }
        if(isTipEnabled) {
            tipTextView.setText(getString(R.string.dollar) + MathUtils.getTwoDecimalInString(tipAmount));
            tipTitleView.setTextColor(enableColor);
            tipTextView.setTextColor(enableColor);
        }
        else{
            tipTextView.setText(getString(R.string.zero_amount));
            tipTitleView.setTextColor(disableColor);
            tipTextView.setTextColor(disableColor);
        }
        grandTotalTextView.setText(getString(R.string.dollar) + MathUtils.getTwoDecimalInString
                (MathUtils.roundToTwoDecimal(grandTotal)));
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_order_details;
    }

    @Override protected int getMenuResource() {
        restoreActionBar();
        return R.menu.menu_order_details;
    }

    @Override
    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(getString(R.string.title_activity_order_details));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
