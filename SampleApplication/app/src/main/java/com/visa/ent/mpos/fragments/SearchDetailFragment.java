package com.visa.ent.mpos.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cybersource.mpos.client.ReceiptRequest;
import com.cybersource.mpos.client.Transaction;
import com.cybersource.mpos.client.TransactionActionType;
import com.cybersource.mpos.client.TransactionType;
import com.visa.ent.mpos.BaseNavigationActivity;
import com.visa.ent.mpos.R;
import com.visa.ent.mpos.dialogs.YesNoDialog;
import com.visa.ent.mpos.utils.SearchUtil;
import com.visainc.mpos.sdk.datamodel.VMposTransactionSearchQuery;
import com.visainc.mpos.sdk.datamodel.transaction.VMposHistoryTransactionEvent;
import com.visainc.mpos.sdk.datamodel.transaction.VMposTransactionObject;

import org.spongycastle.jcajce.provider.symmetric.ARC4;

import java.math.BigDecimal;
import java.util.List;

/*
 * Copyright © 2016 CyberSource. All rights reserved.
 */

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchDetailFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String ARG_RESULT_RECEIVER = "result_receiver";
    public static final String ARG_HISTORY_POSITION = "history_position";
    private static final int YES_NO_CALL = 2;
    private static final int YES_NO_CALL_VOID = 3;

    public static final String TAG_REFUND_DIALOG = "Tag_Refund_Dialog";
    public static final String TAG_VOID_DIALOG = "Tag_void_Dialog";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int position;

    private OnFragmentInteractionListener mListener;
    private TextView date;
    private TextView transactionID;
    private TextView transactionStatus;
    private TextView merchantReferenceCode;
    private TextView transactionReferenceNumber;
    private TextView authorizationCode;
    private TextView reasonCode;
    private TextView replyMessage;
    private TextView requestToken;
    private TextView status;
    private TextView paymentType;
    private TextView cardExpiry;
    private TextView processor;
    private TextView currency;
    private TextView merchantName;
    private TextView amount;
    private TextView cardType;
    private TextView cardSuffix;
    private Button emailButton;
    private Button refundButton;
    private Button voidButton;
    Transaction transactionDetails;
    private ProgressDialog progressDialog;
    private ReceiptRequest receiptRequest;
    private String toEmail;


    public SearchDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param position Parameter 1.
     * @return A new instance of fragment SearchDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchDetailFragment newInstance(int position) {
        SearchDetailFragment fragment = new SearchDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_HISTORY_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_HISTORY_POSITION);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_detail, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setupUI(view);
        findFragmentForService();
        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_settings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings_done){
            getFragmentManager().popBackStackImmediate();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showProgressbar(){
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Processing..");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    private void dismissProgressbar(){
        progressDialog.dismiss();
    }


    private void findFragmentForService(){
        String transactionID = SearchUtil.getInstance().getSearchResult().get(position).getTransactionID();
        if(transactionID != null){
            showProgressbar();
            ((BaseNavigationActivity)getActivity()).searchDetailService(transactionID);
        }
    }

    public void updateDetailSearchUI(boolean success){
        dismissProgressbar();
        if(success){
            setSearchDetails();
        }
    }

    public void updateDetailSearchUIVoid(boolean success){
        dismissProgressbar();
        if(success){
            voidButton.setEnabled(false);
            Toast.makeText(getActivity().getBaseContext(),"Void Completed", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateDetailSearchUIRefund(boolean success){
        dismissProgressbar();
        if(success){
            refundButton.setEnabled(false);
            Toast.makeText(getActivity().getBaseContext(),"Refund Completed", Toast.LENGTH_SHORT).show();
        }
    }


    private void setupUI(View view){

        transactionID = (TextView)view.findViewById(R.id.textViewTransactionId);
        date = (TextView)view.findViewById(R.id.textViewDate);
        transactionStatus = (TextView)view.findViewById(R.id.textViewTransactionStatus);
        currency = (TextView)view.findViewById(R.id.textViewCurrency);
        amount = (TextView)view.findViewById(R.id.textViewAmount);
        merchantReferenceCode = (TextView)view.findViewById(R.id.textViewMerchantReferenceCode);
        transactionReferenceNumber = (TextView)view.findViewById(R.id.textViewTransactionRefNo);
        authorizationCode = (TextView)view.findViewById(R.id.textViewAuthorizationCode);
        reasonCode = (TextView)view.findViewById(R.id.textViewReasonCode);
        replyMessage = (TextView)view.findViewById(R.id.textViewReplyMessage);
        requestToken = (TextView)view.findViewById(R.id.textViewRequestToken);
        status = (TextView)view.findViewById(R.id.textViewStatus);
        paymentType = (TextView)view.findViewById(R.id.textViewPaymentType);
        merchantName = (TextView)view.findViewById(R.id.textViewMerchantName);
        cardType = (TextView)view.findViewById(R.id.textViewCardTypeLabel);
        cardSuffix = (TextView)view.findViewById(R.id.textViewCardNumber);
        cardExpiry = (TextView)view.findViewById(R.id.textViewCardExpiry);
        processor = (TextView)view.findViewById(R.id.textViewProcessor);
        emailButton = (Button)view.findViewById(R.id.sendEmailButton);
        voidButton = (Button)view.findViewById(R.id.voidButton);
        refundButton = (Button)view.findViewById(R.id.refundButton);
        emailButton.setOnClickListener(this);
        voidButton.setOnClickListener(this);
        refundButton.setOnClickListener(this);
        voidButton.setEnabled(false);
        refundButton.setEnabled(false);
        emailButton.setEnabled(false);

    }
    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.sendEmailButton:
                emailDialog();
                break;
            case R.id.refundButton:
                if(transactionDetails.getTransactionID() != null && transactionDetails.getAmount()!= null) {
                    refundDialog();
                }
                break;
            case R.id.voidButton:
                if(transactionDetails.getTransactionID() != null && transactionDetails.getAmount() != null){
                    voidDialog();
                }
                break;
            default:
                break;
        }
    }

    private void setSearchDetails(){
        if(SearchUtil.getInstance().getSearchResponseDetail()!= null) {
            transactionDetails = SearchUtil.getInstance().getSearchResponseDetail();
            if (transactionDetails != null) {
                transactionID.setText(transactionDetails.getTransactionID());
                date.setText(transactionDetails.getTransactionDate() + " " + transactionDetails.getTransactionTime());
                transactionStatus.setText(transactionDetails.getTransactionType().name());
                currency.setText(transactionDetails.getCurrency());
                amount.setText(transactionDetails.getAmount()+"");
                merchantReferenceCode.setText(transactionDetails.getMerchantReferenceCode());
                transactionReferenceNumber.setText(transactionDetails.getTransRefNo());
                authorizationCode.setText(transactionDetails.getAuthCode());
                reasonCode.setText(transactionDetails.getReasonCode()+"");
                replyMessage.setText(transactionDetails.getReplyMessage());
                requestToken.setText(transactionDetails.getRequestToken());
                status.setText(transactionDetails.getStatus());
                paymentType.setText(transactionDetails.getPaymentInfo().getPaymentType());
                merchantName.setText(transactionDetails.getPaymentInfo().getFullName());
                cardType.setText(transactionDetails.getPaymentInfo().getCardType());
                cardSuffix.setText(transactionDetails.getPaymentInfo().getAccountSuffix());
                if(transactionDetails.getPaymentInfo().getExpirationMonth()!=null||
                        transactionDetails.getPaymentInfo().getExpirationYear()!=null){
                    cardExpiry.setText(transactionDetails.getPaymentInfo().getExpirationMonth().toString()+"/"+transactionDetails.getPaymentInfo().getExpirationYear().toString());
                }
                processor.setText(transactionDetails.getPaymentInfo().getProcessor());
                enableActions();
            }
        }
    }

    private void enableActions(){
        if(canSendReceipt()){emailButton.setEnabled(true);}
        if(canVoid()){voidButton.setEnabled(true);}
        if(canRefund()){refundButton.setEnabled(true);}
    }

    private boolean canSendReceipt(){
        TransactionType transactionType = transactionDetails.getTransactionType();
        switch (transactionType){
            case Capture:
            case Sale:
                return true;
            default:
                break;
        }
        return false;
    }

    private boolean canVoid(){
        TransactionType transactionType = transactionDetails.getTransactionType();
        switch (transactionType){
            case Capture:
            case Sale:
            case Refund:
                if(transactionDetails.getStatus()!= null && transactionDetails.getStatus().equalsIgnoreCase("Pending")){
                    return true;
                }else{
                    return false;
                }
            default:
                break;
        }
        return false;
    }

    private boolean canReverse(){
        TransactionType transactionType = transactionDetails.getTransactionType();
        switch (transactionType){
            case Authorization:
                if(transactionDetails.getActions()!= null && transactionDetails.getActions().name()== TransactionActionType.Reverse.name()){
                    return true;
                }
            default:
                break;
        }
        return false;
    }

    private boolean canRefund(){
        TransactionType transactionType = transactionDetails.getTransactionType();
        switch (transactionType){
            case Capture:
            case Sale:
                if(transactionDetails.getStatus() != null && (transactionDetails.getStatus().equalsIgnoreCase("Transmitted") ||
                        transactionDetails.getStatus().equalsIgnoreCase("Pending"))){
                    if(transactionDetails.getEvents()!= null){
                        for(Transaction event : transactionDetails.getEvents()){
                            if(event.getTransactionType()==TransactionType.Refund){
                                return false;
                            }
                        }
                        return true;
                    }
                }
            default:
                break;
        }
        return false;
    }

    private void refundDialog() {
        DialogFragment dialog = new YesNoDialog();
        Bundle args = new Bundle();
        args.putString(YesNoDialog.ARG_TITLE, getActivity().getString(R.string.refund));
        args.putString(YesNoDialog.ARG_MESSAGE, getActivity().getString(R.string.want_to_refund)
                + " " + transactionDetails.getTransactionID() + "?");
        args.putString(YesNoDialog.ARG_POSITIVE, getActivity().getString(R.string.refund));
        args.putString(YesNoDialog.ARG_NEGATIVE, getActivity().getString(android.R.string.no));
        args.putBoolean(YesNoDialog.ARG_FROM_FRAGMENT, true);
        dialog.setArguments(args);
        dialog.setTargetFragment(this, YES_NO_CALL);
        dialog.show(getFragmentManager(), TAG_REFUND_DIALOG);
    }

    private void voidDialog() {
        DialogFragment dialog = new YesNoDialog();
        Bundle args = new Bundle();
        args.putString(YesNoDialog.ARG_TITLE, getActivity().getString(R.string.doVoid));
        args.putString(YesNoDialog.ARG_MESSAGE, getActivity().getString(R.string.want_to_void)
                + " " + transactionDetails.getTransactionID() + "?");
        args.putString(YesNoDialog.ARG_POSITIVE, getActivity().getString(R.string.doVoid));
        args.putString(YesNoDialog.ARG_NEGATIVE, getActivity().getString(android.R.string.no));
        args.putBoolean(YesNoDialog.ARG_FROM_FRAGMENT, true);
        dialog.setArguments(args);
        dialog.setTargetFragment(this, YES_NO_CALL_VOID);
        dialog.show(getFragmentManager(), TAG_VOID_DIALOG);
    }

    private void emailDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View emailDialogView = layoutInflater.inflate(R.layout.email_receipt_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(emailDialogView);
        alertDialogBuilder.setTitle(com.visainc.mpos.sdk.R.string.email_dialog_title);
        final EditText emailAddressEditText = (EditText)emailDialogView.findViewById(R.id.edittextEmailAddress);
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //resultText.setText("Hello, " + editText.getText());
                        toEmail = emailAddressEditText.getText().toString();
                        if(toEmail.length()>0){
                            createReceiptRequestObject();
                            ((BaseNavigationActivity)getActivity()).sendEmailService(receiptRequest);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case YES_NO_CALL:
                if(resultCode == Activity.RESULT_OK){
                    showProgressbar();
                    ((BaseNavigationActivity)getActivity()).findFragmentForService(transactionDetails.getTransactionID(),
                            transactionDetails.getAmount().toString());
                }
                break;
            case YES_NO_CALL_VOID:
                if(resultCode == Activity.RESULT_OK){
                    showProgressbar();
                    ((BaseNavigationActivity)getActivity()).voidService(transactionDetails.getTransactionID(), transactionDetails.getAmount().toString());
                }
                break;
        }
    }

    private void createReceiptRequestObject(){
        receiptRequest = new ReceiptRequest();
        if(transactionDetails != null){
            receiptRequest.setTransactionID(transactionDetails.getTransactionID());
            receiptRequest.setMerchantReferenceCode(transactionDetails.getMerchantReferenceCode());
            receiptRequest.setAuthCode(transactionDetails.getAuthCode());
            receiptRequest.setTotalPurchaseAmount(transactionDetails.getAmount()+"");
        }
        receiptRequest.setToEmail(toEmail);
        receiptRequest.setFromEmail("no-reply@cybersource.com");
        receiptRequest.setEmailSubject("Your Transaction Receipt");
        receiptRequest.setMerchantDescriptor("Cybersource");
        receiptRequest.setMerchantDescriptorStreet("P.O. Box 8999");
        receiptRequest.setMerchantDescriptorCity("San Francisco");
        receiptRequest.setMerchantDescriptorState("CA");
        receiptRequest.setMerchantDescriptorPostalCode("94128-8999");
        receiptRequest.setMerchantDescriptorCountry("USA");
        receiptRequest.setShippingAmount("0.0");
        receiptRequest.setTaxAmount("0.0");
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Fragment fragment) {
        if (mListener != null) {
            mListener.onFragmentInteraction(fragment);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
