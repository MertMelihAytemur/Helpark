package xyz.scoca.helpark.util

import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter

@BindingAdapter("app:setCity")
fun setCity(textView: AppCompatTextView, city: String?) {
    city?.let {
        textView.text = "Şehir : $city"
    }
}