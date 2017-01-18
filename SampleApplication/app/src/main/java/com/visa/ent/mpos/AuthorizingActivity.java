package com.visa.ent.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cybersource.mpos.client.PaymentRequest;
import com.cybersource.mpos.client.PaymentResponse;
import com.cybersource.mpos.client.ReceiptRequest;
import com.visa.ent.mpos.fragments.PayTransactionHeadlessFrangment;
import com.visa.ent.mpos.receivers.TransactionResultReceiver;
import com.visa.ent.mpos.services.TransactionIntentService;
import com.visa.ent.mpos.transaction.MposTransaction;
import com.visa.ent.mpos.utils.MathUtils;
import com.visa.ent.mpos.utils.PaymentUtil;
import com.visa.ent.mpos.utils.SearchUtil;
import com.visainc.mpos.sdk.common.error.VMposError;
import com.visainc.mpos.sdk.connectors.cybersource.responses.VMposResponseDecision;
import com.visainc.mpos.sdk.datamodel.VMposGateway;
import com.visainc.mpos.sdk.datamodel.VMposGatewayErrorMapping;
import com.visainc.mpos.sdk.datamodel.VMposGatewayResponse;
import com.visainc.mpos.sdk.datamodel.transaction.VMposTransactionObject;
import com.visainc.mpos.sdk.transactions.VMposPaymentUpdateType;
import com.visainc.mpos.sdk.transactions.interfaces.VMposPaymentDelegate;


/*
 * Copyright © 2016 CyberSource. All rights reserved.
 */

public class AuthorizingActivity extends BaseActivity {

    public static final String TAG_FRAGMENT_SERVICE = "fragment_service";

    private ProgressBar progressBar;
    private TextView amountTextView;
    private TextView cardTypeTextView;
    private TextView primaryTextView;
    private TextView successTextView;
    private ImageView checkMarkImageView;
    private CardView receiptCardView;
    private TextView grandTotalTextView;
    private TextView transactionIDTextView;
    private TextView transactionIDLabel;
    private Button doneButton;
    private boolean inTransaction = true;
    private VMposTransactionObject transactionObject;
    private boolean success = false;
    private String toEmail;
    private ReceiptRequest receiptRequestObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transactionObject = MposTransaction.getInstance().getTransactionObject();
        setupUI();
        findFragmentForService();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_authorizing;
    }

    @Override protected int getMenuResource() {
        restoreActionBar();
        return R.menu.menu_authorizing;
    }

    @Override
    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(getString(R.string.title_activity_authorizing));
    }

    private void setupUI(){
        progressBar = (ProgressBar) findViewById(R.id.authorizing_progressBar);
        amountTextView = (TextView) findViewById(R.id.authorizing_amount);
        amountTextView.setText(getString(R.string.dollar) + MathUtils.getTwoDecimalInString
                (MposTransaction.getInstance().getTransactionObject().getTotalAmount().doubleValue()));
        cardTypeTextView = (TextView) findViewById(R.id.card_type_text_view_auth_activity);
        cardTypeTextView.setVisibility(View.INVISIBLE);
        primaryTextView = (TextView) findViewById(R.id.authorizing_text_view);
        successTextView = (TextView) findViewById(R.id.authorizing_text_view_second);
        checkMarkImageView = (ImageView) findViewById(R.id.authorized_success_checkmark);
        receiptCardView = (CardView) findViewById(R.id.receipt_card_view);
        transactionIDTextView = (TextView) findViewById(R.id.transaction_id_text_view);
        transactionIDLabel = (TextView) findViewById(R.id.transaction_id_label);
        grandTotalTextView = (TextView) findViewById(R.id.grand_total_text_view);
        grandTotalTextView.setText(getString(R.string.dollar) + MposTransaction.getInstance()
                .getTransactionObject().getTotalAmount());
        doneButton = (Button) findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (success)
                    saveTransactionObject();
                destroyTransactionObject();
                everythingIsDone(true);
            }
        });
    }

    private void everythingIsDone(boolean fromDoneButton) {
        Intent intent = new Intent(this, BaseNavigationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

        if(fromDoneButton)
            overridePendingTransition(R.anim.anim_translate_from_right,
                    R.anim.anim_translate_to_left);
        else
            overridePendingTransition(R.anim.anim_transalate_from_left,
                    R.anim.anim_translate_to_right);
    }

    private void emailDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(AuthorizingActivity.this);
        View emailDialogView = layoutInflater.inflate(R.layout.email_receipt_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AuthorizingActivity.this);
        alertDialogBuilder.setView(emailDialogView);
        alertDialogBuilder.setTitle(com.visainc.mpos.sdk.R.string.email_dialog_title);
        final EditText emailAddressEditText = (EditText)emailDialogView.findViewById(R.id.edittextEmailAddress);
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        toEmail = emailAddressEditText.getText().toString();
                        if(toEmail.length()>0){
                            createReceiptRequestObject();
                            Intent msgIntent = new Intent(AuthorizingActivity.this, TransactionIntentService.class);
                            msgIntent.putExtra(TransactionIntentService.SERVICE_ACTION_TAG, BaseActivity.EMAIL_RECEIPT_ACTION);
                            msgIntent.putExtra(TransactionIntentService.SERVICE_RESULT_RECEIVER, mReceiver);
                            SearchUtil.getInstance().setReceiptRequest(receiptRequestObject);
                            AuthorizingActivity.this.startService(msgIntent);
                        }
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();

    }

    private void createReceiptRequestObject(){
        receiptRequestObject = new ReceiptRequest();
        PaymentRequest paymentRequest = PaymentUtil.getInstance().getPaymentRequest();
        PaymentResponse paymentResponse = PaymentUtil.getInstance().getPaymentResponse();
        String oAuthToken = PaymentUtil.getInstance().getoAuthToken();
        if(paymentResponse != null){
            receiptRequestObject.setTransactionID(paymentResponse.getRequestID());
            receiptRequestObject.setAuthCode(paymentResponse.getAuthorizationCode());
        }
        if(paymentRequest != null){
            receiptRequestObject.setMerchantReferenceCode(paymentRequest.getMerchantReferenceCode());
            receiptRequestObject.setTotalPurchaseAmount(paymentRequest.getPurchaseTotal().getGrandTotalAmount() + "");
        }
        receiptRequestObject.setToEmail(toEmail);
        receiptRequestObject.setFromEmail("no-reply@cybersource.com");
        receiptRequestObject.setEmailSubject("Your Transaction Receipt");
        receiptRequestObject.setMerchantDescriptor("Cybersource");
        receiptRequestObject.setMerchantDescriptorStreet("P.O. Box 8999");
        receiptRequestObject.setMerchantDescriptorCity("San Francisco");
        receiptRequestObject.setMerchantDescriptorState("CA");
        receiptRequestObject.setMerchantDescriptorPostalCode("94128-8999");
        receiptRequestObject.setMerchantDescriptorCountry("USA");
        receiptRequestObject.setShippingAmount("0.0");
        receiptRequestObject.setTaxAmount("0.0");
        receiptRequestObject.setAccessToken(oAuthToken);
    }

    public void updateUI(boolean success){

        this.success = success;
        inTransaction = false;
        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha_checkmark);
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(animAlpha);
        progressBar.setVisibility(View.GONE);
        transactionIDLabel.setVisibility(View.INVISIBLE);
        transactionIDTextView.setVisibility(View.INVISIBLE);
        if(success){
            receiptCardView.startAnimation(animationSet);
            receiptCardView.setVisibility(View.VISIBLE);
            primaryTextView.setText(getString(R.string.transaction_complete));
            primaryTextView.setTextSize(24);
            successTextView.setVisibility(View.GONE);
            emailDialog();
        }
        else{
            primaryTextView.setText(getString(R.string.transaction_failed));
            primaryTextView.setTextSize(24);
            primaryTextView.setTextColor(getResources().getColor(R.color.red));
            successTextView.setText(getString(R.string.something_went_wrong));
        }
        doneButton.startAnimation(animationSet);
        doneButton.setVisibility(View.VISIBLE);
    }

    private void saveTransactionObject() {
        MposTransaction.getInstance().getTransactionObjects().add(transactionObject);
        MposTransaction.getInstance().mapTransactionToHistoryItem();
    }

    private void destroyTransactionObject(){
        MposTransaction.getInstance().destroyTransactionObject();
        MposTransaction.getInstance().destroyCartItems();
    }

    private void findFragmentForService(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        PayTransactionHeadlessFrangment paymentState =
                (PayTransactionHeadlessFrangment)fragmentManager
                        .findFragmentByTag(TAG_FRAGMENT_SERVICE);

        if(paymentState == null) {
            registerResultReceiver();
            paymentState = new PayTransactionHeadlessFrangment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(PayTransactionHeadlessFrangment.ARG_RESULT_RECEIVER, mReceiver);
            paymentState.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .add(paymentState, TAG_FRAGMENT_SERVICE).commit();
        }
    }

    @Override
    public void onBackPressed() {
        if(!inTransaction){
            if(success)
                saveTransactionObject();
            everythingIsDone(false);
        }
    }
}
