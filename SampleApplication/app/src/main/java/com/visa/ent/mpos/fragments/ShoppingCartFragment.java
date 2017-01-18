package com.visa.ent.mpos.fragments;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.visa.ent.mpos.BaseNavigationActivity;
import com.visa.ent.mpos.R;
import com.visa.ent.mpos.OrderDetailsActivity;
import com.visa.ent.mpos.adapters.CartItemsAdapter;
import com.visa.ent.mpos.datamodel.CheckoutCartItem;
import com.visa.ent.mpos.dialogs.EditTextDialog;
import com.visa.ent.mpos.dialogs.YesNoDialog;
import com.visa.ent.mpos.helper.RedcyclerViewDividerItemDecoration;
import com.visa.ent.mpos.transaction.MposTransaction;
import com.visa.ent.mpos.utils.GUIUtils;
import com.visa.ent.mpos.utils.MathUtils;
import com.visainc.mpos.sdk.common.error.VMposError;
import com.visainc.mpos.sdk.datamodel.transaction.VMposTransactionObject;
import com.visainc.mpos.sdk.devices.VMposDeviceTransaction;
import com.visainc.mpos.sdk.devices.interfaces.VMposDeviceManagerDelegate;

import java.math.BigDecimal;
import java.util.ArrayList;

/*
 * Copyright © 2016 CyberSource. All rights reserved.
 */

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ShoppingCartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShoppingCartFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SECTION_NUMBER = "section_number";
    //private static final String ARG_TIP_ENABLED = "tip_enabled";

    private static final int YES_NO_CALL = 2;
    private static final int EDIT_TEXT_CALL = 3;
    public static final String EDIT_TEXT_CALL_PARAM = "tip_edit_text_value";
    public static final String VISA_MPOS = "visa.mpos";

    // TODO: Rename and change types of parameters
    private int mSectionNumber;

    private RecyclerView cartItemsRecycler;
    private CartItemsAdapter cartItemsAdapter;
    private FloatingActionButton checkoutFAB;
    private FloatingActionButton addToCartFAB;
    private TextInputLayout itemNameEditText;
    private TextInputLayout itemPriceEditText;
    private View mHeader;
    private RelativeLayout mainLayout;
    private LinearLayout emptyLayout;
    private ImageView addNewItemImage;

    private OnFragmentInteractionListener mListener;

    private SharedPreferences sharedpreferences;
    private View revealView;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sectionNumber section number of the attached fragment for Navigation Drawer.
     * @return A new instance of fragment ShoppingCartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShoppingCartFragment newInstance(int sectionNumber) {
        ShoppingCartFragment fragment = new ShoppingCartFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ShoppingCartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        sharedpreferences = getActivity().getSharedPreferences(VISA_MPOS, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shopping_cart, container, false);
        setupUI(view);
        setUpRecyclerView(view);
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

    private void setupUI(View view) {
        mainLayout = (RelativeLayout) view.findViewById(R.id.mainLayout);
        itemNameEditText = (TextInputLayout) view.findViewById(R.id.item_name_edit_text);
        itemPriceEditText = (TextInputLayout) view.findViewById(R.id.item_price_edit_text);
        itemNameEditText.getEditText().setSingleLine(true);
        itemPriceEditText.getEditText().setSingleLine(true);
        checkoutFAB = (FloatingActionButton) view.findViewById(R.id.view_fab_checkout_button);
        checkoutFAB.setOnClickListener(this);
        final Animation animAlpha = AnimationUtils.loadAnimation(getActivity(),
                android.support.v7.mediarouter.R.anim.abc_slide_in_bottom);
        checkoutFAB.startAnimation(animAlpha);
        addToCartFAB = (FloatingActionButton) view.findViewById(R.id.view_fab_add_button);
        addToCartFAB.setOnClickListener(this);
        revealView = view.findViewById(R.id.reveal_view);
        mHeader = (LinearLayout) view.findViewById(R.id.activity_transition_header);
        emptyLayout = (LinearLayout) view.findViewById(R.id.empty_view);
        addNewItemImage = (ImageView) view.findViewById(R.id.add_new_item_image);
        addNewItemImage.setOnClickListener(this);
    }

    /**
     * setup the recyclerview for the cart items
     * @param rootView view inflated to insert the layout elements in
     */
    private void setUpRecyclerView(View rootView) {
        cartItemsRecycler = (RecyclerView) rootView.findViewById(R.id.item_cards_recycler_view);
        cartItemsRecycler.setHasFixedSize(true);
        // set the line divider decoration for the recycler view
        cartItemsRecycler.addItemDecoration(new RedcyclerViewDividerItemDecoration
                (getActivity().getApplicationContext()
        ));
        // set the cart items list adapter
        cartItemsAdapter = new CartItemsAdapter(MposTransaction.getInstance().getCartItemsList());
        cartItemsRecycler.setAdapter(cartItemsAdapter);
        updateEmptyCartView();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        cartItemsRecycler.setLayoutManager(layoutManager);
        RecyclerView.ItemAnimator animator = cartItemsRecycler.getItemAnimator();
        animator.setAddDuration(500);
    }

    private void updateEmptyCartView() {
        if(MposTransaction.getInstance().getCartItemsList().size() == 0) {
            cartItemsRecycler.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        }
        else {
            cartItemsRecycler.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        }
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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.view_fab_checkout_button:
                if(MposTransaction.getInstance().getTransactionObject().getItems().size() == 0) {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.no_items_in_cart),
                            Toast.LENGTH_LONG).show();
                    checkoutFAB.startAnimation(AnimationUtils.
                            loadAnimation(getActivity(), R.anim.shake_error));
                }
                else {
                    mListener.onFragmentInteraction(this);
                    if (MposTransaction.getInstance().isIsTipEnabled())
                        onCheckoutDialog();
                    else
                        onCheckOut();
                }
                break;
            case R.id.view_fab_add_button:
                addItemToList();
                break;
            case R.id.add_new_item_image:
                requestFocusIn();
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(itemNameEditText != null)
            itemNameEditText.setVisibility(View.VISIBLE);
        if(itemPriceEditText != null)
            itemPriceEditText.setVisibility(View.VISIBLE);
    }

//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onCheckOut(){
        itemNameEditText.setVisibility(View.INVISIBLE);
        itemPriceEditText.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(getActivity(), OrderDetailsActivity.class);
        String transitionName = getString(R.string.transition_expand_header_to_swipe_background);
        String transitionNameFabMini = getString(R.string.transition_expand_header_to_swipe_background);
        // To make multiple view transition
        Pair<View, String> p1 = Pair.create(mHeader, transitionName);
        Pair<View, String> p2 = Pair.create((View)addToCartFAB, transitionNameFabMini);
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), p1);

        ActivityCompat.startActivity(getActivity(), intent, optionsCompat.toBundle());

        //Lollipop's way of doing the same above thing without app compat
        /*ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(
                getActivity(), Pair.create(mHeader, "expand_background"));*/
    }

    private void addItemToList(){
        requestFocusOut();
        String itemName = itemNameEditText.getEditText().getText().toString();
        if(itemName == null || itemName.isEmpty())
            itemName = getString(R.string.goods_and_services);
        String itemPrice = itemPriceEditText.getEditText().getText().toString();
        if(itemPrice == null || itemPrice.isEmpty())
            return;

        itemNameEditText.getEditText().setText("");
        itemPriceEditText.getEditText().setText("");

        double price = Double.valueOf(itemPrice);
        CheckoutCartItem checkoutCartItem = new CheckoutCartItem(itemName, price);
        cartItemsAdapter.addCartItem(checkoutCartItem);
        updateEmptyCartView();
    }

    private void requestFocusOut(){
        mainLayout.requestFocus();
        // Check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void requestFocusIn(){
        itemNameEditText.requestFocus();
        View view = getActivity().getCurrentFocus();
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInputFromWindow(view.getWindowToken(), InputMethodManager.SHOW_FORCED, 0);
    }

    private void onCheckoutDialog() {
        DialogFragment dialog = new EditTextDialog();
        dialog.setTargetFragment(this, EDIT_TEXT_CALL);
        dialog.show(getFragmentManager(), "tag");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case YES_NO_CALL:
                if(resultCode == Activity.RESULT_OK){
                    Toast.makeText(getActivity(), "Ok Checking out", Toast.LENGTH_LONG).show();
                }
                else if(resultCode == Activity.RESULT_CANCELED){
                    Toast.makeText(getActivity(), getActivity().getString(R.string.staying),
                            Toast.LENGTH_LONG).show();
                }
                break;
            case EDIT_TEXT_CALL:
                if(resultCode == Activity.RESULT_OK){
                    if(data != null) {
                        String tipAmountString = data.getStringExtra(EDIT_TEXT_CALL_PARAM);
                        double tipAmount = 0.00;
                        if(tipAmountString != null && !tipAmountString.isEmpty())
                            tipAmount = MathUtils.roundToTwoDecimal(Double.valueOf(tipAmountString));

                        MposTransaction.getInstance().setTipAmount(tipAmount);
                        MposTransaction.getInstance().getTransactionObject()
                                .getPurchaseDetails().setTipAmount(BigDecimal.valueOf(tipAmount));
                        onCheckOut();
                    }
                }
                else if(resultCode == Activity.RESULT_CANCELED){
                    Toast.makeText(getActivity(), getActivity().getString(R.string.staying), Toast.LENGTH_LONG).show();
                }
        }
    }

}
