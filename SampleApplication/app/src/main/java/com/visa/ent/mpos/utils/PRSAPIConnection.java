package com.visa.ent.mpos.utils;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Xml;

import com.visainc.mpos.sdk.common.VMposUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.SocketTimeoutException;

import javax.net.ssl.HttpsURLConnection;

/*
 * Copyright © 2016 CyberSource. All rights reserved.
 */

/**
 * Created by CyberSource on 8/24/2016.
 */
public class PRSAPIConnection {
    static TokenDelegate tokenDelegate;
    private final static String POST = "POST";

    public static void connection(TokenParameters tokenParameters, TokenDelegate tokendelegate) {
        tokenDelegate = tokendelegate;
        new TokenGenerationAsyncTask().execute(tokenParameters);
    }
    private static class TokenGenerationAsyncTask extends AsyncTask<TokenParameters, Void, Object> {

        @Override
        protected Object doInBackground(TokenParameters... tokenParameters) {
            TokenParameters tokenParameter = tokenParameters[0];
            String token="";

            try {

                HttpsURLConnection urlConnection = VMposUtils.getHttpsURLConnection(tokenParameter.getURL(), POST, true);
                Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("content-type", "application/x-www-form-urlencoded")
                .appendQueryParameter("platform", tokenParameter.getPlatform())
                .appendQueryParameter("grant_type", tokenParameter.getGrantType())
                .appendQueryParameter("device_id", tokenParameter.getDeviceID())
                .appendQueryParameter("merchant_id", tokenParameter.getMerchantID())
                .appendQueryParameter("client_id", tokenParameter.getClientID())
                .appendQueryParameter("username", tokenParameter.getUsername())
                .appendQueryParameter("password", tokenParameter.getPassword());
                String query = builder.build().getEncodedQuery();
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                //urlConnection.connect();
                int responseCode = urlConnection.getResponseCode();
                android.util.Log.i("TokenManager", "responseCode: " + responseCode);
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String response = VMposUtils.convertStreamToString(urlConnection.getInputStream());
                    android.util.Log.i("TokenManager", "response: " + response);
                    try {
                        JSONObject json = new JSONObject(response);
                        token = json.getString("access_token");
                        android.util.Log.i("TokenManager", "token: " + token);
                    } catch (JSONException e) {
                        // TODO: we should throw here our own exception;
                    }
                }
            }catch (SocketTimeoutException ste) {
                return "";
            }
            catch (IOException e) {
                return "";
            }catch(Exception e){
                return "";
            }
            return token;
        }

        @Override
        protected void onPostExecute(Object token) {
            tokenDelegate.tokenGenerationFinished(token.toString());
        }
    }
}
