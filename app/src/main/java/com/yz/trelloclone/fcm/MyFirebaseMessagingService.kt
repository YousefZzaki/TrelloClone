package com.yz.trelloclone.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.yz.trelloclone.R
import com.yz.trelloclone.Utils.Constants
import com.yz.trelloclone.Utils.Constants.FCM_KEY_MESSAGE
import com.yz.trelloclone.Utils.Constants.FCM_KEY_TITLE
import com.yz.trelloclone.Utils.Constants.USER_ASSIGNED_NOTIFICATION_ID
import com.yz.trelloclone.activities.BaseActivity.Companion.TAG
import com.yz.trelloclone.activities.MainActivity


class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d(TAG, "From: ${message.from}")

        // Check if message contains a data payload.
        message.data.isNotEmpty().let {
            // The notification data payload is printed in the log.
            Log.i(TAG, "Message data payload: " + message.data)

            // The Title and Message are assigned to the local variables
            val title = message.data[FCM_KEY_TITLE]!!
            val message = message.data[FCM_KEY_MESSAGE]!!
            Log.i(TAG, "Message data title: $title")
            Log.i(TAG, "Message data message: $message")
            // Finally sent them to build a notification.
            sendNotification(title, message)
        }
        // END

        // Check if message contains a notification payload.
        message.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {

        val sharedPreferences =
            this.getSharedPreferences(Constants.PROJECT_PREFS, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(Constants.FCM_TOKEN, token)
        editor.apply()
    }

    private fun sendNotification(title: String, message: String) {

        val intent = Intent(this, MainActivity::class.java)

        intent.addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        )

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val channelId = resources.getString(R.string.notification_channel_id)
        val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(
            this, channelId
        )
            .setSmallIcon(R.drawable.ic_baseline_android_24)
            .setContentTitle(title)
            .setContentText(message)
            .setSound(notificationSound)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel project",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(USER_ASSIGNED_NOTIFICATION_ID, notificationBuilder.build())
    }

}