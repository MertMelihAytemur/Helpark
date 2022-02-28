package xyz.scoca.helpark.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xyz.scoca.helpark.data.local.ClientPreferences
import xyz.scoca.helpark.databinding.ReservationItemBinding
import xyz.scoca.helpark.model.reservation.ReservationData
import xyz.scoca.helpark.util.OnItemClickListener

class ReservationHistoryAdapter(
    private val context: Context,
    private val reservationList: ArrayList<ReservationData>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<ReservationHistoryAdapter.ViewHolder>() {

    inner class ViewHolder(private var itemBinding: ReservationItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind() {
            val itemPosition = reservationList[adapterPosition]
            itemBinding.tvReservationParkName.text = itemPosition.parkName
            itemBinding.tvReservationDate.text = itemPosition.reservationDate
            itemBinding.tvReservationParkFee.text = itemPosition.fee.toString()
            itemBinding.tvReservationEmail.text = ClientPreferences(context).getUserEmail().toString()

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ReservationItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount() = reservationList.size
}