package xyz.scoca.helpark.model.park.naerbyparks


import com.google.gson.annotations.SerializedName


data class NearbyPark(
    @SerializedName("html_attributions")
    val htmlAttributions: List<String>? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("next_page_token")
    val nextPageToken : String? = null,

    @SerializedName("results")
    val results: List<Result>? = null
)