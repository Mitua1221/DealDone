package com.arjental.dealdone.di

import android.content.Context
import com.arjental.dealdone.DealDoneApplication
import com.arjental.dealdone.MainActivity
import com.arjental.dealdone.userinterface.LoadingFragment
import com.arjental.dealdone.userinterface.NewTaskFragment
import com.arjental.dealdone.userinterface.TasksFragment
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class, RepositoryModule::class, DatabaseModule::class, NetworkingModule::class])
interface DealDoneAppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): DealDoneAppComponent
    }

    fun injectApplication(app: DealDoneApplication)

    fun inject(activity: MainActivity)

    fun inject(fragment: LoadingFragment)

    fun inject(fragment: NewTaskFragment)

    fun inject(fragment: TasksFragment)

}