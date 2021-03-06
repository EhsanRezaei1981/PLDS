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
import kotlinx.android.synthetic.main.fragment_add_multi_doc.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.model.local.Document
import rezaei.mohammad.plds.data.model.local.DocumentType
import rezaei.mohammad.plds.data.model.response.ErrorHandling
import rezaei.mohammad.plds.databinding.FragmentAddMultiDocBinding
import rezaei.mohammad.plds.util.EventObserver
import rezaei.mohammad.plds.util.snack
import rezaei.mohammad.plds.util.tryNavigate
import rezaei.mohammad.plds.views.getDocReference.GetDocReferenceFragmentDirections
import rezaei.mohammad.plds.views.main.GlobalViewModel
import rezaei.mohammad.plds.views.reportIssue.perdocument.ReportIssuePerDocFragment
import rezaei.mohammad.plds.views.reportIssue.perdocument.ReportIssuePerDocFragmentDirections

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
    private lateinit var viewDataBinding: FragmentAddMultiDocBinding
    private lateinit var documentAdapter: DocumentAdapter
    // live data of document list for parent fragments access
    lateinit var documentList: LiveData<MutableList<Document>>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_add_multi_doc, container, false)
        viewDataBinding = FragmentAddMultiDocBinding.bind(root).apply {
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
        viewModel.loadDocumentList()
    }

    private fun navigateToQrScanner() {
        val action =
            if (parentFragment is ReportIssuePerDocFragment)
                ReportIssuePerDocFragmentDirections.actionReportIssueFragmentToQrReaderFragment()
            else
                GetDocReferenceFragmentDirections.actionGetDocReferenceFragmentToQrReaderFragment()
        findNavController().tryNavigate(action)
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
        viewModel.documentRemoveEvent.observe(this.viewLifecycleOwner, EventObserver {
            listDocs?.snack(
                message = ErrorHandling(errorMessage = getString(R.string.item_removed)),
                actionText = getString(R.string.undo),
                action = { viewModel.loadDocumentList() },
                onDismissAction = { viewModel.removeItem(it) },
                duration = 3000
            )
        })
        viewModel.allDocumentsRemoveEvent.observe(this.viewLifecycleOwner, EventObserver {
            listDocs?.snack(
                ErrorHandling(errorMessage = getString(R.string.all_items_deleted)),
                getString(R.string.undo),
                { viewModel.loadDocumentList() },
                { viewModel.clearList() },
                3000
            )
        })
    }

    private fun duplicateItemMessage() {
        viewModel.duplicateDocumentEvent.observe(this.viewLifecycleOwner, EventObserver {
            viewDataBinding.listDocs.snack(ErrorHandling(errorMessage = getString(R.string.doc_exist)))
        })
    }

    private fun setupRecyclerScroll() {
        viewModel.documentsList.observe(this.viewLifecycleOwner, Observer {
            listDocs?.post {
                listDocs?.smoothScrollToPosition(0)
            }
        })
    }

}
