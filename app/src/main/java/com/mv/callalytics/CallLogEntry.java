package com.mv.callalytics;

import android.provider.CallLog;

import androidx.annotation.NonNull;

import java.util.Date;

public class CallLogEntry {

    String phoneNo;
    int type;
    long dateInMilliSec;
    int duration;

    public CallLogEntry(String phoneNo, int type, long dateInMilliSec, int duration) {
        this.phoneNo = phoneNo;
        this.type = type;
        this.dateInMilliSec = dateInMilliSec;
        this.duration = duration;
    }

    @NonNull
    @Override
    public String toString(){
        super.toString();

        String typeOfCallStr = "";
        switch (type) {
            case CallLog.Calls.OUTGOING_TYPE:
                typeOfCallStr = "OUTGOING";
                break;

            case CallLog.Calls.INCOMING_TYPE:
                typeOfCallStr = "INCOMING";
                break;

            case CallLog.Calls.MISSED_TYPE:
                typeOfCallStr = "MISSED\t";
                break;

            case CallLog.Calls.BLOCKED_TYPE:
                typeOfCallStr = "BLOCKED\t";
                break;

            case CallLog.Calls.REJECTED_TYPE:
                typeOfCallStr = "REJECTED";
                break;

            case CallLog.Calls.VOICEMAIL_TYPE:
                typeOfCallStr = "VOICEMAIL";
                break;

            case CallLog.Calls.ANSWERED_EXTERNALLY_TYPE:
                typeOfCallStr = "ANSWERED_EXTERNALLY";
                break;

            default:
                typeOfCallStr = String.valueOf(type);
        }


        return "PhoneNo : " + phoneNo + "\t\tType " + typeOfCallStr + "\t\tTime : " + new Date(dateInMilliSec) + "\t\tDuration : " + duration;
    }
}