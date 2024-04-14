package com.mv.callalytics;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity {


    GifImageView loadingGIFImage;
    Button loadDataButton, showStatsButton;
    AllCallLogs allCallLogs = new AllCallLogs();
    AllContacts contacts = new AllContacts();


    SharedPreferences mPrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mPrefs = getSharedPreferences("My_Preferences", MODE_PRIVATE);

        Gson gson = new Gson();
        if(!mPrefs.getString("allCallLogs", "").equals("")){                                    // Check if not empty
            allCallLogs = gson.fromJson(mPrefs.getString("allCallLogs", ""), AllCallLogs.class);
        }
        if(!mPrefs.getString("contacts", "").equals("")) {                                    // Check if not empty
            contacts = gson.fromJson(mPrefs.getString("contacts", ""), AllContacts.class);
        }

        loadingGIFImage = findViewById(R.id.loadingGIFImage);
        loadingGIFImage.setVisibility(View.GONE);

        loadDataButton = findViewById(R.id.loadDataButton);
        loadDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d("QWER", getCallDetails());
                loadingGIFImage.setVisibility(View.VISIBLE);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getCallDetails();
                        allCallLogs.sort();
                        getAllContacts();

                        /*for(int i=0; i<allCallLogs.size(); i++){
                            Log.d("QWERTEST", allCallLogs.get(i).toString());
                        }

                        Log.d("QWER", "Total Call Logs present : " + allCallLogs.size());

                        for(int i=0; i<contacts.size(); i++){
                            Log.d("QWERTEST", contacts.get(i).name + " | " + contacts.get(i).phoneNo);
                        }*/


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                                prefsEditor.putString("allCallLogs", gson.toJson(allCallLogs));
                                prefsEditor.putString("contacts", gson.toJson(contacts));
                                prefsEditor.putLong("dataLastLoaded", System.currentTimeMillis());
                                prefsEditor.apply();
                                loadingGIFImage.setVisibility(View.GONE);
                            }
                        });
                    }
                }).start();
            }
        });

        showStatsButton = findViewById(R.id.showStatsButton);
        showStatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, StatsActivity.class);
                startActivity(myIntent);
            }
        });
    }






    private String getCallDetails() {

        int totalCalls = 0;

        StringBuilder sb = new StringBuilder();
        Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null,
                null, null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        sb.append("Call Details :");
        while (managedCursor.moveToNext()) {
            totalCalls++;
            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            Date callDayTime = new Date(Long.parseLong(callDate));
            String callDuration = managedCursor.getString(duration);
            String dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;

                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
            }
            sb.append("\nPhone Number:--- ").append(phNumber).append(" \nCall Type:--- ").append(dir).append(" \nCall Date:--- ").append(callDayTime).append(" \nCall duration in sec :--- ").append(callDuration);
            sb.append("\n----------------------------------");
            allCallLogs.addCallLog(new CallLogEntry(phNumber, dircode, callDayTime.getTime(), Integer.parseInt(callDuration)));
        }
        managedCursor.close();
        Log.d("QWER", "Total call logs = " + totalCalls);
        return sb.toString();

    }



    public void getAllContacts() {

        Cursor cursor = MainActivity.this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            try {
                final int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                final int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                while (cursor.moveToNext()) {
                    String name = cursor.getString(nameIndex);
                    String number = cursor.getString(numberIndex);
                    number = number.replace(" ", "");
                    contacts.add(new Contact(name, number));
                }
            } finally {
                cursor.close();
            }
        }

    }



}