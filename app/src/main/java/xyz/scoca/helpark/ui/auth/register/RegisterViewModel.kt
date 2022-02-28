package xyz.scoca.helpark.ui.auth.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.scoca.helpark.model.auth.ApiResponse
import xyz.scoca.helpark.model.status.OnError
import xyz.scoca.helpark.model.status.OnSuccess
import xyz.scoca.helpark.model.status.StatusModel
import xyz.scoca.helpark.network.NetworkHelper

class RegisterViewModel : ViewModel() {
    val userDetail = MutableLiveData<StatusModel<ApiResponse>>()

    fun register(name: String, email: String, password: String){
        NetworkHelper().authService?.register(name, email, password)
            ?.enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    val result = response.body()?.error
                    if (result == false) {
                        userDetail.postValue(OnSuccess(response.body()))
                    } else{
                        userDetail.postValue(OnError<String>("Unknown error occurred in registration"))
                    }
                }
                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    userDetail.postValue(OnError<String>("Registration Failure."))
                }
            })
    }
}