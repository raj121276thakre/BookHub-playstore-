package com.rajapps.bookify_test.Activity

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.firebase.database.FirebaseDatabase

class MyBookApp() : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        MobileAds.initialize(this) { } // admob openad
    }
}