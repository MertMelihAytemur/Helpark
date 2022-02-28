package xyz.scoca.helpark.network.service

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import xyz.scoca.helpark.model.auth.ApiResponse
import xyz.scoca.helpark.model.reservation.Reservation
import xyz.scoca.helpark.model.user.UserInfo

interface AuthService {
    @FormUrlEncoded
    @POST("/scoca/login.php")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<ApiResponse>

    @FormUrlEncoded
    @POST("/scoca/register.php")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<ApiResponse>

    @FormUrlEncoded
    @POST("/scoca/fetch_user.php")
    fun fetchUserData(
        @Field("email") email :String
    ) : Call<UserInfo>

    @FormUrlEncoded
    @POST("/scoca/update_user_data.php")
    fun updateUserData(
        @Field("name") name : String,
        @Field("email") email : String
    ) : Call<UserInfo>

    @FormUrlEncoded
    @POST("/scoca/get_feedback.php")
    fun getFeedback(
        @Field("feedback_text") message : String,
        @Field("feedback_email") email : String
    ): Call<UserInfo>

    @FormUrlEncoded
    @POST("/scoca/delete_account.php")
    fun deleteAccount(
        @Field("email") email : String
    ) : Call<UserInfo>

    @FormUrlEncoded
    @POST("/scoca/post_reservation.php")
    fun postReservation(
        @Field("r_park_name") parkName : String,
        @Field("r_email") email : String,
        @Field("r_date") date : String,
        @Field("r_fee") parkFee : Float
    ) : Call<UserInfo>

    @FormUrlEncoded
    @POST("/scoca/get_reservation.php")
    fun getReservation(
        @Field("r_email") email : String
    ) : Call<Reservation>

}