package xyz.scoca.helpark.ui.home

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.scoca.helpark.R
import xyz.scoca.helpark.data.local.ClientPreferences
import xyz.scoca.helpark.databinding.FragmentReservationBinding
import xyz.scoca.helpark.model.user.UserInfo
import xyz.scoca.helpark.util.extension.snack
import java.util.*

class ReservationFragment : Fragment() {
    private lateinit var binding : FragmentReservationBinding

    private val parkFee = 20
    private val args : ReservationFragmentArgs by navArgs()
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReservationBinding.inflate(inflater,container,false)
        initListeners()

        binding.btnReservation.setOnClickListener {
            if(ClientPreferences(requireContext()).isRememberMe()){
                reservation()
            }else{
                snack(requireView(),"You must be a member for this process.")
            }

        }
        return binding.root
    }

    private fun initListeners(){
        binding.tvReservationParkName.text = args.parkName
        binding.tvReservationParkFee.text = parkFee.toString()

    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun getDateAndTime(): String{
        val minute = binding.timePicker.minute
        val hour = binding.timePicker.hour
        val day = binding.datePicker.dayOfMonth
        val month = binding.datePicker.month
        val year = binding.datePicker.year

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute)
        return setDateAndTime(calendar.timeInMillis)
    }

    private fun setDateAndTime(time: Long): String {
        val date = Date(time)
        val dateFormat = android.text.format.DateFormat.getLongDateFormat(requireContext())
        val timeFormat = android.text.format.DateFormat.getTimeFormat(requireContext())

        return dateFormat.format(date) + " " + timeFormat.format(date)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun reservation(){
        val dateAndTime = getDateAndTime()
        val parkName = args.parkName
        val email = ClientPreferences(requireContext()).getUserEmail().toString()
        xyz.scoca.helpark.network.NetworkHelper().authService?.postReservation(parkName,email,dateAndTime,parkFee.toFloat())
            ?.enqueue(object : Callback<UserInfo>{
                override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
                    findNavController().navigate(R.id.action_reservationFragment_to_homeFragment)
                    snack(requireView(),"Reservation added.")
                }

                override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                    snack(requireView(),"Reservation Failed.")
                }
            })
    }
}
