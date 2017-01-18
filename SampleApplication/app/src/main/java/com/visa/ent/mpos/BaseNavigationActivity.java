package com.visa.ent.mpos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.widget.TextView;

import com.cybersource.mpos.client.ReceiptRequest;
import com.cybersource.mpos.client.Transaction;
import com.visa.ent.mpos.dialogs.YesNoDialog;
import com.visa.ent.mpos.fragments.HistoryFragment;
import com.visa.ent.mpos.fragments.PayTransactionHeadlessFrangment;
import com.visa.ent.mpos.fragments.RefundTransactionHeadlessFragment;
import com.visa.ent.mpos.fragments.SearchDetailFragment;
import com.visa.ent.mpos.fragments.SearchDetailTransactionHeadlessFragment;
import com.visa.ent.mpos.fragments.SearchFragment;
import com.visa.ent.mpos.fragments.SearchTransactionHeadlessFragment;
import com.visa.ent.mpos.fragments.ShoppingCartFragment;
import com.visa.ent.mpos.fragments.OnFragmentInteractionListener;
import com.visa.ent.mpos.services.TransactionIntentService;
import com.visa.ent.mpos.utils.SearchUtil;
import com.visainc.mpos.sdk.datamodel.VMposTransactionSearchQuery;

import java.math.BigDecimal;
import java.util.Calendar;


/*
 * Copyright © 2016 CyberSource. All rights reserved.
 */

public class BaseNavigationActivity extends BaseActivity
        implements OnFragmentInteractionListener,
        YesNoDialog.OnYesNODialogInteractionListener, NavigationView.OnNavigationItemSelectedListener{

    public static final String TAG_FRAGMENT_CART = "TAG_FRAGMENT_CART";
    public static final String TAG_FRAGMENT_HISTORY = "TAG_FRAGMENT_HISTORY";
    public static final String TAG_FRAGMENT_SEARCH = "TAG_FRAGMENT_SEARCH";
    public static final String TAG_FRAGMENT_HISTORY_DETAIL = "TAG_FRAGMENT_HISTORY_DETAIL";
    private static final long DRAWER_CLOSE_DELAY_MS = 150;
    private static final String NAV_ITEM_ID = "navItemId";
    public static final String TAG_FRAGMENT_SERVICE = "service_fragment_refund";
    public static final String TAG_FRAGMENT_SERVICE_SEARCH = "service_fragment_search";
    public static final String TAG_FRAGMENT_SERVICE_DETAIL_SEARCH = "service_fragment_detail_search";

    private final Handler mDrawerActionHandler = new Handler();
    private DrawerLayout mDrawerLayout;
    private View parentView;
    private TextView footer;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationView mNavigationDrawerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar mToolbar;
    private int mNavItemId;

    /**
     * Used to store the last screen title.
     */
    private CharSequence mTitle;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI(savedInstanceState);
    }

    private void initUI(Bundle savedInstanceState) {
        parentView = findViewById(R.id.main_parent_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationDrawerView = (NavigationView) findViewById(R.id.navigation_view);
        mNavigationDrawerView.setNavigationItemSelectedListener(this);
        footer = (TextView) findViewById(R.id.footer);
        insertYearToFooter();
        mNavItemId = R.id.navigation_item_cart;
        // select the correct nav menu item
        mNavigationDrawerView.getMenu().findItem(mNavItemId).setChecked(true);

        // set up the hamburger icon to open and close the drawer
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        mTitle = getTitle();

        mNavigationDrawerView.setNavigationItemSelectedListener(this);
        onNavigationDrawerItemSelected(R.id.navigation_item_cart);
    }

    public void onNavigationDrawerItemSelected(final int itemId) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = null;
        String tag = null;
        switch (itemId) {
            case R.id.navigation_item_cart:
                tag = TAG_FRAGMENT_CART;
                fragment = fragmentManager.findFragmentByTag(tag);
                if(fragment == null)
                    fragment = ShoppingCartFragment.newInstance(0);
                break;
            case R.id.navigation_item_history:
                tag = TAG_FRAGMENT_SEARCH;
                fragment = fragmentManager.findFragmentByTag(tag);
                if(fragment == null)
                    fragment = SearchFragment.newInstance(1);
                break;
            case R.id.navigation_item_settings:
                Intent intent = new Intent(BaseNavigationActivity.this, SettingsActivity.class);
                startActivity(intent);
                return;
            default:
                tag = TAG_FRAGMENT_CART;
                fragment = fragmentManager.findFragmentByTag(tag);
                if(fragment == null)
                    fragment = ShoppingCartFragment.newInstance(0);
        }

        FragmentTransaction ft =  fragmentManager.beginTransaction();
        ft.replace(R.id.container, fragment, tag);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 0:
                mTitle = getString(R.string.title_shopping_cart);
                break;
            case 1:
                mTitle = getString(R.string.title_history);
                break;
            case 2:
                mTitle = getString(R.string.title_settings);
                break;
        }
        this.restoreActionBar();
    }

    @Override
    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override protected int getMenuResource() {
        return R.menu.base;
    }

    @Override
    public void onFragmentInteraction(Fragment fragment) {
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_base_navigation;
    }

    @Override
    public void onDialogButtonClicked(int requestCode, int resultCode, Intent data) {
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem menuItem) {
        mNavItemId = menuItem.getItemId();
        if(mNavItemId != R.id.navigation_item_settings)
            menuItem.setChecked(true);
        // allow some time after closing the drawer before performing real navigation
        // so the user can see what is happening
        mDrawerLayout.closeDrawer(GravityCompat.START);
        mDrawerActionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onNavigationDrawerItemSelected(menuItem.getItemId());
            }
        }, DRAWER_CLOSE_DELAY_MS);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.action_settings) {
            //Toast.makeText(getActivity(), "Notes", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void insertYearToFooter() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        footer.setText(getString(R.string.copyright) + " " + year + " " + getString(R.string.rights_reserved_visa_inc));
    }

    public void sendEmailService(ReceiptRequest receiptRequest){
        Intent msgIntent = new Intent(this, TransactionIntentService.class);
        msgIntent.putExtra(TransactionIntentService.SERVICE_ACTION_TAG, BaseActivity.EMAIL_RECEIPT_ACTION);
        msgIntent.putExtra(TransactionIntentService.SERVICE_RESULT_RECEIVER, mReceiver);
        SearchUtil.getInstance().setReceiptRequest(receiptRequest);
        this.startService(msgIntent);
    }

    public void voidService(String transactionID, String amount){
        Intent msgIntent = new Intent(this, TransactionIntentService.class);
        msgIntent.putExtra(TransactionIntentService.SERVICE_ACTION_TAG, BaseActivity.VOID_ACTION);
        msgIntent.putExtra(TransactionIntentService.SERVICE_RESULT_RECEIVER, mReceiver);
        msgIntent.putExtra(TransactionIntentService.SERVICE_VOID_TRANSACTIONID, transactionID);
        msgIntent.putExtra(TransactionIntentService.SERVICE_VOID_AMOUNT, amount);
        this.startService(msgIntent);
    }

    public void searchNextService(){
        Intent msgIntent = new Intent(this, TransactionIntentService.class);
        msgIntent.putExtra(TransactionIntentService.SERVICE_ACTION_TAG, BaseActivity.SEARCH_ACTION_NEXT);
        msgIntent.putExtra(TransactionIntentService.SERVICE_RESULT_RECEIVER, mReceiver);
        this.startService(msgIntent);
    }

    public void searchDetailService(String transactionId){
        Intent msgIntent = new Intent(this, TransactionIntentService.class);
        msgIntent.putExtra(TransactionIntentService.SERVICE_ACTION_TAG, BaseActivity.SEARCH_ACTION_DETAIL);
        msgIntent.putExtra(TransactionIntentService.SERVICE_RESULT_RECEIVER, mReceiver);
        msgIntent.putExtra(TransactionIntentService.SERVICE_SEARCH_TRANSACTIONID, transactionId);
        this.startService(msgIntent);
    }

    public void findFragmentForService(String transactionId, String amount){
        FragmentManager fragmentManager = getSupportFragmentManager();
        RefundTransactionHeadlessFragment refundState =
                (RefundTransactionHeadlessFragment)fragmentManager
                        .findFragmentByTag(TAG_FRAGMENT_SERVICE);

        if(refundState == null) {
            registerResultReceiver();
            refundState = new RefundTransactionHeadlessFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(RefundTransactionHeadlessFragment.ARG_RESULT_RECEIVER, mReceiver);
            bundle.putString(RefundTransactionHeadlessFragment.ARG_TRANSACTIONID, transactionId);
            bundle.putString(RefundTransactionHeadlessFragment.ARG_AMOUNT,amount);
            //bundle.putDouble("hello", amount);
            refundState.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .add(refundState, TAG_FRAGMENT_SERVICE).commit();
        }
        else{
            //refundState.setPosition(position);
            refundState.setTransactionId(transactionId);
            refundState.setAmount(amount);
            refundState.startServiceWithReceiver();
        }
    }

    public void findSearchFragmentForService(String searchType){
        FragmentManager fragmentManager = getSupportFragmentManager();
        SearchTransactionHeadlessFragment searchState =
                (SearchTransactionHeadlessFragment)fragmentManager
                        .findFragmentByTag(TAG_FRAGMENT_SERVICE_SEARCH);

        if(searchState == null) {
            registerResultReceiver();
            searchState = new SearchTransactionHeadlessFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(SearchTransactionHeadlessFragment.ARG_RESULT_RECEIVER, mReceiver);
            bundle.putString(SearchTransactionHeadlessFragment.SEARCH_TYPE, searchType);
            searchState.setArguments(bundle);
            //searchState.setQuery(searchQuery);
            fragmentManager.beginTransaction()
                    .add(searchState, TAG_FRAGMENT_SERVICE_SEARCH).commit();
        }
        else{
            searchState.startServiceWithReceiver();
        }
    }

    public void updateSearch(boolean success){
        FragmentManager fragmentManager = getSupportFragmentManager();
        HistoryFragment historyFragment =
                (HistoryFragment)fragmentManager
                        .findFragmentByTag(TAG_FRAGMENT_HISTORY);

        if(historyFragment != null) {
            historyFragment.updateSearchUI(success);
        }
    }

    public void updateSearchNext(boolean success){
        FragmentManager fragmentManager = getSupportFragmentManager();
        HistoryFragment historyFragment =
                (HistoryFragment)fragmentManager
                        .findFragmentByTag(TAG_FRAGMENT_HISTORY);

        if(historyFragment != null) {
            historyFragment.updateSearchUI(success);
        }
    }

    public void updateSearchDetail(boolean success){
        FragmentManager fragmentManager = getSupportFragmentManager();
        SearchDetailFragment searchDetailFragment =
                (SearchDetailFragment)fragmentManager
                        .findFragmentByTag(TAG_FRAGMENT_HISTORY_DETAIL);

        if(searchDetailFragment != null) {
            searchDetailFragment.updateDetailSearchUI(success);
        }

    }
    public void updateSearchDetailVoid(boolean success){
        FragmentManager fragmentManager = getSupportFragmentManager();
        SearchDetailFragment searchDetailFragment =
                (SearchDetailFragment)fragmentManager
                        .findFragmentByTag(TAG_FRAGMENT_HISTORY_DETAIL);

        if(searchDetailFragment != null) {
            searchDetailFragment.updateDetailSearchUIVoid(success);
        }

    }

    public void updateSearchDetailRefund(boolean success){
        FragmentManager fragmentManager = getSupportFragmentManager();
        SearchDetailFragment searchDetailFragment =
                (SearchDetailFragment)fragmentManager
                        .findFragmentByTag(TAG_FRAGMENT_HISTORY_DETAIL);

        if(searchDetailFragment != null) {
            searchDetailFragment.updateDetailSearchUIRefund(success);
        }

    }

}
