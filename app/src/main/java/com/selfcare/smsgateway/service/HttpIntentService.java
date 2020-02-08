package com.selfcare.smsgateway.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.util.Log;

import com.selfcare.smsgateway.App;
import com.selfcare.smsgateway.ws.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpIntentService extends IntentService {
    private static String TAG = HttpIntentService.class.getSimpleName();
    private App app;

    public HttpIntentService() {
        super(HttpIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            app  = (App)getApplicationContext();
            String message = intent.getStringExtra("message");
            String numberSender = intent.getStringExtra("numberSender");
            Log.i(TAG, "message: " + message);
            Log.i(TAG, "numberSender: " + numberSender);
            Log.i(TAG, "url serveur: " + app.getServerUrl());
            String url = app.getServerUrl();
            JSONObject postDataParams = new JSONObject();
            try {
                postDataParams.put("number", numberSender);
                postDataParams.put("message", message);
                postDataParams.put("codeApplication", "selfcare");
                String response  = RequestHandler.sendPost(url,postDataParams);
                Log.i(TAG, "responseServer: " + response);


                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(numberSender, null, "sms selfcare", null, null);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

