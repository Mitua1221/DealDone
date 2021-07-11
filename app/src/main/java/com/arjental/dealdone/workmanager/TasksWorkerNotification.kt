package com.arjental.dealdone.workmanager


import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.arjental.dealdone.MainActivity
import com.arjental.dealdone.NOTIFICATION_CHANNEL_ID
import com.arjental.dealdone.R
import com.arjental.dealdone.repository.Repository
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "TasksWorkerNotification"

class TasksWorkerNotification(val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val date = SimpleDateFormat("HH").format(Date())

        if (date == "09") {

            Repository.initialize(context)
            val repository = Repository.get()

            val tasks = repository.getTasksFromDatabase()

            if (!tasks.isNullOrEmpty()) {

                val year = Calendar.getInstance().get(Calendar.YEAR)
                val day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

                val quality =
                    tasks.count { it.isSolved == false && dateCheck(year, day, it.deadline) }

                if (quality > 0) {
                    val intent = MainActivity.newIntent(context)
                    val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

                    val resources = context.resources
                    val notification = NotificationCompat
                        .Builder(context, NOTIFICATION_CHANNEL_ID)
                        .setTicker(resources.getString(R.string.tasks_to_done_title))
                        .setSmallIcon(android.R.drawable.ic_menu_report_image)
                        .setContentTitle(resources.getString(R.string.tasks_to_done_title))
                        .setContentText(
                            resources.getString(
                                R.string.new_tasks_text,
                                quality.toString()
                            )
                        )
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .build()
                    val notificationManager = NotificationManagerCompat.from(context)
                    notificationManager.notify(0, notification)
                }
            }
        }
        return Result.success()
    }

    private fun dateCheck(year: Int, day: Int, deadline: Long?): Boolean {
        return if (deadline == null) {
            false
        } else {
            val cal = Calendar.getInstance()
            cal.timeInMillis = deadline
            cal.get(Calendar.YEAR) == year && cal.get(Calendar.DAY_OF_YEAR) == day
        }


    }

}