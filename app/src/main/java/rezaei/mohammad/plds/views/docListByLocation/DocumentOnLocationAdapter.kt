package rezaei.mohammad.plds.views.docListByLocation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import rezaei.mohammad.plds.data.model.response.DocumentOnLocationResponse
import rezaei.mohammad.plds.databinding.ItemDocumentBasedOnLocationBinding
import rezaei.mohammad.plds.util.BindableAdapter

class DocumentOnLocationAdapter(private val docListByLocationViewModel: DocListByLocationViewModel) :
    ListAdapter<DocumentOnLocationResponse.Data,
            SubscriptionViewHolder>(SubscriptionDiffCallback()),
    BindableAdapter<List<DocumentOnLocationResponse.Data>> {

    override fun setData(data: List<DocumentOnLocationResponse.Data>) {
        submitList(data)
    }

    override fun onBindViewHolder(holder: SubscriptionViewHolder, position: Int) {
        val document = getItem(position)
        holder.bind(document, docListByLocationViewModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionViewHolder {
        return SubscriptionViewHolder.from(parent)
    }

    companion object {
        /**
         * This diff callback informs the PagedListAdapter how to compute list differences when new
         * PagedLists arrive.
         */
        class SubscriptionDiffCallback :
            DiffUtil.ItemCallback<DocumentOnLocationResponse.Data>() {
            override fun areItemsTheSame(
                oldItem: DocumentOnLocationResponse.Data,
                newItem: DocumentOnLocationResponse.Data
            ): Boolean =
                oldItem.documentReferenceNo == newItem.documentReferenceNo

            override fun areContentsTheSame(
                oldItem: DocumentOnLocationResponse.Data,
                newItem: DocumentOnLocationResponse.Data
            ): Boolean =
                oldItem == newItem
        }
    }
}

class SubscriptionViewHolder private constructor(private val binding: ItemDocumentBasedOnLocationBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        document: DocumentOnLocationResponse.Data?,
        docListByLocationViewModel: DocListByLocationViewModel
    ) {
        binding.document = document
        binding.viewmodel = docListByLocationViewModel
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): SubscriptionViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemDocumentBasedOnLocationBinding.inflate(layoutInflater, parent, false)
            return SubscriptionViewHolder(binding)
        }
    }
}