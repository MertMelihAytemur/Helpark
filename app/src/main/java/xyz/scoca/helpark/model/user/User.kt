package xyz.scoca.helpark.model.user


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("name")
    val name: String
)