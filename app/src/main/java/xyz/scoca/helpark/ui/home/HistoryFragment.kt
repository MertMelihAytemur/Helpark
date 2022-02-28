package xyz.scoca.helpark.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.scoca.helpark.data.local.ClientPreferences
import xyz.scoca.helpark.databinding.FragmentHistoryBinding
import xyz.scoca.helpark.model.reservation.Reservation
import xyz.scoca.helpark.model.reservation.ReservationData
import xyz.scoca.helpark.network.NetworkHelper
import xyz.scoca.helpark.ui.home.adapter.ReservationHistoryAdapter
import xyz.scoca.helpark.util.OnItemClickListener
import xyz.scoca.helpark.util.extension.snack


class HistoryFragment : Fragment() {
    private lateinit var binding : FragmentHistoryBinding
    private var reservationList =  ArrayList<ReservationData>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater,container,false)
        getReservationHistory()

        return binding.root
    }

    private fun setRecyclerViewAdapter(reservationList : ArrayList<ReservationData>){
        val mLayoutManager =
            LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.rvReservation.layoutManager = mLayoutManager
        binding.rvReservation.adapter = ReservationHistoryAdapter(requireContext(),reservationList,object :
            OnItemClickListener {
            override fun onClick(position: Int) {}
        })
    }

    private fun getReservationHistory(){
        val email = ClientPreferences(requireContext()).getUserEmail().toString()
        NetworkHelper().authService?.getReservation(email)
            ?.enqueue(object : Callback<Reservation> {
                override fun onResponse(call: Call<Reservation>, response: Response<Reservation>) {
                    binding.progressBar.visibility = View.GONE
                    reservationList = response.body()?.reservation!!
                    setRecyclerViewAdapter(reservationList)
                }
                override fun onFailure(call: Call<Reservation>, t: Throwable) {
                    binding.progressBar.visibility = View.GONE
                    snack(requireView(),"There is no Reservation Found.")
                }
            })
    }
}