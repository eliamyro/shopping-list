package com.udacity.firebase.shoppinglistplusplus.model;

/**
 * Created by Elias Myronidis on 6/8/17.
 */

public class ShoppingListItem {

    private String itemName;
    private String itemOwner;

    public ShoppingListItem() {
    }

    public ShoppingListItem(String itemName) {
        this.itemName = itemName;
        this.itemOwner = "Anonymous Owner";
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemOwner() {
        return itemOwner;
    }
}
