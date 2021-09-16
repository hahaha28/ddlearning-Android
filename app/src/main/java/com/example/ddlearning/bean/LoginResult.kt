package com.example.ddlearning.bean

import com.google.gson.annotations.SerializedName

data class LoginResult(

    val id:String,

    @SerializedName("category_id")
    val categoryId:String,

    val name:String,

) {
}