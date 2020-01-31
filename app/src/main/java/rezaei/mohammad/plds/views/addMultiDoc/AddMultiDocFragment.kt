package rezaei.mohammad.plds.views.addMultiDoc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import kotlinx.android.synthetic.main.add_multi_doc_fragment.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.model.local.Document
import rezaei.mohammad.plds.data.model.local.DocumentType
import rezaei.mohammad.plds.data.model.response.ErrorHandling
import rezaei.mohammad.plds.databinding.AddMultiDocFragmentBinding
import rezaei.mohammad.plds.util.EventObserver
import rezaei.mohammad.plds.util.snack
import rezaei.mohammad.plds.views.docProgress.DocProgressFragmentDirections
import rezaei.mohammad.plds.views.main.GlobalViewModel
import rezaei.mohammad.plds.views.reportIssue.ReportIssueFragment
import rezaei.mohammad.plds.views.reportIssue.ReportIssueFragmentDirections

class AddMultiDocFragment : Fragment() {

    private val docType: Lazy<DocumentType> = lazy {
        if (parentFragment is ReportIssueFragment)
            DocumentType.ReportIssue
        else
            DocumentType.CheckProgress
    }
    private val globalViewModel: GlobalViewModel by sharedViewModel()
    private val viewModel: AddMultiDocViewModel by inject {
        parametersOf(
            globalViewModel.docRefNo,
            docType.value
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
    }

    private fun navigateToQrScanner() {
        val action =
            if (parentFragment is ReportIssueFragment)
                ReportIssueFragmentDirections.actionReportIssueFragmentToQrReaderFragment()
            else
                DocProgressFragmentDirections.actionDocProgressFragmentToQrReaderFragment()
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
                message = ErrorHandling(errorMessage = "Item removed."),
                actionText = "UNDO",
                action = { viewModel.loadDocumentList() },
                onDismissAction = { viewModel.removeItem(it) },
                duration = 3000
            )
        })
        viewModel.allDocumentsRemoveEvent.observe(this, EventObserver {
            listDocs.snack(
                ErrorHandling(errorMessage = "All items deleted."),
                "UNDO",
                { viewModel.loadDocumentList() },
                { viewModel.clearList() },
                3000
            )
        })
    }

    private fun duplicateItemMessage() {
        viewModel.duplicateDocumentEvent.observe(this, EventObserver {
            viewDataBinding.listDocs.snack(ErrorHandling(errorMessage = "Document already exist."))
        })
    }

}
