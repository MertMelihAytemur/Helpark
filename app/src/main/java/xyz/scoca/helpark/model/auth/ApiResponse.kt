package xyz.scoca.helpark.model.auth


import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("error")
    val error: Boolean? = null,
    @SerializedName("uid")
    val uid: Any? = null,
    @SerializedName("user")
    val user: User? = null
)