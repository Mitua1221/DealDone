package com.arjental.dealdone

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.work.*
import com.arjental.dealdone.di.DealDoneAppComponent
import com.arjental.dealdone.repository.Actualizer
import com.arjental.dealdone.repository.Repository
import com.arjental.dealdone.workmanager.TasksWorkerActualization
import com.arjental.dealdone.workmanager.TasksWorkerNotification
import java.util.concurrent.TimeUnit

private const val TAG = "DealDoneApplication"
const val NOTIFICATION_CHANNEL_ID = "tasks_poll"
private const val NOTIFICATION_WORK = "NOTIFICATION_WORK"
private const val UPDATE_WORK = "UPDATE_WORK"

class DealDoneApplication : Application() {

    val dealDoneComponent: DealDoneAppComponent by lazy {
        DaggerDealDoneAppComponent.factory().create(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        Translator
        Repository.initialize(this)

        Actualizer.initialize()
        Actualizer.get().actualize()

        setChannels()
        setNotificationWorker(this)
        setActualizationWorker(this)
    }

    private fun setChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)
            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setNotificationWorker(context: Context) {
        val constraints = Constraints.Builder()
            .build()
        val periodicRequest = PeriodicWorkRequest
            .Builder(TasksWorkerNotification::class.java, 1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            NOTIFICATION_WORK,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )

//        val workRequest = OneTimeWorkRequest
//            .Builder(TasksWorker::class.java)
//            .build()
//        WorkManager.getInstance(requireContext())
//            .enqueue(workRequest)
    }

    private fun setActualizationWorker(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val periodicRequest = PeriodicWorkRequest
            .Builder(TasksWorkerActualization::class.java, 1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UPDATE_WORK,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )
    }

}