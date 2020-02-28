package rezaei.mohammad.plds.views.addMultiDoc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import kotlinx.android.synthetic.main.add_multi_doc_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.model.local.Document
import rezaei.mohammad.plds.data.model.local.DocumentType
import rezaei.mohammad.plds.data.model.response.ErrorHandling
import rezaei.mohammad.plds.databinding.AddMultiDocFragmentBinding
import rezaei.mohammad.plds.util.EventObserver
import rezaei.mohammad.plds.util.snack
import rezaei.mohammad.plds.views.getDocReference.GetDocReferenceFragmentDirections
import rezaei.mohammad.plds.views.main.GlobalViewModel
import rezaei.mohammad.plds.views.reportIssue.ReportIssueFragment
import rezaei.mohammad.plds.views.reportIssue.ReportIssueFragmentDirections

class AddMultiDocFragment : Fragment() {

    companion object {
        const val DOC_TYPE = "DOC_TYPE"
        fun newInstance(docType: DocumentType) = AddMultiDocFragment().apply {
            arguments = Bundle().apply {
                putString(DOC_TYPE, docType.name)
            }
        }
    }

    private val globalViewModel: GlobalViewModel by sharedViewModel()
    private val viewModel: AddMultiDocViewModel by viewModel {
        parametersOf(
            globalViewModel.docRefNo,
            DocumentType.valueOf(arguments?.getString(DOC_TYPE)!!)
        )
    }
    private lateinit var viewDataBinding: AddMultiDocFragmentBinding
    private lateinit var documentAdapter: DocumentAdapter
    // live data of document list for parent fragments access
    lateinit var documentList: LiveData<MutableList<Document>>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.add_multi_doc_fragment, container, false)
        viewDataBinding = AddMultiDocFragmentBinding.bind(root).apply {
            this.viewmodel = this@AddMultiDocFragment.viewModel
        }
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        documentList = viewModel.documentsList
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btnReadQR.setOnClickListener {
            navigateToQrScanner()
        }
        setupRecyclerView()
        setupItemRemover()
        duplicateItemMessage()
        setupRecyclerScroll()
    }

    private fun navigateToQrScanner() {
        val action =
            if (parentFragment is ReportIssueFragment)
                ReportIssueFragmentDirections.actionReportIssueFragmentToQrReaderFragment()
            else
                GetDocReferenceFragmentDirections.actionGetDocReferenceFragmentToQrReaderFragment()
        findNavController().navigate(action)
    }

    private fun setupRecyclerView() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            viewDataBinding.listDocs.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
            documentAdapter = DocumentAdapter(viewModel)
            viewDataBinding.listDocs.adapter = documentAdapter
        }
    }

    private fun setupItemRemover() {
        viewModel.documentRemoveEvent.observe(this, EventObserver {
            listDocs.snack(
                message = ErrorHandling(errorMessage = getString(R.string.item_removed)),
                actionText = getString(R.string.undo),
                action = { viewModel.loadDocumentList() },
                onDismissAction = { viewModel.removeItem(it) },
                duration = 3000
            )
        })
        viewModel.allDocumentsRemoveEvent.observe(this, EventObserver {
            listDocs.snack(
                ErrorHandling(errorMessage = getString(R.string.all_items_deleted)),
                getString(R.string.undo),
                { viewModel.loadDocumentList() },
                { viewModel.clearList() },
                3000
            )
        })
    }

    private fun duplicateItemMessage() {
        viewModel.duplicateDocumentEvent.observe(this, EventObserver {
            viewDataBinding.listDocs.snack(ErrorHandling(errorMessage = getString(R.string.doc_exist)))
        })
    }

    private fun setupRecyclerScroll() {
        viewModel.documentsList.observe(this, Observer {
            listDocs.post {
                listDocs.smoothScrollToPosition(0)
            }
        })
    }

}
