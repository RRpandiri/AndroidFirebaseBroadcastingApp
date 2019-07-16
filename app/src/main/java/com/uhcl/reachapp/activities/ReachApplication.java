package com.uhcl.reachapp.activities;

import android.app.Application;

import com.firebase.client.Firebase;

public class ReachApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);

    }
}
