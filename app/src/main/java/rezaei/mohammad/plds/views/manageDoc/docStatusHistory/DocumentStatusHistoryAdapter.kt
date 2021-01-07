package rezaei.mohammad.plds.views.manageDoc.docStatusHistory

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isGone
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.model.response.DocumentStatusHistoryResponse
import rezaei.mohammad.plds.databinding.ItemDocumentStatusHistoryBinding
import rezaei.mohammad.plds.util.BindableAdapter

class DocumentStatusHistoryAdapter(private val viewModel: DocumentStatusHistoryViewModel) :
    ListAdapter<DocumentStatusHistoryResponse.Data,
            SubscriptionViewHolder>(SubscriptionDiffCallback()),
    BindableAdapter<List<DocumentStatusHistoryResponse.Data>> {

    override fun setData(data: List<DocumentStatusHistoryResponse.Data>) {
        submitList(data)
    }

    override fun onBindViewHolder(holder: SubscriptionViewHolder, position: Int) {
        val document = getItem(position)
        holder.bind(viewModel, document)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionViewHolder =
        SubscriptionViewHolder.from(parent)

    companion object {
        /**
         * This diff callback informs the PagedListAdapter how to compute list differences when new
         * PagedLists arrive.
         */
        class SubscriptionDiffCallback :
            DiffUtil.ItemCallback<DocumentStatusHistoryResponse.Data>() {
            override fun areItemsTheSame(
                oldItem: DocumentStatusHistoryResponse.Data,
                newItem: DocumentStatusHistoryResponse.Data
            ): Boolean =
                oldItem.vT == newItem.vT

            override fun areContentsTheSame(
                oldItem: DocumentStatusHistoryResponse.Data,
                newItem: DocumentStatusHistoryResponse.Data
            ): Boolean =
                oldItem == newItem
        }
    }
}

class SubscriptionViewHolder private constructor(private val binding: ItemDocumentStatusHistoryBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        viewModel: DocumentStatusHistoryViewModel,
        document: DocumentStatusHistoryResponse.Data?
    ) {
        binding.document = document
        binding.viewModel = viewModel
        binding.executePendingBindings()

        if (document?.isSuccess == 0)
            binding.btnView.iconTint = ColorStateList.valueOf(
                ResourcesCompat
                    .getColor(binding.btnView.context.resources, R.color.colorFail, null)
            )
        else
            binding.btnView.iconTint = ColorStateList.valueOf(
                ResourcesCompat
                    .getColor(binding.btnView.context.resources, R.color.colorSuccessful, null)
            )

        if (document?.stage == "Start" || document?.stage == "End" || document?.statusId == null) {
            binding.btnModify.isGone = true
            binding.btnView.isGone = true
        } else {
            binding.btnModify.isGone = document.isSuccess == null
            binding.btnView.isGone = document.isSuccess == null
        }
    }

    companion object {
        fun from(parent: ViewGroup): SubscriptionViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemDocumentStatusHistoryBinding.inflate(layoutInflater, parent, false)
            return SubscriptionViewHolder(binding)
        }
    }
}