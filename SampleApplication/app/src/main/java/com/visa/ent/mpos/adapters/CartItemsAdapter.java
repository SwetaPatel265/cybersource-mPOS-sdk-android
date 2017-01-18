package com.visa.ent.mpos.adapters;

import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.visa.ent.mpos.R;
import com.visa.ent.mpos.datamodel.CheckoutCartItem;
import com.visa.ent.mpos.transaction.MposTransaction;
import com.visa.ent.mpos.utils.MathUtils;
import com.visainc.mpos.sdk.datamodel.transaction.fields.VMposItem;

import java.math.BigDecimal;
import java.util.ArrayList;

/*
 * Copyright © 2016 CyberSource. All rights reserved.
 */

/**
 * Created by CyberSource on 5/4/2015.
 */
public class CartItemsAdapter extends RecyclerView.Adapter<CartItemsAdapter.CartItemsViewHolder>{

    private ArrayList<CheckoutCartItem> cartItemsDataSet;

    public CartItemsAdapter(ArrayList<CheckoutCartItem> dataList) {
        cartItemsDataSet = dataList;
    }

    public CartItemsAdapter() {
        cartItemsDataSet = new ArrayList<>();
    }

    @Override
    public CartItemsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.cart_items_list_item, viewGroup, false);
        return new CartItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartItemsViewHolder itemViewHolder, int position) {
        CheckoutCartItem checkoutCartItem = cartItemsDataSet.get(position);
        itemViewHolder.itemNameTextView.setText(checkoutCartItem.getName());
        itemViewHolder.priceTextView.setText("$" + MathUtils.getTwoDecimalInString(checkoutCartItem.getPrice()));
    }

    @Override
    public int getItemCount() {
        return cartItemsDataSet.size();
    }

    public class CartItemsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected TextView itemNameTextView;
        protected TextView priceTextView;
        protected TextView timestampTextView;
        protected ImageButton removeItemImageButton;

        public CartItemsViewHolder(View itemView) {
            super(itemView);
            itemNameTextView = (TextView) itemView.findViewById(R.id.transaction_id_text_view);
            priceTextView = (TextView) itemView.findViewById(R.id.price_text_view);
            timestampTextView = (TextView) itemView.findViewById(R.id.timestamp_text_view);
            removeItemImageButton = (ImageButton) itemView.findViewById(R.id.remove_cart_item_image_button);
            removeItemImageButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();

            switch (id){
                case R.id.remove_cart_item_image_button:
                    removeCartItem(getPosition());
            }
        }
    }

    public void addOrUpdateHistory(CheckoutCartItem checkoutCartItem) {
        int pos = cartItemsDataSet.indexOf(checkoutCartItem);
        if (pos >= 0) {
            updateCartItem(checkoutCartItem, pos);
        } else {
            addCartItem(checkoutCartItem);
        }
    }

    private void updateCartItem(CheckoutCartItem checkoutCartItem, int position) {
        removeCartItem(position);
        addCartItem(checkoutCartItem);
    }

    public void addCartItem(CheckoutCartItem checkoutCartItem) {
        addPrice(checkoutCartItem.getPrice());
        //cartItemsDataSet.add(0, checkoutCartItem);
        cartItemsDataSet.add(checkoutCartItem);
        MposTransaction.getInstance().addItemToTransactionObject(checkoutCartItem);
        notifyItemInserted(cartItemsDataSet.size() - 1);
    }

    public void removeCartItem(int position) {
        subtractPrice(cartItemsDataSet.get(position).getPrice());
        CheckoutCartItem cartItem = cartItemsDataSet.remove(position);
        VMposItem vMposItem = MposTransaction.getInstance().mapCartItemToTransactionItem(cartItem);
        MposTransaction.getInstance().removeItemFromTransactionObject(position);
        notifyItemRemoved(position);
    }

    private void subtractPrice(Double price){
        MposTransaction.getInstance().setSubtotalAmount(
                MposTransaction.getInstance().getSubtotalAmount() - price);
    }

    private void addPrice(Double price){
        MposTransaction.getInstance().setSubtotalAmount(
                MposTransaction.getInstance().getSubtotalAmount() + price);
    }

}
