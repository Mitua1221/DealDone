package com.arjental.dealdone.di

import com.arjental.dealdone.DealDoneApplication
import com.arjental.dealdone.databases.tasksdatabase.TasksDb
import com.arjental.dealdone.databases.tasksdatabase.interfaces.TasksDbInterface
import com.arjental.dealdone.repository.Actualizer
import com.arjental.dealdone.repository.ConverterFromApi
import com.arjental.dealdone.repository.Repository
import com.arjental.dealdone.repository.interfaces.ActualizerInterface
import com.arjental.dealdone.repository.interfaces.ConverterFromApiInterface
import com.arjental.dealdone.repository.interfaces.RepositoryInterface
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class DatabaseModule {

    @Binds
    abstract fun provideTaskDb(tasksDb: TasksDb): TasksDbInterface

}