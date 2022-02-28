package xyz.scoca.helpark.ui.onboarding

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import xyz.scoca.helpark.R
import xyz.scoca.helpark.data.local.ClientPreferences
import xyz.scoca.helpark.databinding.FragmentOnBoardingSecondBinding


class OnBoardingSecond : Fragment() {
    private lateinit var binding : FragmentOnBoardingSecondBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnBoardingSecondBinding.inflate(inflater,container,false)
        activity?.actionBar?.hide()
        binding.btnFinish.setOnClickListener {
            onBoardingDone()
            findNavController().navigate(R.id.action_viewPagerFragment_to_loginFragment)
        }
        return binding.root
    }

    private fun onBoardingDone() {
        val sharedPreferences =
            requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("done", true)
        editor.apply()
    }

}