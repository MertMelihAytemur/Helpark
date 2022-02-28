package xyz.scoca.helpark.ui.auth.login


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.scoca.helpark.data.local.ClientPreferences
import xyz.scoca.helpark.model.auth.ApiResponse
import xyz.scoca.helpark.model.status.OnError
import xyz.scoca.helpark.model.status.OnSuccess
import xyz.scoca.helpark.model.status.StatusModel
import xyz.scoca.helpark.network.NetworkHelper

class LoginViewModel : ViewModel() {

    val userDetail = MutableLiveData<StatusModel<
            ApiResponse>>()

    fun login(userEmail: String, userPassword: String) {
        NetworkHelper().authService?.login(userEmail, userPassword)
            ?.enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    val result = response.body()?.error
                    if (result == false) {
                        userDetail.postValue(OnSuccess(response.body()))
                    } else
                        userDetail.postValue(OnError<String>("Login information are wrong. Try again."))
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    userDetail.postValue(OnError<String>("Login Failed."))
                }
            })
    }
}