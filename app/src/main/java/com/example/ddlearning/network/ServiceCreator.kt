package com.example.ddlearning.network

import com.tencent.mmkv.MMKV
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceCreator {

    private val PORT:String by lazy {
        getPort()
    }

    private val mmkv = MMKV.defaultMMKV()

    private var BASE_URL = "http://inaction.fun:${PORT}"
    private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    fun <T> create(service: Class<T>): T {
        return retrofit.create(service)
    }

    fun modifyPort(port:String) =
        mmkv.putString("port",port)

    fun getPort():String =
        mmkv.getString("port","7000")!!



}

val networkService = ServiceCreator.create(NetworkService::class.java)