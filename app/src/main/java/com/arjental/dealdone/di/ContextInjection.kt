package com.arjental.dealdone.di

import android.content.Context
import dagger.android.HasAndroidInjector

object ContextInjection {
    @JvmStatic
    fun inject(to: Any, with: Context) {
        (with.applicationContext as HasAndroidInjector).androidInjector().inject(to)
    }
}