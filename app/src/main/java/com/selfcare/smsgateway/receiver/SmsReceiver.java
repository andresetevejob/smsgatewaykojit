package com.selfcare.smsgateway.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import com.selfcare.smsgateway.App;
import com.selfcare.smsgateway.service.HttpIntentService;

public class SmsReceiver extends BroadcastReceiver {

    private static final String TAG = SmsReceiver.class.getName();
    private App app;
    @Override
    public void onReceive(Context context, Intent intent) {
        app = (App) context.getApplicationContext();

        if (!app.isEnabled())
        {

            return;
        }
        if(!app.isConfigured()){
            return;
        }
        forwardMessageToServer(context,intent);
    }

    public void forwardMessageToServer(Context context,Intent intent){
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (Object aPdusObj : pdusObj) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);
                    String message = currentMessage.getDisplayMessageBody();
                    String numberSender = currentMessage.getDisplayOriginatingAddress();
                    Intent httpIntent = new Intent(context, HttpIntentService.class);
                    Log.i(TAG, "messagebroadcast: " + message);
                    Log.i(TAG, "messagebroadcastnum: " + numberSender);
                    httpIntent.putExtra("message", message);
                    httpIntent.putExtra("numberSender", numberSender);
                    context.startService(httpIntent);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }
}
