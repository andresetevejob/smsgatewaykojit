package com.selfcare.smsgateway.service;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.selfcare.smsgateway.App;
import com.selfcare.smsgateway.ws.RequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BackgroundSendSmsService extends IntentService {

    private static final Uri SMS_URI_INBOX = Uri.parse("content://sms/inbox");
    private static String TAG = BackgroundSendSmsService.class.getName();
    private App app;


    public BackgroundSendSmsService() {
        super(BackgroundSendSmsService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            app  = (App)getApplicationContext();
            long historyTime = app.tryGetLongSetting("history_time",20);
            List<String> messages = retrieveMessages(this.getContentResolver(),historyTime);
            Log.i(BackgroundSendSmsService.class.getName(),"temps messages@ "+messages.size());
            Log.i(BackgroundSendSmsService.class.getName(),"temps history@ "+historyTime);
            JSONArray jsonArray = buildJsonDataFromArrayList(messages);
            String url = app.getServerUrl();
            JSONObject postDataParams = new JSONObject();
            try {
                postDataParams.put("messages", jsonArray);
                postDataParams.put("codeApplication", "selfcare");
                String response  = RequestHandler.sendPost(url,postDataParams);
                Log.i(TAG, "responseServer: " + response);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONArray buildJsonDataFromArrayList(List<String> listData) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < listData.size(); i++){
            JSONObject myJsonObject = new JSONObject();
            myJsonObject.put("message "+i,listData.get(i));
            jsonArray.put(i,myJsonObject);
        }
        return jsonArray;
    }

    private List<String> retrieveMessages(ContentResolver contentResolver,long historyTime)
    {
        long minTime = System.currentTimeMillis() - (historyTime*1000);
        final List<String> messages = new ArrayList<>();
        final Cursor cursor = contentResolver.query(SMS_URI_INBOX, new String[] {
                "_id", "date", "body", "address"
        }, " date <= ?", new String[]{Long.toString(minTime)}, null);

        if (cursor == null)
        {
            Log.e("retrieveMessages", "Cannot retrieve the messages");
            return null;
        }

        if (cursor.moveToFirst() == true)
        {
            do
            {
                final String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                final String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                final String body = cursor.getString(cursor.getColumnIndexOrThrow("body")).concat(date);
                final String id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));

                messages.add(address + " - " + body);

                Log.i("retrieveContacts", "The message with from + '" + address + "' with the body '" + body + "' has been retrieved");
            }
            while (cursor.moveToNext() == true);
        }

        if (cursor.isClosed() == false)
        {
            cursor.close();
        }

        return messages;
    }
}
