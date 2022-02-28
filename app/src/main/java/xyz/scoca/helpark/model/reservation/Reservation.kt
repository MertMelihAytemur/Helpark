package xyz.scoca.helpark.model.reservation


import com.google.gson.annotations.SerializedName

data class Reservation(
    @SerializedName("reservation")
    val reservation: ArrayList<ReservationData>
)