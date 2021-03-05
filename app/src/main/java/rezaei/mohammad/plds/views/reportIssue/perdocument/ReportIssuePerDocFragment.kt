package rezaei.mohammad.plds.views.reportIssue.perdocument

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_add_multi_doc.*
import kotlinx.android.synthetic.main.fragment_report_issue.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.ApiResult
import rezaei.mohammad.plds.data.model.local.DocumentType
import rezaei.mohammad.plds.data.model.request.DocumentsInfoItem
import rezaei.mohammad.plds.data.model.request.FormResult
import rezaei.mohammad.plds.data.model.request.GetDocumentsOnLocationRequest
import rezaei.mohammad.plds.data.model.request.GetFileRequest
import rezaei.mohammad.plds.data.model.response.CourtResponse
import rezaei.mohammad.plds.data.model.response.ErrorHandling
import rezaei.mohammad.plds.data.model.response.FormResponse
import rezaei.mohammad.plds.data.model.response.SheriffResponse
import rezaei.mohammad.plds.databinding.FragmentReportIssueBinding
import rezaei.mohammad.plds.formBuilder.ElementParser
import rezaei.mohammad.plds.formBuilder.ElementsActivityRequestCallback
import rezaei.mohammad.plds.util.EventObserver
import rezaei.mohammad.plds.util.setActivityTitle
import rezaei.mohammad.plds.util.snack
import rezaei.mohammad.plds.util.tryNavigate
import rezaei.mohammad.plds.views.addMultiDoc.AddMultiDocFragment
import rezaei.mohammad.plds.views.main.GlobalViewModel
import rezaei.mohammad.plds.views.main.MainActivity

class ReportIssuePerDocFragment : Fragment() {

    private val perDocViewModel: ReportIssuePerDocViewModel by viewModel()
    private val globalViewModel: GlobalViewModel by sharedViewModel()
    private lateinit var imageResult: MutableLiveData<Intent>
    private lateinit var viewDataBinding: FragmentReportIssueBinding
    private lateinit var elementParser: ElementParser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding =
            FragmentReportIssueBinding.inflate(layoutInflater, container, false).apply {
                viewmodel = perDocViewModel
            }
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActivityTitle(getString(R.string.report_issue))
        if (arguments?.containsKey("FormResponse") == true)
            perDocViewModel.getCommonIssues(globalViewModel.docRefNo.value)
        else
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
                    setupMultiDocFragmentInteractor()
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
                        setupMultiDocFragmentInteractor()
                }
            }, false)


    }

    private fun setupCommonIssueList() {
        perDocViewModel.commonIssues.observe(this.viewLifecycleOwner, Observer {
            (it as? ApiResult.Success)?.let { result ->
                viewDataBinding.layoutContainer.removeAllViews()
                if (result.response.data?.isNotEmpty() == true) {
                    val argument = arguments?.getParcelable<FormResponse.DataItem>("FormResponse")
                    val fileRequest = arguments?.getParcelable<GetFileRequest>("FileRequest")
                    val dateValue = argument?.date
                    val reasonId = argument?.commonIssue?.commonIssueId
                    val reasonValue = argument?.commonIssue?.commonIssue
                    val reasonComment = argument?.commonIssue?.commentValue
                    val chosenFile = argument?.commonIssue?.chosenFile
                    elementParser = ElementParser(
                        this,
                        listOf(
                            //create date picker
                            FormResponse.DataItem(
                                1,
                                "Date",
                                "Date",
                                value = FormResponse.Value(reply = dateValue)
                                //create reason spinner
                            ), FormResponse.DataItem(
                                1,
                                "Reason",
                                "List",
                                value = FormResponse.Value(
                                    listSelectedId = reasonId,
                                    listSelectedText = reasonValue,
                                    listComment = reasonComment
                                ),
                                list = result.response.data.map {
                                    FormResponse.ListItem(
                                        it.description,
                                        "Issue",
                                        it.commentIsNeeded,
                                        it.listId,
                                        it.gPSIsNeeded
                                    )
                                }),
                            FormResponse.DataItem(
                                isMandatory = 0,
                                dataType = "File",
                                label = "Image",
                                value = FormResponse.Value(
                                    extension = chosenFile?.extension,
                                    fileId = chosenFile?.fileId,
                                    VTFileId = chosenFile?.VTFileId
                                ),
                                dataTypeSetting = FormResponse.DataTypeSetting(
                                    FormResponse.File(
                                        cameraIsNeeded = true,
                                        isFileBrowserNeeded = true
                                    )
                                )
                            )
                        ),
                        viewDataBinding.layoutContainer,
                        object :
                            ElementsActivityRequestCallback {
                            override fun requestPermission(permission: String) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestPermissions(arrayOf(permission), 123)
                                }
                            }

                            override fun onImageSelected(result: MutableLiveData<Intent>) {
                                imageResult = result
                            }

                            override fun courtListNeeded(courtList: MutableLiveData<List<CourtResponse.Court>>) {
                            }

                            override fun sheriffListNeeded(sheriffList: MutableLiveData<List<SheriffResponse.Sheriff>>) {
                            }

                            override fun onPreviewImageClicked(
                                fileId: Int?,
                                fileVT: String?,
                                base64: String?
                            ) {
                                findNavController().tryNavigate(
                                    ReportIssuePerDocFragmentDirections
                                        .actionReportIssuePerDocFragmentToImageViewerFragment(
                                            base64 = base64,
                                            getFileRequest = fileRequest?.also {
                                                it.fileId = fileId
                                                it.vTFileId = fileVT
                                            }
                                        )
                                )
                            }
                        }
                    )
                } else {
                    btnSubmit.snack(ErrorHandling(errorMessage = getString(R.string.no_data_for_docs)))
                }
            }
            (it as? ApiResult.Error)?.let { error -> btnSubmit.snack(error.errorHandling) }
        })
    }

    private fun setupSubmitEvent() {
        perDocViewModel.submitEvent.observe(this.viewLifecycleOwner, EventObserver {
            if (elementParser.isItemsValid()) {
                MainScope().launch {
                    if (arguments?.containsKey("FormResponse") == false) {
                        val formResult = FormResult.DocumentProgress().apply {
                            val documents = mutableListOf<DocumentsInfoItem>()
                            //ad document ref nos to response
                            perDocViewModel.getDocumentList().forEach {
                                documents.add(DocumentsInfoItem(it.docRefNo))
                            }
                            this.documentsInfo = documents
                            //set type
                            this.responseType = "ReportIssue"
                        }
                        val result = elementParser.getResult(formResult)
                        perDocViewModel.submitForm(result as FormResult.DocumentProgress)
                    } else {
                        val formResult = FormResult.DocumentProgress().apply {
                            this.lastUpdateDateTime = requireArguments().getString("lastUpdateTime")
                            //set type
                            this.responseType = "ReportIssue"
                        }
                        val result = elementParser.getResult(
                            formResult,
                            documentStatusQueryId = requireArguments().getInt("DocumentStatusQueryId"),
                            vt = requireArguments().getString("VT")
                        )
                        perDocViewModel.updateRespondedField(result as FormResult.DocumentProgress)
                    }
                }
            }
        })
    }

    private fun setupSubmitFormEvent() {
        perDocViewModel.submitFormEvent.observe(this.viewLifecycleOwner, EventObserver {
            (it as? ApiResult.Success)?.let { error ->
                btnSubmit.snack(
                    error.response.errorHandling,
                    onDismissAction = {
                        if (isAdded)
                            findNavController().popBackStack()
                    })
                perDocViewModel.removeAllDocuments()
            }
            (it as? ApiResult.Error)?.let { error -> btnSubmit.snack(error.errorHandling) }
        })
    }

    private fun setupMultiDocFragmentInteractor() {
        with((childFragmentManager.findFragmentById(R.id.multiAddDoc) as AddMultiDocFragment)) {
            //setDocumentListObserver
            documentList.observe(this.viewLifecycleOwner, Observer {
                //if common issue list was empty, load it again on document changes
                if (viewDataBinding.layoutContainer.childCount == 0 && it.isNotEmpty())
                    perDocViewModel.getCommonIssues()
                else if (it.isEmpty()) {
                    viewDataBinding.layoutContainer.removeAllViews()
                    perDocViewModel.dataExist.value = false
                }
                //show note if there is multiple doc in list
                with((requireActivity() as MainActivity)) {
                    if (it.size > 1)
                        showNote()
                    else
                        hideNote()
                }
            })

            //setOnDocumentListButtonClickListener
            this.btnDocumentList.setOnClickListener {
                val location = (requireActivity() as MainActivity).checkInService?.checkedInLocation
                findNavController().navigate(
                    ReportIssuePerDocFragmentDirections
                        .actionReportIssuePerDocFragmentToDocListByLocationFragment(
                            GetDocumentsOnLocationRequest(
                                location?.locationId,
                                location?.vTLocationId,
                                location?.vTLocation,
                                location?.uTPId,
                                location?.vTUTPId,
                                location?.locationType
                            )
                        )
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            imageResult.value = data
        }
    }
}
