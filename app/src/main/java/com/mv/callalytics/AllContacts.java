package com.mv.callalytics;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;

public class AllContacts {

    HashSet<String> numbers = new HashSet<>();                    // Primary key is number
    ArrayList<Contact> contacts = new ArrayList<>();


    public int size(){
        return contacts.size();
    }

    public void add(Contact contact){
        if(numbers.contains(contact.phoneNo)){
            Log.d("QWER", "Contact " + contact.phoneNo + " already added!");
            return;
        }
        contacts.add(contact);
        numbers.add(contact.phoneNo);
    }

    public Contact get(int i){
        return contacts.get(i);
    }


    public void clear(){
        contacts.clear();
        numbers.clear();
    }

    public String getContact(String phoneNo){
        phoneNo = Contact.trimPhoneNo(phoneNo);
        if(!numbers.contains(phoneNo)){
            return phoneNo;
        }
        for(int i=0; i<contacts.size(); i++){
            if(contacts.get(i).phoneNo.equals(phoneNo)){
                return contacts.get(i).name;
            }
        }
        return phoneNo;
    }

}
