package com.visa.ent.mpos.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.visa.ent.mpos.datamodel.HistoryItem;
import com.visa.ent.mpos.R;
import com.visa.ent.mpos.datamodel.HistoryViewHolder;
import com.visa.ent.mpos.dialogs.EditTextDialog;
import com.visa.ent.mpos.fragments.HistoryFragment;


import java.util.ArrayList;

/*
 * Copyright © 2016 CyberSource. All rights reserved.
 */

/**
 * Created by CyberSource on 4/24/2015.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryViewHolder> {

    private ArrayList<HistoryItem> historyDataSet;
    private HistoryFragment historyFragment;
    private int lastBoundPosition;

    public HistoryAdapter(ArrayList<HistoryItem> dataList, HistoryFragment historyFragment) {
        this.historyDataSet = dataList;
        this.historyFragment = historyFragment;
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.history_list_item, viewGroup, false);
        return new HistoryViewHolder(view, historyFragment);
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder itemViewHolder, int position) {
        HistoryItem historyItem = historyDataSet.get(position);
        itemViewHolder.getTransactionIdView().setText(historyItem.getTransactionId());
        itemViewHolder.getPriceTextView().setText("$" + historyItem.getPrice());
        itemViewHolder.getTimestampTextView().setText(historyItem.getDate() + " " + historyItem.getTime());
        itemViewHolder.getStatusTextView().setText(historyItem.getStatus());
        itemViewHolder.getRefundedTextView().setVisibility((historyItem.isRefunded()) ? View.VISIBLE : View.INVISIBLE);
        lastBoundPosition = position;
    }

    @Override
    public int getItemCount() {
        if(historyDataSet != null) {
            return historyDataSet.size();
        }
        return 0;
    }
    public int getLastBoundPosition() {
        return lastBoundPosition;
    }

    public void addOrUpdateHistory(HistoryItem historyItem) {
        int pos = historyDataSet.indexOf(historyItem);
        if (pos >= 0) {
            updateHistory(historyItem, pos);
        } else {
            addHistory(historyItem);
        }
    }

    public void setRefundedTrue(int position){
        historyDataSet.get(position).setRefunded(true);
        notifyItemChanged(position);
    }

    private void updateHistory(HistoryItem historyItem, int position) {
        historyDataSet.remove(position);
        notifyItemRemoved(position);
        addHistory(historyItem);
    }

    private void addHistory(HistoryItem historyItem) {
        historyDataSet.add(historyItem);
        notifyItemInserted(0);
    }
}
