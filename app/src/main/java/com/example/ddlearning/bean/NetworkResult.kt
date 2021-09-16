package com.example.ddlearning.bean

data class NetworkResult<T>(
        val data: T? = null,
        val code: Int,
        val msg: String
) {
}