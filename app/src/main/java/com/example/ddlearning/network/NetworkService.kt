package com.example.ddlearning.network

import com.example.ddlearning.bean.LoginResult
import com.example.ddlearning.bean.NetworkResult
import com.example.ddlearning.bean.Task
import retrofit2.Call
import retrofit2.http.*

interface NetworkService {

    @FormUrlEncoded
    @POST("/login")
    fun login(@Field("account") account: String, @Field("password") password: String): Call<NetworkResult<LoginResult>>

    @GET("/task/get")
    fun getTaskList(@Query("account")account:String):Call<NetworkResult<List<Task>>>

    @POST("/task/switch")
    fun switchTask(@Query("id")taskId:String,@Query("switch")switch:Boolean):Call<NetworkResult<Any>>

    @POST("/task/modify")
    fun replaceTask(@Body task:Task):Call<NetworkResult<Any>>

    @POST("/task/add")
    fun addTask(@Body task:Task):Call<NetworkResult<Task>>

    @POST("/task/delete")
    fun deleteTask(@Query("id")taskId:String):Call<NetworkResult<Any>>

}