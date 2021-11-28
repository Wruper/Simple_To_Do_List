package com.example.simpleto_dolist.notifications


import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import com.example.simpleto_dolist.MainActivity
import com.example.simpleto_dolist.R

/*This class helps create the notification for a specific Task and changes the settings for this app
* that are related to notifications.*/
class NotificationHelper(base: Context?) : ContextWrapper(base) {
    private var mManager: NotificationManager? = null

    @TargetApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val channel =
            NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH)
        manager!!.createNotificationChannel(channel)

        //Makes the app Vibrate, when the alarm is triggered(Only while being in the app)
        val v = this.getSystemService(VIBRATOR_SERVICE) as Vibrator
        v.vibrate(VibrationEffect.createOneShot(1000, 60))

        //Uses the phones set Default sound, to alert the user when the notification is triggered.
        try {
            val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(applicationContext, notification)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //Creates the Intent for the notification, which will be used later
    private val intent = Intent(this, MainActivity::class.java)
    private val resultPendingIntent: PendingIntent =
        PendingIntent.getActivity(
            this,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

    // Sets the Default values for the notification
    val channelNotification: NotificationCompat.Builder
        get() = NotificationCompat.Builder(applicationContext, channelID)
            .setSmallIcon(R.drawable.warning_icon)
            .setColor(Color.parseColor("#e7d5ad"))
            .setAutoCancel(true) //Notification will disappear if clicked
            .setContentIntent(resultPendingIntent)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)


    val manager: NotificationManager?
        get() {
            if (mManager == null) {
                mManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }
            return mManager
        }


    companion object {
        const val channelID = "channelID"
        const val channelName = "Channel Name"
    }

    //Checks the phones SDK version
    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
    }
}