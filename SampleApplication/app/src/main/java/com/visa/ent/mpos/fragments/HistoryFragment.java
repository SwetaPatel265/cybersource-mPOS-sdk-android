package com.visa.ent.mpos.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cybersource.mpos.client.Transaction;
import com.visa.ent.mpos.BaseActivity;
import com.visa.ent.mpos.BaseNavigationActivity;
import com.visa.ent.mpos.R;
import com.visa.ent.mpos.adapters.HistoryAdapter;
import com.visa.ent.mpos.datamodel.HistoryItem;
import com.visa.ent.mpos.datamodel.HistoryViewHolder;
import com.visa.ent.mpos.dialogs.YesNoDialog;
import com.visa.ent.mpos.receivers.TransactionResultReceiver;
import com.visa.ent.mpos.transaction.MposTransaction;
import com.visa.ent.mpos.utils.SearchUtil;
import com.visainc.mpos.sdk.common.error.VMposError;
import com.visainc.mpos.sdk.connectors.cybersource.responses.VMposResponseDecision;
import com.visainc.mpos.sdk.datamodel.VMposGateway;
import com.visainc.mpos.sdk.datamodel.VMposGatewayErrorMapping;
import com.visainc.mpos.sdk.datamodel.VMposGatewayResponse;
import com.visainc.mpos.sdk.datamodel.VMposTransactionSearchQuery;
import com.visainc.mpos.sdk.datamodel.transaction.VMposTransactionObject;
import com.visainc.mpos.sdk.transactions.VMposPaymentUpdateType;
import com.visainc.mpos.sdk.transactions.interfaces.VMposPaymentDelegate;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/*
 * Copyright © 2016 CyberSource. All rights reserved.
 */

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, HistoryViewHolder.HistoryViewHolderClicksListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final int YES_NO_CALL = 2;
    public static final String TAG_FRAGMENT_HISTORY_DETAIL = "TAG_FRAGMENT_HISTORY_DETAIL";

    private RecyclerView historyRecycler;
    private LinearLayout emptyLayout;
    private HistoryAdapter historyAdapter;
    private SwipeRefreshLayout swipeLayout;

    private OnFragmentInteractionListener mListener;

    private int mSectionNumber;
    private int size = 0;
    private int selectedItem;
    VMposTransactionSearchQuery searchQuery;
    ArrayList<HistoryItem> historyTransactionList;
    private ProgressDialog progressDialog;


    public static HistoryFragment newInstance(int sectionNumber) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpSwipeLayout(rootView);
        setUpRecyclerView(rootView);
        setHistoryAdapter();
        findFragmentForService();
        return rootView;
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


    private void findFragmentForService(){
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading..");
        //progressDialog.setTitle("Checking Network");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.show();
        ((BaseNavigationActivity)getActivity()).findSearchFragmentForService(SearchTransactionHeadlessFragment.MAIN);
    }

    private void updateEmptyCartView() {
        if(historyTransactionList != null && historyTransactionList.size() == 0) {
            swipeLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        }
        else {
            swipeLayout.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        }
    }
    LinearLayoutManager layoutManager;

    private void setUpRecyclerView(View rootView) {
        emptyLayout = (LinearLayout) rootView.findViewById(R.id.empty_history_view);
        historyRecycler = (RecyclerView) rootView.findViewById(R.id.history_recycler_view);
        historyRecycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        historyRecycler.setLayoutManager(layoutManager);
        RecyclerView.ItemAnimator animator = historyRecycler.getItemAnimator();
        animator.setAddDuration(500);
        historyRecycler.addOnScrollListener(mRecyclerViewOnScrollListener);
    }
    private RecyclerView.OnScrollListener
            mRecyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            HistoryAdapter adapter = (HistoryAdapter)recyclerView.getAdapter();
            int lastPosition = adapter.getLastBoundPosition();
            LinearLayoutManager layoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
            int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
            int loadBufferPosition = 5;
            if(lastVisiblePosition >= adapter.getItemCount() - loadBufferPosition){
                ((BaseNavigationActivity)getActivity()).searchNextService();
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };

    private void setUpSwipeLayout(View rootView) {
        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(R.color.visa_blue,
                R.color.visa_golden);
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
            if (getArguments() != null)
                mSectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            ((BaseNavigationActivity) activity).onSectionAttached(mSectionNumber);
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

    @Override
    public void onRefresh() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                stopRefresh();
            }
        }, 4000);
    }

    private void stopRefresh(){
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (swipeLayout != null) swipeLayout.setRefreshing(false);
                }
            });
        }
    }

    private void startRefresh(){
        swipeLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(true);
            }
        });
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case YES_NO_CALL:
                if(resultCode == Activity.RESULT_OK){
                    Toast.makeText(getActivity(), getActivity().getString(R.string.refunding),
                            Toast.LENGTH_LONG).show();
                }
                break;
        }
    }



    @Override
    public void historyItemClicked(View view, int position) {
        selectedItem = position;
        String tag = TAG_FRAGMENT_HISTORY_DETAIL;
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = SearchDetailFragment.newInstance(selectedItem);
        }
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.container, fragment, tag);
        ft.addToBackStack(null);
        ft.hide(this);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    public void updateSearchUI(boolean success){
        progressDialog.dismiss();
        if(success){
            historyTransactionList = new ArrayList<>();
            List<Transaction> searchList = SearchUtil.getInstance().getSearchResult();
            for(Transaction searchObject : searchList) {

                HistoryItem historyItem = new HistoryItem(searchObject.getTransactionID(), searchObject.getAmount()+"", searchObject.getTransactionDate(), searchObject.getTransactionTime(), false, searchObject.getTransactionType().name());
                historyTransactionList.add(historyItem);
            }

            setHistoryAdapter();
            updateEmptyCartView();
        }
    }

    private void setHistoryAdapter() {
        historyAdapter = new HistoryAdapter(historyTransactionList, this);
        historyRecycler.setAdapter(historyAdapter);
    }

}
