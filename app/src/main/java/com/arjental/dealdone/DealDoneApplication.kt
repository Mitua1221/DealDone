package com.arjental.dealdone

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.*
import com.arjental.dealdone.di.DaggerDealDoneAppComponent
import com.arjental.dealdone.di.DealDoneAppComponent
import com.arjental.dealdone.repository.Actualizer
import com.arjental.dealdone.viewmodels.ViewModelEnvironment
import com.arjental.dealdone.workmanager.TasksWorkerActualization
import com.arjental.dealdone.workmanager.TasksWorkerNotification
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val TAG = "DealDoneApplication"
const val NOTIFICATION_CHANNEL_ID = "tasks_poll"
private const val NOTIFICATION_WORK = "NOTIFICATION_WORK"
private const val UPDATE_WORK = "UPDATE_WORK"


class DealDoneApplication : Application(), HasAndroidInjector {

    lateinit var appComponent: DealDoneAppComponent

    @Inject lateinit var androidInjector : DispatchingAndroidInjector<Any>

    @Inject lateinit var actualizer: Actualizer

    @Inject lateinit var viewModelEnvironment: ViewModelEnvironment

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerDealDoneAppComponent.factory().create(this)

        appComponent.injectApplication(this)

        actualizer.actualize()

        viewModelEnvironment

        setChannels()
        setNotificationWorker(this)
        setActualizationWorker(this)
    }

    override fun androidInjector(): AndroidInjector<Any> = androidInjector

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
//            .Builder(TasksWorkerNotification::class.java)
//            .build()
//        WorkManager.getInstance(context)
//            .enqueue(workRequest)
    }

    private fun setActualizationWorker(context: Context) {
        val constraints = Constraints.Builder()
            .build()
        val periodicRequest = PeriodicWorkRequest
            .Builder(TasksWorkerActualization::class.java, 8, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UPDATE_WORK,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )

//        val workRequest = OneTimeWorkRequest
//            .Builder(TasksWorkerActualization::class.java)
//            .build()
//        WorkManager.getInstance(context)
//            .enqueue(workRequest)

    }




}