package xyz.scoca.helpark.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.scoca.helpark.R
import xyz.scoca.helpark.data.local.ClientPreferences
import xyz.scoca.helpark.databinding.FragmentFeedbackBinding
import xyz.scoca.helpark.model.user.UserInfo
import xyz.scoca.helpark.network.NetworkHelper
import xyz.scoca.helpark.util.extension.snack


class FeedbackFragment : Fragment() {
    private lateinit var binding : FragmentFeedbackBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedbackBinding.inflate(inflater,container,false)

        initListener()
        binding.btnFeedback.setOnClickListener {
            feedBack()
        }
        return binding.root
    }
    private fun feedBack(){
        val message = binding.etFeedback.text.toString()
        val email = ClientPreferences(requireContext()).getUserEmail().toString()

        NetworkHelper().authService?.getFeedback(message,email)
            ?.enqueue(object : Callback<UserInfo>{
                override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
                    snack(requireView(),"Feedback sent successfully.")
                    findNavController().navigate(R.id.action_feedbackFragment_to_homeFragment)
                }
                override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                    snack(requireView(),"Error occurred.")
                }
            })
    }

    private fun initListener(){
        ClientPreferences(requireContext()).getUserEmail().let {
            binding.etFeedbackEmail.setText(it)
        }
    }

}