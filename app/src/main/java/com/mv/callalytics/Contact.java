package com.mv.callalytics;

public class Contact {

    String name;
    String phoneNo;

    public Contact(String name, String phoneNo) {
        phoneNo = phoneNo.replace(" ", "");
        if(phoneNo.length() > 10) {
            phoneNo = phoneNo.substring(phoneNo.length() - 10);
        }
        this.name = name;
        this.phoneNo = phoneNo;
    }
}
