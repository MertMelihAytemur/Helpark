package xyz.scoca.helpark.util.common

import xyz.scoca.helpark.network.nearby.IGoogleApiService
import xyz.scoca.helpark.network.nearby.RetrofitClient

object Common{
    private val GOOGLE_API_URL = "https://maps.googleapis.com/"
    const val PARK_ID = "parking"

    val googleApiService : IGoogleApiService
    get() = RetrofitClient.getClient(GOOGLE_API_URL)
        .create(IGoogleApiService::class.java)
}
