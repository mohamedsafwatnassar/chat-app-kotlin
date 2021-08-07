package com.example.chatappkotlin

import android.app.Application
import android.content.Context
import com.google.firebase.database.FirebaseDatabase
import com.orhanobut.hawk.Hawk

open class application : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

    }

    override fun onCreate() {
        super.onCreate()

        Hawk.init(getApplicationContext()).build();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }

}