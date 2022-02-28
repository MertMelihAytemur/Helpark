package xyz.scoca.helpark.model.park.naerbyparks


import com.google.gson.annotations.SerializedName

data class Geometry(
    @SerializedName("location")
    val location: Location? = null,
    @SerializedName("viewport")
    val viewport: Viewport? = null
)