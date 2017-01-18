package com.visa.ent.mpos.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cybersource.mpos.client.TransactionSearchQuery;
import com.visa.ent.mpos.BaseNavigationActivity;
import com.visa.ent.mpos.R;
import com.visa.ent.mpos.utils.SearchUtil;
import com.visa.ent.mpos.utils.Utils;
import com.visainc.mpos.sdk.datamodel.VMposGateway;
import com.visainc.mpos.sdk.datamodel.VMposTransactionSearchQuery;
import com.visainc.mpos.sdk.datamodel.VMposTransactionSearchQueryType;

/*
 * Copyright © 2016 CyberSource. All rights reserved.
 */

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG_FRAGMENT_HISTORY = "TAG_FRAGMENT_HISTORY";

    private EditText last4;
    private EditText first4;
    private EditText lastName;
    private EditText merchantReferenceNo;
    private EditText deviceId;
    private EditText dateFrom;
    private EditText dateTo;
    private Button searchButton;
    TransactionSearchQuery searchQuery;
    private int mSectionNumber;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sectionNumber
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(int sectionNumber) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_search, container, false);
        setupUI(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            mSectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            ((BaseNavigationActivity) mListener).onSectionAttached(mSectionNumber);
        }
    }

    private void setupUI(View view){
        last4 = (EditText)view.findViewById(R.id.last4Value);
        first4 = (EditText)view.findViewById(R.id.first4Value);
        lastName = (EditText)view.findViewById(R.id.lastNameValue);
        merchantReferenceNo = (EditText)view.findViewById(R.id.merchantReferenceNoValue);
        deviceId = (EditText)view.findViewById(R.id.deviceIdValue);
        dateFrom = (EditText)view.findViewById(R.id.dateFromValue);
        dateTo = (EditText)view.findViewById(R.id.dateToValue);
        searchButton = (Button)view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
   /*
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/

    @Override
    public void onClick(View view){
        int id = view.getId();
        switch(id){
            case R.id.searchButton:
                getSearchParameters();
        }
    }

    private void getSearchParameters(){
        searchQuery = new TransactionSearchQuery();
        if(first4.getText().length() > 0 && last4.getText().length() > 0){
            searchQuery.setFilter(TransactionSearchQuery.TransactionSearchQueryFilter.AccountPrefixAndSuffix);
            searchQuery.setAccountPrefix(first4.getText().toString());
            searchQuery.setAccountSuffix(last4.getText().toString());
        }
        else if(last4.getText().length() > 0){
            searchQuery.setFilter(TransactionSearchQuery.TransactionSearchQueryFilter.AccountSuffix);
            searchQuery.setAccountSuffix(last4.getText().toString());
        }else if(first4.getText().length() > 0){
            searchQuery.setFilter(TransactionSearchQuery.TransactionSearchQueryFilter.AccountPrefix);
            searchQuery.setAccountPrefix(first4.getText().toString());
        }else if(merchantReferenceNo.getText().length() > 0){
            searchQuery.setFilter(TransactionSearchQuery.TransactionSearchQueryFilter.MerchantReferenceCode);
            searchQuery.setMerchantReferenceCode(merchantReferenceNo.getText().toString());
        }else if(lastName.getText().length() > 0){
            searchQuery.setFilter(TransactionSearchQuery.TransactionSearchQueryFilter.LastName);
            searchQuery.setLastName(lastName.getText().toString());
        }else if(deviceId.getText().length() > 0){
            searchQuery.setFilter(TransactionSearchQuery.TransactionSearchQueryFilter.DeviceId);
            searchQuery.setDeviceId(deviceId.getText().toString());
        }
        long dateFromValue= 0, dateToValue=0;
        if(dateFrom.getText().length() > 0){
            dateFromValue = Utils.stringDateToMillis(dateFrom.getText().toString()+" 00:01:00");
            if(dateFromValue > 0){
                searchQuery.setDateFrom(dateFromValue);
            }
        }
        if(dateTo.getText().length() > 0){
            dateToValue = Utils.stringDateToMillis(dateTo.getText().toString()+" 23:59:00");
            if(dateToValue > 0){
                searchQuery.setDateTo(dateToValue);
            }
        }
        if(dateToValue == -1 || dateFromValue == -1 ){
            Toast.makeText(getActivity(),"Invalid Field : Date To / Date From. Correct Format is mm-dd-yyyy",Toast.LENGTH_SHORT).show();
        }else if(searchQuery.getFilter()== null && searchQuery.getDateFrom()<=0 && searchQuery.getDateTo() <=0){
            Toast.makeText(getActivity(),"Please enter valid search criteria",Toast.LENGTH_SHORT).show();
        }else {
            SearchUtil.getInstance().setSearchQuery(searchQuery);

            String tag = TAG_FRAGMENT_HISTORY;
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentByTag(tag);
            if (fragment == null) {
                fragment = HistoryFragment.newInstance(1);
            }
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.container, fragment, tag);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.addToBackStack(null);
            ft.commit();
        }

    }

}
