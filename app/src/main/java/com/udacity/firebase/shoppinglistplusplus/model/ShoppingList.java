package com.udacity.firebase.shoppinglistplusplus.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Elias Myronidis on 1/8/17.
 */

public class ShoppingList {

    private String listName;
    private String owner;
    private HashMap<String, Object> editTime;

    public ShoppingList() { }

    public ShoppingList(String listName, String owner) {
        this.listName = listName;
        this.owner = owner;

        HashMap<String, Object> editTimeTemp = new HashMap<>();
        editTimeTemp.put("edit_time", ServerValue.TIMESTAMP);
        editTime = editTimeTemp;
    }

    public String getListName() {
        return listName;
    }

    public String getOwner() {
        return owner;
    }

    public Map<String, Object> getEditTime() {
        return editTime;
    }

    @Exclude
    public long getEditTimeLong(){
        return (long)editTime.get("edit_time");
    }

}
