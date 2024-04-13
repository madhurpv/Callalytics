package com.mv.callalytics;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;

public class AllCallLogs {



    HashSet<Long> timesOfCalls = new HashSet<>();                    // Primary key is timestamp
    ArrayList<CallLogEntry> allCallLogs = new ArrayList<>();


    private boolean containsTime(Long time){
        /*for(int i=0; i<allCallLogs.size(); i++){
            if(allCallLogs.get(i).dateInMilliSec == time) {
                return true;
            }
        }
        return false;*/
        return timesOfCalls.contains(time);
    }

    public void addCallLog(CallLogEntry callLogEntry){
        if(this.containsTime(callLogEntry.dateInMilliSec)){
            Log.d("QWER", "Not adding time " + callLogEntry.dateInMilliSec + " as it is already present!");
            return;
        }
        allCallLogs.add(callLogEntry);
        timesOfCalls.add(callLogEntry.dateInMilliSec);
    }

    public int size(){
        return allCallLogs.size();
    }


    public CallLogEntry get(int i){
        return allCallLogs.get(i);
    }


    public void clear(){
        allCallLogs.clear();
        timesOfCalls.clear();
    }




}
