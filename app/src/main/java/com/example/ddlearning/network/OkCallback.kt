package com.example.ddlearning.network

import android.util.Log
import com.example.ddlearning.bean.NetworkResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class OkCallback<T> : Callback<NetworkResult<T>> {

    private val TAG = "OkCallback"

    override fun onResponse(call: Call<NetworkResult<T>>, response: Response<NetworkResult<T>>) {
        if (response.isSuccessful && response.body() != null) {
            val data = response.body()!!
            if(data.code == 200){
                onSuccess(response.body()!!.data)
            }else{
                onError(data.code,data.msg)
            }
        } else {
            onError(response.code(),response.message())
        }
        onFinally()
    }

    override fun onFailure(call: Call<NetworkResult<T>>, t: Throwable) {
        onFailureFinally()
        onFinally()
        Log.d(TAG, "onFailure: ${t.toString()}")
    }

    open fun onSuccess(data: T?) {
        Log.d(TAG, "onSuccess: ${data}")
    }

    open fun onError(code: Int, msg: String) {
        onFailureFinally()
        Log.d(TAG, "onError: $code,$msg")
    }

    open fun onFailureFinally() {

    }

    open fun onFinally(){

    }

}