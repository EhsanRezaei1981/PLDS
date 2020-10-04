package rezaei.mohammad.plds.views.checkin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import rezaei.mohammad.plds.data.model.response.CheckInResponse
import rezaei.mohammad.plds.databinding.ItemLocationBinding
import rezaei.mohammad.plds.util.BindableAdapter

class LocationAdapter(private val locationClick: (CheckInResponse.LocationItem) -> Unit) :
    ListAdapter<CheckInResponse.LocationItem,
            SubscriptionViewHolder>(SubscriptionDiffCallback()),
    BindableAdapter<List<CheckInResponse.LocationItem>> {

    override fun setData(data: List<CheckInResponse.LocationItem>) {
        submitList(data)
    }

    override fun onBindViewHolder(holder: SubscriptionViewHolder, position: Int) {
        val document = getItem(position)
        holder.bind(document)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionViewHolder {
        val viewHolder = SubscriptionViewHolder.from(parent)
        viewHolder.itemView.setOnClickListener {
            locationClick.invoke(currentList[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    companion object {
        /**
         * This diff callback informs the PagedListAdapter how to compute list differences when new
         * PagedLists arrive.
         */
        class SubscriptionDiffCallback :
            DiffUtil.ItemCallback<CheckInResponse.LocationItem>() {
            override fun areItemsTheSame(
                oldItem: CheckInResponse.LocationItem,
                newItem: CheckInResponse.LocationItem
            ): Boolean =
                oldItem.locationId == newItem.locationId

            override fun areContentsTheSame(
                oldItem: CheckInResponse.LocationItem,
                newItem: CheckInResponse.LocationItem
            ): Boolean =
                oldItem == newItem
        }
    }
}

class SubscriptionViewHolder private constructor(private val binding: ItemLocationBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        location: CheckInResponse.LocationItem?
    ) {
        binding.location = location
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): SubscriptionViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemLocationBinding.inflate(layoutInflater, parent, false)
            return SubscriptionViewHolder(binding)
        }
    }
}