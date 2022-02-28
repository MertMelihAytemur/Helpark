package xyz.scoca.helpark.model.reservation


import com.google.gson.annotations.SerializedName

data class ReservationData(
    @SerializedName("park_fee")
    val fee: Int,
    @SerializedName("park_name")
    val parkName: String,
    @SerializedName("park_reservation_date")
    val reservationDate: String
)