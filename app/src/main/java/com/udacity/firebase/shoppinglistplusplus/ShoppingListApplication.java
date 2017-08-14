package com.udacity.firebase.shoppinglistplusplus;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;


/**
 * Includes one-time initialization of Firebase related code
 */
public class ShoppingListApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG);
    }

}