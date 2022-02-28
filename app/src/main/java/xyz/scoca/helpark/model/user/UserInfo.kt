package xyz.scoca.helpark.model.user


import com.google.gson.annotations.SerializedName

data class UserInfo(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("user")
    val user: User
)