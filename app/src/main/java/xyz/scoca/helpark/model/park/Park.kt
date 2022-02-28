package xyz.scoca.helpark.model.park


import com.google.gson.annotations.SerializedName

data class Park(
    @SerializedName("park_data")
    val parkData: ArrayList<ParkData>,
    @SerializedName("success")
    val success: Int
)