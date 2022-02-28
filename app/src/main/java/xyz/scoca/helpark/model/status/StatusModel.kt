package xyz.scoca.helpark.model.status

sealed class StatusModel<out T : Any>

data class OnSuccess<out T : Any>(val data: T?) : StatusModel<T>()
data class OnError<out T : Any>(val msg: String?) : StatusModel<Nothing>()
