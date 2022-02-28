package xyz.scoca.helpark.data.local

import android.content.Context

class ClientPreferences(context: Context) {
    companion object {
        private const val PREFERENCES_NAME = "User"
        private const val USER_EMAIL = "UserEmail"
        private const val REMEMBER_ME = "Done"
    }

    private val sharedPreferences by lazy {
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    fun setUserEmail(userEmail : String){
        with(sharedPreferences.edit()){
            userEmail.let {
                putString(USER_EMAIL,it)
                apply()
            }
        }
    }

    fun getUserEmail() = sharedPreferences.getString(USER_EMAIL,"")

    fun clearSharedPref(){
        with(sharedPreferences.edit()){
            clear()
            apply()
        }
    }


    fun setRememberMe(state : Boolean){
        with(sharedPreferences.edit()){
            putBoolean(REMEMBER_ME,state)
            apply()
        }
    }

    fun isRememberMe() = sharedPreferences.getBoolean(REMEMBER_ME,false)

}