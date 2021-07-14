package com.arjental.dealdone.di

import android.content.Context
import com.arjental.dealdone.MainActivity
import com.arjental.dealdone.userinterface.LoadingFragment
import com.arjental.dealdone.userinterface.NewTaskFragment
import com.arjental.dealdone.userinterface.TasksFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [RepositoryModule::class])
interface DealDoneAppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): DealDoneAppComponent
    }

    fun inject(activity: MainActivity)

    fun inject(fragment: LoadingFragment)

    fun inject(fragment: NewTaskFragment)

    fun inject(fragment: TasksFragment)

}