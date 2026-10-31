package com.example.utis;

import android.app.Application;
import com.google.firebase.database.FirebaseDatabase;

public class UTISApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Enable disk persistence to store the version locally.
        // It keeps the data cached and only fetches updates (delta) when the app is opened, saving API calls.
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
