package xyz.scoca.helpark.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import xyz.scoca.helpark.R


class OnBoardingFirst : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.actionBar?.hide()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_on_boarding_first, container, false)
    }

}