package com.example.chatappkotlin.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import com.example.chatappkotlin.R
import com.example.chatappkotlin.activity.DashBoardActivity
import com.example.chatappkotlin.database.model.UserModel
import com.example.chatmekotlin.Constants.AppConstants
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.orhanobut.hawk.Hawk
import java.util.*
import kotlin.collections.HashMap

class FirebaseNotificationService : FirebaseMessagingService() {

    var user: UserModel? = UserModel()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        updateToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage.data.isNotEmpty()) {

            val map: Map<String, String> = remoteMessage.data

            val title = map["title"]
            val message = map["message"]
            val hisId = map["hisId"]
            val hisImage = map["hisImage"]
            val chatId = map["chatId"]

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
                createOreoNotification(title!!, message!!, hisId!!, hisImage!!)
            else createNormalNotification(title!!, message!!, hisId!!, hisImage!!)

        }
    }

    private fun updateToken(token: String) {
        user = Hawk.get("CurrentUser")
        if (user != null) {
            val databaseReference =
                FirebaseDatabase.getInstance().getReference("Users").child(user!!.uID)
            val map: MutableMap<String, Any> = HashMap()
            map["token"] = token
            databaseReference.updateChildren(map)
        }
    }

    // create normal notification in case if android less than android(Oreo) [android 8]
    private fun createNormalNotification(
        title: String, message: String, hisId: String, hisImage: String
    ) {

        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(this, AppConstants.CHANNEL_ID)
        builder.setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.ic_chat)
            .setAutoCancel(true)
            .setColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
            .setSound(uri)

        val intent = Intent(this, DashBoardActivity::class.java)

        intent.putExtra("hisId", hisId)
        intent.putExtra("hisImage", hisImage)
        //intent.putExtra("chatId", chatId)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        builder.setContentIntent(pendingIntent)

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(Random().nextInt(85 - 65), builder.build())

    }

    // create oreo notification in case if android bigger than android(Oreo) [android 8]
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createOreoNotification(
        title: String, message: String, hisId: String, hisImage: String
    ) {

        val channel = NotificationChannel(
            AppConstants.CHANNEL_ID,
            "Message",
            NotificationManager.IMPORTANCE_HIGH
        )

        channel.setShowBadge(true)
        channel.enableLights(true)
        channel.enableVibration(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val intent = Intent(this, DashBoardActivity::class.java)

        intent.putExtra("hisId", hisId)
        intent.putExtra("hisImage", hisImage)
        //intent.putExtra("chatId", chatId)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = Notification.Builder(this, AppConstants.CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_chat)
            .setAutoCancel(true)
            .setColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(Random().nextInt(85 - 65), notification)
    }
}
