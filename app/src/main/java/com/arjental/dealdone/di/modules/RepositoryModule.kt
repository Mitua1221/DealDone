package com.arjental.dealdone.di.modules

import com.arjental.dealdone.databases.tasksdatabase.TasksDb
import com.arjental.dealdone.databases.tasksdatabase.interfaces.TasksDbInterface
import com.arjental.dealdone.network.RetrofitInstance
import com.arjental.dealdone.network.interfaces.RetrofitInstanceInterface
import com.arjental.dealdone.repository.Actualizer
import com.arjental.dealdone.repository.ConverterFromApi
import com.arjental.dealdone.repository.Repository
import com.arjental.dealdone.repository.interfaces.ActualizerInterface
import com.arjental.dealdone.repository.interfaces.ConverterFromApiInterface
import com.arjental.dealdone.repository.interfaces.RepositoryInterface
import com.arjental.dealdone.viewmodels.ViewModelEnvironment
import com.arjental.dealdone.viewmodels.interfaces.ViewModelEnvironmentInterface
import dagger.Binds
import dagger.Module

@Module
abstract class RepositoryModule {

    @Binds
    abstract fun provideRepository(repository: Repository): RepositoryInterface

    @Binds
    abstract fun provideConverterFromApi(converterFromApi: ConverterFromApi): ConverterFromApiInterface

    @Binds
    abstract fun provideActualizer(actualizer: Actualizer): ActualizerInterface

    @Binds
    abstract fun provideRetrofitInstance(retrofitInstance: RetrofitInstance): RetrofitInstanceInterface

    @Binds
    abstract fun provideTaskDb(tasksDb: TasksDb): TasksDbInterface

    @Binds
    abstract fun retVMEnvironment(viewModelEnvironment: ViewModelEnvironment): ViewModelEnvironmentInterface

}