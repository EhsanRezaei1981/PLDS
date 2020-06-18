package rezaei.mohammad.plds.views.reportIssue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.report_issue_fragment.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.Result
import rezaei.mohammad.plds.data.model.local.DocumentType
import rezaei.mohammad.plds.data.model.request.DocumentsInfoItem
import rezaei.mohammad.plds.data.model.request.FormResult
import rezaei.mohammad.plds.data.model.response.ErrorHandling
import rezaei.mohammad.plds.data.model.response.FormResponse
import rezaei.mohammad.plds.databinding.ReportIssueFragmentBinding
import rezaei.mohammad.plds.formBuilder.ElementParser
import rezaei.mohammad.plds.util.EventObserver
import rezaei.mohammad.plds.util.setActivityTitle
import rezaei.mohammad.plds.util.snack
import rezaei.mohammad.plds.views.addMultiDoc.AddMultiDocFragment
import rezaei.mohammad.plds.views.main.MainActivity

class ReportIssueFragment : Fragment() {

    private val viewModel: ReportIssueViewModel by viewModel()
    private lateinit var viewDataBinding: ReportIssueFragmentBinding
    private lateinit var elementParser: ElementParser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding =
            ReportIssueFragmentBinding.inflate(layoutInflater, container, false).apply {
                viewmodel = viewModel
            }
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActivityTitle(getString(R.string.report_issue))
        addMoreDocFragment()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupCommonIssueList()
        setupSubmitEvent()
        setupSubmitFormEvent()
    }

    private fun addMoreDocFragment() {
        if (childFragmentManager.findFragmentByTag("AddDoc") == null)
            childFragmentManager.beginTransaction()
                .replace(
                    multiAddDoc.id,
                    AddMultiDocFragment.newInstance(DocumentType.ReportIssue),
                    "AddDoc"
                )
                .runOnCommit {
                    setupDocumentListObserver()
                }
                .commit()
        else
            childFragmentManager.registerFragmentLifecycleCallbacks(object :
                FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentActivityCreated(
                    fm: FragmentManager,
                    f: Fragment,
                    savedInstanceState: Bundle?
                ) {
                    super.onFragmentActivityCreated(fm, f, savedInstanceState)
                    if (f is AddMultiDocFragment)
                        setupDocumentListObserver()
                }
            }, false)


    }

    private fun setupCommonIssueList() {
        viewModel.commonIssues.observe(this, Observer {
            (it as? Result.Success)?.let { result ->
                viewDataBinding.layoutContainer.removeAllViews()
                if (result.response.data?.isNotEmpty() == true) {
                    elementParser = ElementParser(
                        this,
                        listOf(
                            //create date picker
                            FormResponse.DataItem(
                                1,
                                "Date",
                                "Date"
                                //create reason spinner
                            ), FormResponse.DataItem(
                                1,
                                "Reason",
                                "List",
                                result.response.data.map {
                                    FormResponse.ListItem(
                                        it.description,
                                        "Issue",
                                        it.commentIsNeeded,
                                        it.listId,
                                        it.gPSIsNeeded
                                    )
                                })
                        ),
                        viewDataBinding.layoutContainer,
                        null
                    )
                } else {
                    btnSubmit.snack(ErrorHandling(errorMessage = getString(R.string.no_data_for_docs)))
                }
            }
            (it as? Result.Error)?.let { error -> btnSubmit.snack(error.errorHandling) }
        })
    }

    private fun setupSubmitEvent() {
        viewModel.submitEvent.observe(this, EventObserver {
            if (elementParser.isItemsValid()) {
                MainScope().launch {
                    val formResult = FormResult().apply {
                        val documents = mutableListOf<DocumentsInfoItem>()
                        //ad document ref nos to response
                        viewModel.getDocumentList().forEach {
                            documents.add(DocumentsInfoItem(it.docRefNo))
                        }
                        this.documentsInfo = documents
                        //set type
                        this.responseType = "ReportIssue"
                    }
                    val result = elementParser.getResult(formResult)
                    viewModel.submitForm(result)
                }
            }
        })
    }

    private fun setupSubmitFormEvent() {
        viewModel.submitFormEvent.observe(this, EventObserver {
            (it as? Result.Success)?.let { error ->
                btnSubmit.snack(
                    error.response.errorHandling,
                    onDismissAction = { findNavController().popBackStack() })
                viewModel.removeAllDocuments()
            }
            (it as? Result.Error)?.let { error -> btnSubmit.snack(error.errorHandling) }
        })
    }

    private fun setupDocumentListObserver() {
        (childFragmentManager.findFragmentById(R.id.multiAddDoc) as AddMultiDocFragment)
            .documentList.observe(this, Observer {
                //if common issue list was empty, load it again on document changes
                if (viewDataBinding.layoutContainer.childCount == 0 && it.isNotEmpty())
                    viewModel.getCommonIssues()
                else if (it.isEmpty()) {
                    viewDataBinding.layoutContainer.removeAllViews()
                    viewModel.dataExist.value = false
                }
                //show note if there is multiple doc in list
                with((requireActivity() as MainActivity)) {
                    if (it.size > 1)
                        showNote()
                    else
                        hideNote()
                }
            })
    }
}
