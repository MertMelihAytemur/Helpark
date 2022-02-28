package xyz.scoca.helpark.network.service

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import xyz.scoca.helpark.model.park.Park
import xyz.scoca.helpark.model.park.ParkData

interface ParkService {
    @GET("/scoca/all_park_data.php")
    fun getParkData() : Call<Park>


}