package com.example.chatappkotlin.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.chatappkotlin.R
import com.example.chatappkotlin.database.dao.UserDao
import com.example.chatappkotlin.database.model.UserModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.orhanobut.hawk.Hawk

class SplashActivity : AppCompatActivity() {
    var userModel: UserModel? = UserModel()
    var userDao: UserDao? = UserDao()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        userModel = Hawk.get("CurrentUser")

        Handler().postDelayed({

            if (userModel != null) {
                FirebaseInstanceId.getInstance().instanceId
                    .addOnCompleteListener(OnCompleteListener {
                        if (it.isSuccessful) {
                            val token = it.result?.token

                            val map = mapOf(
                                "token" to token)

                            userDao!!.createToken(userModel!!.uID, map)
                        }
                    })
                startActivity(Intent(this, DashBoardActivity::class.java))
                finish()

            } else {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }

        }, 3000)

    }

}