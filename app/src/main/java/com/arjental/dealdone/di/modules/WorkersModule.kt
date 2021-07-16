package com.arjental.dealdone.di.modules

import com.arjental.dealdone.workmanager.TasksWorkerActualization
import com.arjental.dealdone.workmanager.TasksWorkerNotification
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class WorkersModule {

    @ContributesAndroidInjector
    abstract fun workerNotification(): TasksWorkerNotification

    @ContributesAndroidInjector
    abstract fun workerActualization(): TasksWorkerActualization

}