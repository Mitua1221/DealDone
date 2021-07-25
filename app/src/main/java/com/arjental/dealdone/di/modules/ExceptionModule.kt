package com.arjental.dealdone.di.modules

import com.arjental.dealdone.exceptions.ExceptionsHandler
import com.arjental.dealdone.exceptions.interfaces.ExceptionsHandlerInterface
import dagger.Binds
import dagger.Module

@Module
abstract class ExceptionModule {

    @Binds
    abstract fun provideExceptions(exceptionsHandler: ExceptionsHandler): ExceptionsHandlerInterface

}