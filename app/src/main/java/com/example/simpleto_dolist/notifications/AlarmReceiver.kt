package com.example.simpleto_dolist.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/*This Class creates the notification for the specific Task. The notification receives the Tasks
* NAME and the REMINDER_TEXT from AddEditTaskFragment. When it receives the needed values, it
* creates a unique ID, for this task and creates the notification.*/
class AlertReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val intent = intent
        val taskName = intent!!.extras!!.getString("task_name")
        val title = intent.extras!!.getString("title")

        val id = (0..2147483647).random()

        val notificationHelper = NotificationHelper(context)
        val nb = notificationHelper.channelNotification
        nb.setContentText("$taskName")
        nb.setContentTitle("$title")
            .setNumber(id)

        notificationHelper.manager!!.notify(id, nb.build())
    }


}