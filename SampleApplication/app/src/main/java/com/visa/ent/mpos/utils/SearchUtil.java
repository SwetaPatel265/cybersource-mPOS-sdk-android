package com.visa.ent.mpos.utils;

import com.cybersource.mpos.client.ReceiptRequest;
import com.cybersource.mpos.client.Transaction;
import com.cybersource.mpos.client.TransactionSearchQuery;
import com.cybersource.mpos.client.TransactionSearchResult;
import com.visainc.mpos.sdk.datamodel.VMposTransactionSearchQuery;
import com.visainc.mpos.sdk.datamodel.transaction.VMposTransactionObject;

import java.util.List;

/*
 * Copyright © 2016 CyberSource. All rights reserved.
 */

/**
 * Created by CyberSource on 7/20/2016.
 */
public class SearchUtil {

    private TransactionSearchQuery searchQuery;
    private TransactionSearchQuery searchQueryDetail;
    List<Transaction> searchResponse;
    Transaction searchResponseDetail;
    private ReceiptRequest receiptRequest;
    private TransactionSearchResult transactionSearchResult;
    private static SearchUtil searchUtilInstance = null;
    public static SearchUtil getInstance(){
        if(searchUtilInstance == null){
            searchUtilInstance = new SearchUtil();
        }
        return searchUtilInstance;
    }
    private SearchUtil(){

    }

    public void setSearchQuery(TransactionSearchQuery searchQuery){
        this.searchQuery = searchQuery;
    }

    public TransactionSearchQuery getSearchQuery(){
        return searchQuery;
    }


    public TransactionSearchQuery getSearchQueryDetail() {
        return searchQueryDetail;
    }

    public void setSearchQueryDetail(TransactionSearchQuery searchQueryDetail) {
        this.searchQueryDetail = searchQueryDetail;
    }

    public void setSearchResult(List<Transaction> response){
        this.searchResponse = response;
    }

    public List<Transaction> getSearchResult(){
        return this.searchResponse;
    }


    public Transaction getSearchResponseDetail() {
        return searchResponseDetail;
    }

    public void setSearchResponseDetail(Transaction searchResponseDetail) {
        this.searchResponseDetail = searchResponseDetail;
    }
    public ReceiptRequest getReceiptRequest() {
        return receiptRequest;
    }

    public void setReceiptRequest(ReceiptRequest receiptRequest) {
        this.receiptRequest = receiptRequest;
    }

    public void addNextSearchResult(List<Transaction> response){
        searchResponse.addAll(response);
    }

    public TransactionSearchResult getTransactionSearchResult() {
        return transactionSearchResult;
    }

    public void setTransactionSearchResult(TransactionSearchResult transactionSearchResult) {
        this.transactionSearchResult = transactionSearchResult;
    }
}
