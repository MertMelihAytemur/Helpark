package xyz.scoca.helpark.model.park.naerbyparks


import com.google.gson.annotations.SerializedName

data class OpeningHours(
    @SerializedName("open_now")
    val openNow: Boolean? = null
)