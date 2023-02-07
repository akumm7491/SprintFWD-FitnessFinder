package com.test.fitnessstudios.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.test.fitnessstudios.data.models.studio.Studio
import com.test.fitnessstudios.databinding.StudioListItemBinding
import java.math.RoundingMode
import java.text.DecimalFormat

class StudioAdapter : RecyclerView.Adapter<StudioAdapter.StudioViewHolder>() {

    inner class StudioViewHolder(val binding: StudioListItemBinding)  :RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object: DiffUtil.ItemCallback<Studio>(){
        override fun areItemsTheSame(oldItem: Studio, newItem: Studio): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Studio, newItem: Studio): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var studios: List<Studio>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudioViewHolder {
        val binding = StudioListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StudioViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return studios.count()
    }

    private var onItemClickListener: ((Studio) -> Unit)? = null

    fun setOnItemClickListener(listener: (Studio) -> Unit){
        onItemClickListener = listener
    }

    override fun onBindViewHolder(holder: StudioViewHolder, position: Int) {
        val studio = studios[position]
        holder.itemView.apply {
            holder.binding.tvStudioName.text = studio.name
            holder.binding.tvStudioDescription.text = "$$$$ • ${formatDistance(studio.distance)}"
            setOnClickListener{
                onItemClickListener?.let { click ->
                    click(studio)
                }
            }
        }
    }

    private fun formatDistance(distance: Double): String {
        val df = DecimalFormat("#.##").apply {
            roundingMode = RoundingMode.DOWN
        }
        return "${df.format(distance)} miles"
    }


}