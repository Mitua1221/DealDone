package com.arjental.dealdone.network.interfaces

import com.arjental.dealdone.network.TasksApi
import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit

interface RetrofitInstanceInterface {

    val api: TasksApi

}