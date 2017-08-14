package com.udacity.firebase.shoppinglistplusplus.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;
import com.udacity.firebase.shoppinglistplusplus.utils.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Elias Myronidis on 1/8/17.
 */

public class ShoppingList {

    private String listName;
    private String owner;
    private HashMap<String, Object> timestampLastChanged;
    private HashMap<String, Object> timestampCreated;

    public ShoppingList() { }

    public ShoppingList(String listName, String owner) {
        this.listName = listName;
        this.owner = owner;

        HashMap<String, Object> timestampCreatedObj = new HashMap<>();
        timestampCreatedObj.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        timestampCreated = timestampCreatedObj;
        timestampLastChanged = timestampCreated;
    }

    public String getListName() {
        return listName;
    }

    public String getOwner() {
        return owner;
    }

    public Map<String, Object> getTimestampLastChanged() {
        return timestampLastChanged;
    }

    @Exclude
    public long getTimestampLastChangedLong(){
        return (long)timestampLastChanged.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }

    public Map<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    @Exclude
    public long getTimestampCreatedLong(){
        return (long)timestampCreated.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }
}
