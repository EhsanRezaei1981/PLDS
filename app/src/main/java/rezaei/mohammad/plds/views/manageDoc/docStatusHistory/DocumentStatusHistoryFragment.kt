package rezaei.mohammad.plds.views.manageDoc.docStatusHistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.koin.androidx.viewmodel.ext.android.viewModel
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.model.response.DocumentStatusHistoryResponse
import rezaei.mohammad.plds.databinding.FragmentDocumentStatusHistoryBinding
import rezaei.mohammad.plds.util.EventObserver
import rezaei.mohammad.plds.util.setActivityTitle
import rezaei.mohammad.plds.util.snack
import rezaei.mohammad.plds.util.tryNavigate

class DocumentStatusHistoryFragment : Fragment() {

    private val viewModel: DocumentStatusHistoryViewModel by viewModel()
    private lateinit var viewDataBinding: FragmentDocumentStatusHistoryBinding
    private val args: DocumentStatusHistoryFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setActivityTitle(getString(R.string.document_status_history))
        viewDataBinding = FragmentDocumentStatusHistoryBinding.inflate(inflater, container, false)
            .apply {
                this.viewmodel = viewModel
                this.lifecycleOwner = this@DocumentStatusHistoryFragment.viewLifecycleOwner
            }
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupRecyclerView()
        setErrorHandler()
        setOnButtonsClickHandler()
        if (viewModel.documentsStatusHistoryList.value == null)
            viewModel.getDocumentStatusHistory(args.documentBaseInfo)
    }

    private fun setupRecyclerView() {
        viewDataBinding.listDocs.adapter = DocumentStatusHistoryAdapter(viewModel)
    }

    private fun setErrorHandler() {
        viewModel.getDocumentsStatusHistoryError.observe(this.viewLifecycleOwner, EventObserver {
            viewDataBinding.listDocs.snack(it.errorHandling)
        })
    }

    private fun setOnButtonsClickHandler() {
        viewModel.modifyClickEvent.observe(this.viewLifecycleOwner, EventObserver {
            openModifyDocFragment(it, false)
        })
        viewModel.viewClickEvent.observe(this.viewLifecycleOwner, EventObserver {
            openModifyDocFragment(it, true)
        })
    }

    private fun openModifyDocFragment(doc: DocumentStatusHistoryResponse.Data, readOnly: Boolean) {
        val action =
            DocumentStatusHistoryFragmentDirections.actionDocumentStatusHistoryFragmentToEditDocumentFragment(
                doc.documentStatusId!!,
                doc.vT!!, args.documentBaseInfo, readOnly, doc.gPSIsNeeded == 1,
                if (doc.isSuccess == 1) "Success" else "Query"
            )
        findNavController().tryNavigate(action)
    }


}