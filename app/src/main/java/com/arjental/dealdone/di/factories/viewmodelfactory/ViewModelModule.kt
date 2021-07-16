package com.arjental.dealdone.di.factories.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.arjental.dealdone.viewmodels.EditTaskViewModel
import com.arjental.dealdone.viewmodels.TasksFragmentViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(TasksFragmentViewModel::class)
    abstract fun bindTasksViewModel(viewModel: TasksFragmentViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(EditTaskViewModel::class)
    abstract fun bindEditTasksViewModel(viewModel: EditTaskViewModel): ViewModel


}