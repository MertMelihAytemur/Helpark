package xyz.scoca.helpark.util.extension

import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar


fun Fragment.toast(message: String?, duration: Int = Toast.LENGTH_LONG) {
    message?.let {
        Toast.makeText(requireContext(), it, duration).show()
    }
}
fun Fragment.snack(view: View, message: String, duration: Int = Snackbar.LENGTH_LONG) {
    Snackbar.make(view,message,duration).show()
}
