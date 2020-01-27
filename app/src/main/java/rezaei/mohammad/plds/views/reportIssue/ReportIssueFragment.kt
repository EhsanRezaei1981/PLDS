package rezaei.mohammad.plds.views.reportIssue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.report_issue_fragment.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import rezaei.mohammad.plds.data.Result
import rezaei.mohammad.plds.data.model.request.DocumentsInfoItem
import rezaei.mohammad.plds.data.model.request.FormResult
import rezaei.mohammad.plds.data.model.response.FormResponse
import rezaei.mohammad.plds.databinding.ReportIssueFragmentBinding
import rezaei.mohammad.plds.formBuilder.ElementParser
import rezaei.mohammad.plds.util.EventObserver
import rezaei.mohammad.plds.util.snack

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupCommonIssueList()
        setupSubmitEvent()
        setupSubmitFormEvent()
    }

    private fun setupCommonIssueList() {
        viewModel.commonIssues.observe(this, Observer {
            (it as? Result.Success)?.let { result ->
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
                            result.response.data?.map {
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
            }
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
                btnSubmit.snack(error.response.errorHandling?.errorMessage)
                viewModel.removeAllDocuments()
                val action = findNavController().graph.startDestination
                findNavController().navigate(action)
            }
            (it as? Result.Error)?.let { error -> btnSubmit.snack(error.errorHandling?.errorMessage) }
        })
    }
}
