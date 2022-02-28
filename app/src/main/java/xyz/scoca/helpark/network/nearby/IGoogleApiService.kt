package xyz.scoca.helpark.network.nearby

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url
import xyz.scoca.helpark.model.park.naerbyparks.NearbyPark


interface IGoogleApiService {
    @GET
    fun getNearbyPark(@Url url : String) : Call<NearbyPark>

}