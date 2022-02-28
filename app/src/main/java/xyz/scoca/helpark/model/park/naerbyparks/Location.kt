package xyz.scoca.helpark.model.park.naerbyparks

import com.google.gson.annotations.SerializedName

data class Location(
    @SerializedName("lat")
    val lat: Double? = null,
    @SerializedName("lng")
    val lng: Double? = null
)