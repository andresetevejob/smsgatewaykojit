package com.selfcare.smsgateway.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.selfcare.smsgateway.App;
import com.selfcare.smsgateway.R;
import com.selfcare.smsgateway.service.BackgroundSendSmsService;

public class PrefActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private App app;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
        app = (App)getApplication();
        PreferenceScreen screen = this.getPreferenceScreen();
        int numPrefs = screen.getPreferenceCount();
        for(int i=0; i < numPrefs;i++)
        {
            updatePrefSummary(screen.getPreference(i));
        }
    }


    private void updatePrefSummary(Preference p)
    {
        Log.i("ccc","ddd");
        if (p == null)
        {
            return;
        }

        String key = p.getKey();

        if (p instanceof PreferenceCategory)
        {
            PreferenceCategory category = (PreferenceCategory)p;
            int numPreferences = category.getPreferenceCount();
            for (int i = 0; i < numPreferences; i++)
            {
                updatePrefSummary(category.getPreference(i));
            }
        }
        else if (p instanceof ListPreference) {
            p.setSummary(((ListPreference)p).getEntry());
        }
        else if (p instanceof EditTextPreference) {

            EditTextPreference textPref = (EditTextPreference)p;
            String text = textPref.getText();
            if (text == null || text.equals(""))
            {
                p.setSummary("(not set)");
            }
            else
            {
                p.setSummary(text);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.i("ccckey",key);
        if (key.equals("server_url"))
        {
            String serverUrl = sharedPreferences.getString("server_url", "");

            // assume http:// scheme if none entered
            if (serverUrl.length() > 0 && !serverUrl.contains("://"))
            {
                sharedPreferences.edit()
                        .putString("server_url", "http://" + serverUrl)
                        .commit();
            }

            //app.log("Server URL changed to: " + app.getDisplayString(app.getServerUrl()));
        }

        else if (key.equals("history_time"))
        {
            Log.i(PrefActivity.class.getName(),"temps update");
            if(app.isRetroActif()){
                Log.i(PrefActivity.class.getName(),"temps update@");

                //Lancer le service de recuperation des messages en tenant compte
                //du parametre de retroactivite temps
                Intent it = new Intent(PrefActivity.this, BackgroundSendSmsService.class);
                startService(it);
            }
        }
        else if (key.equals("enabled"))
        {
            //app.log(app.isEnabled() ? getText(R.string.started) : getText(R.string.stopped));
            //app.enabledChanged();
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected void onResume(){
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

}
