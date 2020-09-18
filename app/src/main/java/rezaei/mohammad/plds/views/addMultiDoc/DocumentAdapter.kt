package rezaei.mohammad.plds.views.addMultiDoc


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import rezaei.mohammad.plds.data.model.local.Document
import rezaei.mohammad.plds.databinding.DocumentItemBinding
import rezaei.mohammad.plds.util.BindableAdapter


class DocumentAdapter(private val viewModel: AddMultiDocViewModel) :
    ListAdapter<Document, SubscriptionViewHolder>(SubscriptionDiffCallback()),
    BindableAdapter<List<Document>> {

    override fun setData(data: List<Document>) {
        submitList(data)
    }

    override fun onBindViewHolder(holder: SubscriptionViewHolder, position: Int) {
        val document = getItem(position)
        document.positionInList = position.plus(1).toString()
        holder.bind(viewModel, document)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionViewHolder =
        SubscriptionViewHolder.from(parent)

    companion object {
        /**
         * This diff callback informs the PagedListAdapter how to compute list differences when new
         * PagedLists arrive.
         */
        class SubscriptionDiffCallback : DiffUtil.ItemCallback<Document>() {
            override fun areItemsTheSame(oldItem: Document, newItem: Document): Boolean =
                oldItem.docRefNo == newItem.docRefNo

            override fun areContentsTheSame(oldItem: Document, newItem: Document): Boolean =
                oldItem == newItem
        }
    }
}

class SubscriptionViewHolder private constructor(private val binding: DocumentItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(viewModel: AddMultiDocViewModel, document: Document?) {
        binding.document = document
        binding.viewmodel = viewModel
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): SubscriptionViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = DocumentItemBinding.inflate(layoutInflater, parent, false)
            return SubscriptionViewHolder(binding)
        }
    }
}