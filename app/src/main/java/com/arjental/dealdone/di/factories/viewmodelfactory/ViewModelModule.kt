package com.arjental.dealdone.di.factories.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.arjental.dealdone.viewmodels.EditTaskFragmentViewModel
import com.arjental.dealdone.viewmodels.TasksListFragmentViewModel
import com.arjental.dealdone.viewmodels.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(TasksListFragmentViewModel::class)
    abstract fun bindTasksViewModel(viewModel: TasksListFragmentViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(EditTaskFragmentViewModel::class)
    abstract fun bindEditTasksViewModel(viewModel: EditTaskFragmentViewModel): ViewModel


}