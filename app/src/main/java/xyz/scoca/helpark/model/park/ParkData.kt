package xyz.scoca.helpark.model.park


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParkData(
    @SerializedName("park_active_hours")
    val parkActiveHours: String?=null,
    @SerializedName("park_car_count")
    val parkCarCount: String?=null,
    @SerializedName("park_electric_charge")
    val parkElectricCharge: String?=null,
    @SerializedName("park_fee")
    val parkFee: String?=null,
    @SerializedName("park_max_car_count")
    val parkMaxCarCount: String,
    @SerializedName("park_location_city")
    val parkLocationCity: String?=null,
    @SerializedName("park_location_county")
    val parkLocationCounty: String?=null,
    @SerializedName("park_name")
    val parkName: String?=null,
    @SerializedName("park_point")
    val parkPoint: String?=null,
    @SerializedName("park_id")
    val parkId: String?=null
) : Parcelable