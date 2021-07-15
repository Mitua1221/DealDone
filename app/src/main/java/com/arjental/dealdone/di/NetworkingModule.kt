package com.arjental.dealdone.di

import com.arjental.dealdone.databases.tasksdatabase.TasksDb
import com.arjental.dealdone.databases.tasksdatabase.interfaces.TasksDbInterface
import com.arjental.dealdone.network.RetrofitInstance
import com.arjental.dealdone.network.interfaces.RetrofitInstanceInterface
import dagger.Binds
import dagger.Module

@Module
abstract class NetworkingModule {

    @Binds
    abstract fun provideRetrofitInstance(retrofitInstance: RetrofitInstance): RetrofitInstanceInterface

}