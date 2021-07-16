package com.arjental.dealdone.network

import com.arjental.dealdone.network.interfaces.RetrofitInstanceInterface
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitInstance @Inject constructor(): RetrofitInstanceInterface {

//    val loggingInterceptor: HttpLoggingInterceptor =
//        HttpLoggingInterceptor()
//            .setLevel(HttpLoggingInterceptor.Level.BODY)

    private val okHttpClient: OkHttpClient =
        OkHttpClient()
            .newBuilder()
            .connectTimeout(2500, TimeUnit.MILLISECONDS)
            .writeTimeout(2500, TimeUnit.MILLISECONDS)
            .readTimeout(2500, TimeUnit.MILLISECONDS)
            .addInterceptor(object : Interceptor {

                override fun intercept(chain: Interceptor.Chain): Response {
                    val newRequest: Request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $TOKEN")
                        .build()
                    return chain.proceed(newRequest)
                }
            })
            .build()

    private val gson: Gson = GsonBuilder().setLenient().create()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    override val api: TasksApi by lazy {
        retrofit.create(TasksApi::class.java)
    }

    private companion object {
        private const val BASE_URL = "https://d5dps3h13rv6902lp5c8.apigw.yandexcloud.net/ "
        private const val TOKEN = "db0a23b1468c45ef84a6d42a2e174079"
    }

}