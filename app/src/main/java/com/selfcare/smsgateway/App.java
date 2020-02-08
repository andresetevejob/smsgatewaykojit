package com.selfcare.smsgateway;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class App extends Application {
    private SharedPreferences settings;


    public static final String SETTINGS_CHANGED_INTENT = "com.selfcare.smsgateway.SETTINGS_CHANGED";
    public static final String HISTORY_CHANGED_INTENT = "com.selfcare.smsgateway.HISTORY_CHANGED_INTENT";


    @Override
    public void onCreate() {
        super.onCreate();
        settings = PreferenceManager.getDefaultSharedPreferences(this);

    }

    public void configuredChanged()
    {
        if (isConfigured())
        {
            sendBroadcast(new Intent(App.SETTINGS_CHANGED_INTENT));
        }
    }

    public boolean isConfigured()
    {
        return getServerUrl().length() > 0;
    }

    public boolean isEnabled()
    {
        return tryGetBooleanSetting("enabled", false);
    }

    public boolean isRetroActif(){
        return settings.getBoolean("history_sms", false);
    }

    public String getServerUrl() {
        return settings.getString("server_url", "");
    }

    public boolean getOldMessage()
    {
        return tryGetBooleanSetting("fetch_old_message", false);
    }

    public long getHistoryTime()
    {
        return tryGetLongSetting("history_time",20);
    }

    public String tryGetStringSetting(String name, String defaultValue)
    {
        return settings.getString(name, defaultValue);
    }

    public int tryGetIntegerSetting(String name, int defaultValue)
    {
        try
        {
            return settings.getInt(name, defaultValue);
        }
        catch (ClassCastException ex)
        {
            return Integer.parseInt(settings.getString(name, "" + defaultValue));
        }
    }

    public long tryGetLongSetting(String name, int defaultValue)
    {
        try
        {
            return settings.getLong(name, defaultValue);
        }
        catch (ClassCastException ex)
        {
            return Integer.parseInt(settings.getString(name, "" + defaultValue));
        }
    }

    public boolean tryGetBooleanSetting(String name, boolean defaultValue)
    {
        try
        {
            return settings.getBoolean(name, defaultValue);
        }
        catch (ClassCastException ex)
        {
            return defaultValue;
        }
    }

    public synchronized void saveStringSetting(String key, String value)
    {
        settings.edit().putString(key, value).commit();
    }

    public synchronized void saveBooleanSetting(String key, boolean value)
    {
        settings.edit().putBoolean(key, value).commit();
    }


    public void debug(String msg) {
        //Log.d(LOG_NAME, msg);
    }


}

