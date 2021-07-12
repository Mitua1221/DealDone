package com.arjental.dealdone.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitInstance {

    private const val BASE_URL = "https://d5dps3h13rv6902lp5c8.apigw.yandexcloud.net/ "

//    val loggingInterceptor: HttpLoggingInterceptor =
//        HttpLoggingInterceptor()
//            .setLevel(HttpLoggingInterceptor.Level.BODY)

    private const val TOKEN = "db0a23b1468c45ef84a6d42a2e174079"

    val okHttpClient: OkHttpClient =
        OkHttpClient()
            .newBuilder()
            .addInterceptor(object : Interceptor {

                override fun intercept(chain: Interceptor.Chain): Response {
                    val newRequest: Request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $TOKEN")
                        .build()
                    return chain.proceed(newRequest)
                }
            })
            .connectTimeout(2, TimeUnit.SECONDS)
//            .addInterceptor(loggingInterceptor)
            .build()

    val gson: Gson = GsonBuilder().setLenient().create()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val api: TasksApi by lazy {
        retrofit.create(TasksApi::class.java)
    }

}