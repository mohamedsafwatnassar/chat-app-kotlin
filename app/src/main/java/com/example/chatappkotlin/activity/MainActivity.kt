package com.example.chatappkotlin.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chatappkotlin.R
import com.example.chatappkotlin.fragments.GetUserNumber

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction()
                .add(R.id.main_container, GetUserNumber())
                .commit()
    }
}