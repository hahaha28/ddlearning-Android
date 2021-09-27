package com.example.ddlearning.bean

import com.google.gson.annotations.SerializedName

data class RoomDetail(
    val info: Info,
    @SerializedName("POIs")
    val pois:List<Seat>,
    val bestPairSeats: BestPairSeats
) {

    data class Info(
        /**
         * 如“瑶湖二楼北自习室”
         */
        val title:String,

        /**
         * 如 “二楼”
         */
        val storage: String,
        val width: Int,
        val height: Int
    )

    data class BestPairSeats(
        val seats:List<Seat>
    )
}
