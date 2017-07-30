package com.udacity.firebase.shoppinglistplusplus;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Includes one-time initialization of Firebase related code
 */
public class ShoppingListApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Firebase in the app.
        Firebase.setAndroidContext(this);
    }

}