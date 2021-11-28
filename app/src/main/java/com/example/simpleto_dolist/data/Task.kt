package com.example.simpleto_dolist.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat

/*Database for the app*/
@Entity(tableName = "task_table")
@Parcelize
data class Task(
    val name: String,
    var hasReminder: Boolean = false,
    val completed: Boolean = false,
    val created: Long = System.currentTimeMillis(),
    val notificationCode: Int = 0,
    var reminderCreated: Long = 0,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) : Parcelable {
    val createdDateFormatted: String
        get() = DateFormat.getDateTimeInstance().format(created)

    val setReminderTimeFormatted: String
        get() = DateFormat.getDateTimeInstance().format(reminderCreated)
}