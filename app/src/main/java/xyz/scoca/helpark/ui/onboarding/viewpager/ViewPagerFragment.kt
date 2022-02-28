package xyz.scoca.helpark.ui.onboarding.viewpager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import xyz.scoca.helpark.R
import xyz.scoca.helpark.databinding.FragmentViewPagerBinding
import xyz.scoca.helpark.ui.onboarding.OnBoardingFirst
import xyz.scoca.helpark.ui.onboarding.OnBoardingSecond


class ViewPagerFragment : Fragment() {
    private lateinit var binding : FragmentViewPagerBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewPagerBinding.inflate(inflater,container,false)

        val fragmentList = arrayListOf<Fragment>(
            OnBoardingFirst(),
            OnBoardingSecond(),
        )
        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )
        binding.viewPager.adapter = adapter
        return binding.root
    }

}