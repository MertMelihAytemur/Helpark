package xyz.scoca.helpark.ui.home.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import xyz.scoca.helpark.R
import xyz.scoca.helpark.databinding.ParkItemBinding
import xyz.scoca.helpark.model.park.ParkData
import xyz.scoca.helpark.util.OnItemClickListener
import java.io.PipedReader

class ParkDataAdapter(
    private val context : Context,
    private val parkList: ArrayList<ParkData>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<ParkDataAdapter.ViewHolder>() {

    inner class ViewHolder(private val itemBinding: ParkItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind() {
            val itemPosition = parkList[adapterPosition]

            //itemBinding.tvParkId.text = itemPosition.parkId
            itemBinding.tvParkName.text = itemPosition.parkName
            itemBinding.tvParkLocationCity.text = itemPosition.parkLocationCity
            itemBinding.tvParkCarCount.text = itemPosition.parkCarCount
            itemBinding.tvParkLocationCounty.text = itemPosition.parkLocationCounty
            itemBinding.tvParkActiveHours.text = itemPosition.parkActiveHours
            itemBinding.tvParkPoint.text = itemPosition.parkPoint
            itemBinding.tvParkFee.text = itemPosition.parkFee
            itemBinding.tvParkMaxCarCount.text = itemPosition.parkMaxCarCount


            itemBinding.btnReservation.setOnClickListener {
                onItemClickListener.onClick(adapterPosition)
            }

            itemBinding.cardView.setOnClickListener {
                if (itemBinding.hiddenLayout.visibility == View.GONE) {
                    TransitionManager.beginDelayedTransition(
                        itemBinding.hiddenLayout,
                        AutoTransition()
                    )
                    itemBinding.hiddenLayout.visibility = View.VISIBLE
                    itemBinding.ivAngelUp.setImageResource(R.drawable.ic_arrow_down)
                } else {
                    TransitionManager.beginDelayedTransition(
                        itemBinding.hiddenLayout,
                        AutoTransition()
                    )
                    itemBinding.hiddenLayout.visibility = View.GONE
                    itemBinding.ivAngelUp.setImageResource(R.drawable.ic_arrow_up)
                }
            }

            when (calculateDensity(
                itemPosition.parkCarCount!!.toDouble(),
                itemPosition.parkMaxCarCount.toDouble()
            )) {
                in 0.0..33.3 -> {
                    itemBinding.ivCarDensity.setImageResource(R.drawable.car_density_green)
                    itemBinding.tvParkCarCount.setTextColor(ContextCompat.getColor(context,R.color.spotify_green))
                }
                in 33.3..66.6 -> {
                    itemBinding.ivCarDensity.setImageResource(R.drawable.car_density_orange)
                    itemBinding.tvParkCarCount.setTextColor(ContextCompat.getColor(context,R.color.carrot_orange))
                }
                in 66.6..100.0 -> {
                    itemBinding.ivCarDensity.setImageResource(R.drawable.car_density_red)
                    itemBinding.tvParkCarCount.setTextColor(ContextCompat.getColor(context,R.color.red))
                }
                else -> {
                    itemBinding.ivCarDensity.setImageResource(R.drawable.car_density_red)
                    itemBinding.tvParkCarCount.setTextColor(ContextCompat.getColor(context,R.color.red))
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ParkDataAdapter.ViewHolder {
        val itemBinding =
            ParkItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ParkDataAdapter.ViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount() = parkList.size

    private fun calculateDensity(current: Double, total: Double): Double {
        return (current / total) * 100
    }
}