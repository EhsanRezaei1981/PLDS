package rezaei.mohammad.plds.views.checkin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import rezaei.mohammad.plds.data.model.response.CheckInResponse
import rezaei.mohammad.plds.databinding.ItemLocationBinding
import rezaei.mohammad.plds.databinding.ItemLocationFooterBinding
import rezaei.mohammad.plds.util.BindableAdapter

class LocationAdapter(private val locationClick: (CheckInResponse.LocationItem?) -> Unit) :
    ListAdapter<CheckInResponse.LocationItem,
            RecyclerView.ViewHolder>(SubscriptionDiffCallback()),
    BindableAdapter<List<CheckInResponse.LocationItem>> {

    override fun getItemCount(): Int {
        return currentList.size.plus(1)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < currentList.size)
            ITEM_TYPE_LOCATION
        else
            ITEM_TYPE_FOOTER
    }

    override fun setData(data: List<CheckInResponse.LocationItem>) {
        submitList(data)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == ITEM_TYPE_LOCATION) {
            val document = getItem(position)
            (holder as ItemViewHolder).bind(document)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder = if (viewType == ITEM_TYPE_LOCATION)
            ItemViewHolder.from(parent)
        else
            FooterViewHolder.from(parent)

        viewHolder.itemView.setOnClickListener {
            locationClick.invoke(
                if (viewType == ITEM_TYPE_LOCATION)
                    currentList[viewHolder.adapterPosition]
                else
                    null
            )
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

        const val ITEM_TYPE_LOCATION = 0
        const val ITEM_TYPE_FOOTER = 1
    }
}

class ItemViewHolder private constructor(private val binding: ItemLocationBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        location: CheckInResponse.LocationItem?
    ) {
        binding.location = location
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): ItemViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemLocationBinding.inflate(layoutInflater, parent, false)
            return ItemViewHolder(binding)
        }
    }
}

class FooterViewHolder private constructor(private val binding: ItemLocationFooterBinding) :
    RecyclerView.ViewHolder(binding.root) {


    companion object {
        fun from(parent: ViewGroup): FooterViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemLocationFooterBinding.inflate(layoutInflater, parent, false)
            return FooterViewHolder(binding)
        }
    }
}