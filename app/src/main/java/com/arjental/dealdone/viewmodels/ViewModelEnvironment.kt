package com.arjental.dealdone.viewmodels

import com.arjental.dealdone.Translator
import com.arjental.dealdone.repository.Actualizer
import com.arjental.dealdone.viewmodels.interfaces.ViewModelEnvironmentInterface
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ViewModelEnvironment @Inject constructor() : ViewModelEnvironmentInterface {

    init {
        initialize(this)
    }

    @Inject
    lateinit var translator: Translator

    @Inject
    lateinit var actualizer: Actualizer

    companion object {
        private var INSTANCE: ViewModelEnvironment? = null
        fun initialize(viewModelEnvironment: ViewModelEnvironment) {
            if (INSTANCE == null) {
                INSTANCE = viewModelEnvironment
            }
        }

        fun get(): ViewModelEnvironment {
            return INSTANCE ?: throw IllegalStateException("ViewModelEnvironment first must be initialized")
        }
    }

}