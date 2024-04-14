package com.mv.callalytics;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

public class StatsActivity extends AppCompatActivity {


    GifImageView loadingGIFImage;
    TextView statsTextView;

    AllCallLogs allCallLogs = new AllCallLogs();
    AllContacts contacts = new AllContacts();

    SharedPreferences mPrefs;

    String statsString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);


        mPrefs = getSharedPreferences("My_Preferences", MODE_PRIVATE);

        Gson gson = new Gson();
        if(!mPrefs.getString("allCallLogs", "").equals("")){                                    // Check if not empty
            allCallLogs = gson.fromJson(mPrefs.getString("allCallLogs", ""), AllCallLogs.class);
        }
        if(!mPrefs.getString("contacts", "").equals("")) {                                    // Check if not empty
            contacts = gson.fromJson(mPrefs.getString("contacts", ""), AllContacts.class);
        }



        loadingGIFImage = findViewById(R.id.loadingGIFImage);
        loadingGIFImage.setVisibility(View.VISIBLE);

        statsTextView = findViewById(R.id.statsTextView);
        statsTextView.setVisibility(View.GONE);

        STATS_totalCalls();
        STATS_totalContacts();
        STATS_totalCallDuration();
        STATS_averageCallDuration();
        STATS_mostCalls();
        STATS_mostCallsOfType(CallLog.Calls.INCOMING_TYPE);
        STATS_mostCallsOfType(CallLog.Calls.OUTGOING_TYPE);
        STATS_mostCallsOfType(CallLog.Calls.MISSED_TYPE);
        STATS_mostCallsOfType(CallLog.Calls.BLOCKED_TYPE);
        STATS_mostCallsOfType(CallLog.Calls.REJECTED_TYPE);
        STATS_mostCallsOfType(CallLog.Calls.VOICEMAIL_TYPE);
        STATS_mostCallsOfType(CallLog.Calls.ANSWERED_EXTERNALLY_TYPE);
        STATS_mostCallsOfDuration();
        STATS_mostCallsOfDuration(5);
        STATS_maxCallDurationInTotal();
        STATS_monthWithMostCalls();

        statsTextView.setText(statsString);
        statsTextView.setVisibility(View.VISIBLE);
        loadingGIFImage.setVisibility(View.GONE);
    }


    public void STATS_totalCalls(){
        int totalCalls = allCallLogs.size();
        Log.d("QWER_STATS", "Total Calls = " + totalCalls);
        statsString += "\nTotal Calls = " + totalCalls;
    }

    public void STATS_totalContacts(){
        int totalContacts = contacts.size();
        Log.d("QWER_STATS", "Total Contacts = " + totalContacts);
        statsString += "\nTotal Contacts = " + totalContacts;
    }

    public void STATS_totalCallDuration(){
        int totalDuration = 0;
        for (CallLogEntry entry : allCallLogs.allCallLogs) {
            totalDuration += entry.duration;
        }
        Log.d("QWER_STATS", "Total Call duration in seconds = " + totalDuration);
        statsString += "\nTotal Call duration in seconds = " + totalDuration;
    }

    public void STATS_averageCallDuration(){
        int totalDuration = 0;
        for (CallLogEntry entry : allCallLogs.allCallLogs) {
            totalDuration += entry.duration;
        }
        float averageDuration = totalDuration*1.0f/allCallLogs.size();
        Log.d("QWER_STATS", "Total Call duration in seconds = " + averageDuration);
        statsString += "\nTotal Call duration in seconds = " + averageDuration;
    }

    public void STATS_mostCalls(){
        HashMap<String, Integer> frequencyMap = new HashMap<>();
        String mostFrequentPhoneNo = null;
        int maxCalls = 0;

        for (CallLogEntry entry : allCallLogs.allCallLogs) {
            String phoneNo = entry.phoneNo;
            int currentFrequency = frequencyMap.getOrDefault(phoneNo, 0) + 1;
            frequencyMap.put(phoneNo, currentFrequency);

            if (currentFrequency > maxCalls) {
                maxCalls = currentFrequency;
                mostFrequentPhoneNo = phoneNo;
            }
        }

        Log.d("QWER_STATS", "Most Calls to = " + contacts.getContact(mostFrequentPhoneNo) + "\t\tFrequency = " + maxCalls);
        statsString += "\nMost Calls to = " + contacts.getContact(mostFrequentPhoneNo) + "\tFrequency = " + maxCalls;
    }


    public void STATS_mostCallsOfType(int type){
        HashMap<String, Integer> frequencyMap = new HashMap<>();
        String mostFrequentPhoneNo = null;
        int maxCalls = 0;

        for (CallLogEntry entry : allCallLogs.allCallLogs) {
            if (entry.type == type) {                                                  // Filter calls by type 2
                String phoneNo = entry.phoneNo;
                int currentFrequency = frequencyMap.getOrDefault(phoneNo, 0) + 1;
                frequencyMap.put(phoneNo, currentFrequency);

                if (currentFrequency > maxCalls) {
                    maxCalls = currentFrequency;
                    mostFrequentPhoneNo = phoneNo;
                }
            }
        }

        Log.d("QWER_STATS", "Most Calls of type " + type + " to = " + contacts.getContact(mostFrequentPhoneNo) + "\t\tFrequency = " + maxCalls);
        statsString += "\nMost Calls of type " + type + " to = " + contacts.getContact(mostFrequentPhoneNo) + "\tFrequency = " + maxCalls;
    }


    public void STATS_mostCallsOfDuration(){                     // eg.  Most Calls duration is with = Hrishikesh Potnis		Time in seconds = 113371
        HashMap<String, Integer> durationMap = new HashMap<>();
        String phoneNoWithMaxDuration = null;
        int maxDuration = 0;

        for (CallLogEntry entry : allCallLogs.allCallLogs) {
            String phoneNo = entry.phoneNo;
            int currentDuration = durationMap.getOrDefault(phoneNo, 0) + entry.duration;
            durationMap.put(phoneNo, currentDuration);

            if (currentDuration > maxDuration) {
                maxDuration = currentDuration;
                phoneNoWithMaxDuration = phoneNo;
            }
        }

        Log.d("QWER_STATS", "Most Calls duration is with = " + contacts.getContact(phoneNoWithMaxDuration) + "\t\tTime in seconds = " + maxDuration);
        statsString += "\nMost Calls duration is with = " + contacts.getContact(phoneNoWithMaxDuration) + "\tTime in seconds = " + maxDuration;
    }

    public void STATS_mostCallsOfDuration(int n) {
        HashMap<String, Integer> durationMap = new HashMap<>();

        for (CallLogEntry entry : allCallLogs.allCallLogs) {
            String phoneNo = entry.phoneNo;
            int currentDuration = durationMap.getOrDefault(phoneNo, 0) + entry.duration;
            durationMap.put(phoneNo, currentDuration);
        }

        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer>> list = new ArrayList<>(durationMap.entrySet());

        // Sort the list using custom comparator
        Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        // After sorting, get the top 'n' entries
        for (int i = 0; i < Math.min(list.size(), n); i++) {
            Map.Entry<String, Integer> entry = list.get(i);
            String phoneNo = entry.getKey();
            int duration = entry.getValue();

            // Log the durations and their associated phone numbers
            Log.d("QWER_STATS", "Top " + (i + 1) + " Calls duration is with = " + contacts.getContact(phoneNo) + "\t\tTime in seconds = " + duration);
            statsString += "\nTop " + (i + 1) + " Calls duration is with = " + contacts.getContact(phoneNo) + "\tTime in seconds = " + duration;
        }
    }


    public void STATS_maxCallDurationInTotal(){
        int maxDuration = 0;
        CallLogEntry maxCallLog = null;
        for (CallLogEntry entry : allCallLogs.allCallLogs) {
            if (entry.duration > maxDuration) {
                maxDuration = entry.duration;
                maxCallLog = entry;
            }
        }
        Log.d("QWER_STATS", "Maximum Call duration in seconds = " + maxDuration + "\t\twith " + contacts.getContact(maxCallLog.phoneNo) + "\t\ton " + new Date(maxCallLog.dateInMilliSec));
        statsString += "\nMaximum Call duration in seconds = " + maxDuration + "\twith " + contacts.getContact(maxCallLog.phoneNo) + "\ton " + new Date(maxCallLog.dateInMilliSec);
    }

    public void STATS_monthWithMostCalls(){
        HashMap<Integer, Integer> callCountByMonth = new HashMap<>();
        for (CallLogEntry entry : allCallLogs.allCallLogs) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(entry.dateInMilliSec);
            int month = calendar.get(Calendar.MONTH);
            callCountByMonth.put(month, callCountByMonth.getOrDefault(month, 0) + 1);
        }

        int maxCalls = 0;
        int monthWithMostCalls = -1;
        for (Map.Entry<Integer, Integer> entry : callCountByMonth.entrySet()) {
            if (entry.getValue() > maxCalls) {
                maxCalls = entry.getValue();
                monthWithMostCalls = entry.getKey();
            }
        }
        String[] months = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };

        Log.d("QWER_STATS", "Month with Maximum Calls = " + months[monthWithMostCalls] + "\t\twith calls = " + maxCalls);
        statsString += "\nMonth with Maximum Calls = " + months[monthWithMostCalls] + "\twith calls = " + maxCalls;
    }

}