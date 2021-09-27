package com.example.ddlearning.bean

data class Seat(
    val id:String,
    val title:String,
    val x:Int,
    val y:Int,
    val w:Int,
    val h:Int,
    /**
     * 预约状态
     */
    val state:Int
) {
}