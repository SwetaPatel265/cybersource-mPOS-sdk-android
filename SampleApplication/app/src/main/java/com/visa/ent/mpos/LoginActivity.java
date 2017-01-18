package com.visa.ent.mpos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

/*
 * Copyright © 2016 CyberSource. All rights reserved.
 */

/**
 * Created by CyberSource on 4/28/2015.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button loginButton;
    private View mHeader;
    private EditText merchantIdEditText;
    private EditText passwordEditText;
    private LinearLayout forgotLayout;
    private LinearLayout copyrightLayout;
    private ImageView logoImageView;
    private View backGroundView;
    private boolean splashOnly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUI();
    }

    private void setupUI(){
        setContentView(R.layout.activity_login);
        loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(this);
        mHeader = findViewById(R.id.login_button);
        logoImageView = (ImageView) findViewById(R.id.logo_image_view);
        merchantIdEditText = (EditText) findViewById(R.id.merchant_id_text_view);
        passwordEditText = (EditText) findViewById(R.id.password_text_view);
        forgotLayout = (LinearLayout) findViewById(R.id.forgot_linear_layout);
        copyrightLayout = (LinearLayout) findViewById(R.id.copyright_linear_layout);
        backGroundView = findViewById(R.id.login_background_view);

        setupLogoAnimation();
        splashOnly = true;
    }

    private void setupLogoAnimation() {
        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
        animAlpha.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(splashOnly)
                    moveToNextActivity();
                else
                    makeUIViewsVisible();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        logoImageView.startAnimation(animAlpha);
        logoImageView.setVisibility(View.VISIBLE);
    }

    private void makeUIViewsVisible(){
        merchantIdEditText.setVisibility(View.VISIBLE);
        passwordEditText.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.VISIBLE);
        forgotLayout.setVisibility(View.VISIBLE);
        copyrightLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.login_button:
                moveToNextActivity();
                break;
        }
    }

    private void moveToNextActivity() {
        Intent intent = new Intent(this, BaseNavigationActivity.class);
        if(splashOnly)
            moveLikeSplash(intent);
        else
            moveLikeLogin(intent);
        finish();
    }

    private void moveLikeLogin(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.anim_translate_from_right,
                R.anim.anim_translate_to_left);
    }

    private void moveLikeSplash(Intent intent) {
        String transitionName = getString(R.string.transition_expand_header_to_swipe_background);
        Pair<View, String> p1 = Pair.create(backGroundView, transitionName);
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1);
        ActivityCompat.startActivity(this, intent, optionsCompat.toBundle());
    }
}
