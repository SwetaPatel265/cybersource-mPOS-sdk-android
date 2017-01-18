package com.visa.ent.mpos;

import android.content.Intent;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.visa.ent.mpos.fragments.SettingsFragment;


/*
 * Copyright © 2016 CyberSource. All rights reserved.
 */

public class SettingsActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(R.id.container_preferences, new SettingsFragment())
                .commit();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_settings;
    }

    @Override protected int getMenuResource() {
        restoreActionBar();
        return R.menu.menu_settings;
    }

    @Override
    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.action_settings);
        actionBar.setDisplayHomeAsUpEnabled(false);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
            case R.id.action_settings_done:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


}
