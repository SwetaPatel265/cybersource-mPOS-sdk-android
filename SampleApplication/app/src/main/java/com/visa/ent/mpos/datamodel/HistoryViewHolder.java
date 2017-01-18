package com.visa.ent.mpos.datamodel;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.visa.ent.mpos.R;

import org.w3c.dom.Text;

/*
 * Copyright © 2016 CyberSource. All rights reserved.
 */

/**
 * Created by CyberSource on 5/27/2015.
 */
public class HistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    protected TextView transactionIdView;
    protected TextView priceTextView;
    protected TextView timestampTextView;
    protected TextView refundedTextView;
    protected TextView statusTextView;

    private HistoryViewHolderClicksListener historyClicksListener;

    public HistoryViewHolder(View itemView, HistoryViewHolderClicksListener historyClicksListener) {
        super(itemView);
        this.historyClicksListener = historyClicksListener;
        transactionIdView = (TextView) itemView.findViewById(R.id.transaction_id_text_view);
        priceTextView = (TextView) itemView.findViewById(R.id.price_text_view);
        timestampTextView = (TextView) itemView.findViewById(R.id.timestamp_text_view);
        refundedTextView = (TextView) itemView.findViewById(R.id.refunded_text_view);
        statusTextView = (TextView) itemView.findViewById(R.id.status_text_view);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        historyClicksListener.historyItemClicked(view, getPosition());
    }

    public TextView getTransactionIdView() {
        return transactionIdView;
    }

    public void setTransactionIdView(TextView transactionIdView) {
        this.transactionIdView = transactionIdView;
    }

    public TextView getPriceTextView() {
        return priceTextView;
    }

    public void setPriceTextView(TextView priceTextView) {
        this.priceTextView = priceTextView;
    }

    public TextView getTimestampTextView() {
        return timestampTextView;
    }

    public void setTimestampTextView(TextView timestampTextView) {
        this.timestampTextView = timestampTextView;
    }

    public TextView getStatusTextView() {
        return statusTextView;
    }

    public void setStatusTextView(TextView statusTextView) {
        this.statusTextView = statusTextView;
    }

    public static interface HistoryViewHolderClicksListener {
        public void historyItemClicked(View view, int position);
    }

    public TextView getRefundedTextView() {
        return refundedTextView;
    }

    public void setRefundedTextView(TextView refundedTextView) {
        this.refundedTextView = refundedTextView;
    }

}
