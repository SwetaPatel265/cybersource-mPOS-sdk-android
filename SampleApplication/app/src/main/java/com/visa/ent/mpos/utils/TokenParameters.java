package com.visa.ent.mpos.utils;

/*
 * Copyright © 2016 CyberSource. All rights reserved.
 */

/**
 * Created by CyberSource on 8/24/2016.
 */
public class TokenParameters {
    /** REST oAuth TEST Authorization endpoint address. */
    public final static String AUTHORIZATION_TEST_DOMAIN = "https://authtest.ic3.com/apiauth/v1/oauth/";
    /** REST oAuth PROD Authorization endpoint address. */
    public final static String AUTHORIZATION_PROD_DOMAIN = "https://auth.ic3.com/apiauth/v1/oauth/";
    // TEST Generate Token address
    public final static String TEST_GENERATE_TOKEN_URL = AUTHORIZATION_TEST_DOMAIN + "token";
    // PROD Generate Token address
    public final static String PROD_GENERATE_TOKEN_URL = AUTHORIZATION_PROD_DOMAIN + "token";

    private String URL;
    private String platform;
    private String grantType;
    private String deviceID;
    private String merchantID;
    private String clientID;
    private String username;
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getMerchantID() {
        return merchantID;
    }

    public void setMerchantID(String merchantID) {
        this.merchantID = merchantID;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }




}
