package rezaei.mohammad.plds.views.submitForm

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.submit_form_fragment.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.Result
import rezaei.mohammad.plds.data.model.request.DocumentsInfoItem
import rezaei.mohammad.plds.data.model.request.FormResult
import rezaei.mohammad.plds.data.model.response.CourtResponse
import rezaei.mohammad.plds.data.model.response.FormResponse
import rezaei.mohammad.plds.data.model.response.SheriffResponse
import rezaei.mohammad.plds.databinding.SubmitFormFragmentBinding
import rezaei.mohammad.plds.formBuilder.ElementParser
import rezaei.mohammad.plds.formBuilder.ElementsActivityRequestCallback
import rezaei.mohammad.plds.formBuilder.FileView
import rezaei.mohammad.plds.util.EventObserver
import rezaei.mohammad.plds.util.setActivityTitle
import rezaei.mohammad.plds.util.snack

class SubmitFormFragment : Fragment() {

    private val viewModel: SubmitFormViewModel by viewModel()
    private lateinit var viewDataBinding: SubmitFormFragmentBinding
    private val args: SubmitFormFragmentArgs by navArgs()

    private lateinit var elementParser: ElementParser
    private lateinit var cameraResult: MutableLiveData<Intent>
    private lateinit var courtList: MutableLiveData<List<CourtResponse.Court>>
    private lateinit var sheriffList: MutableLiveData<List<SheriffResponse.Sheriff>>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.submit_form_fragment, container, false)
        viewDataBinding = SubmitFormFragmentBinding.bind(root).apply {
            viewmodel = viewModel
        }
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setActivityTitle("Submit result")
        args.successful?.let { drawForm(it) }
        args.unsuccessful?.let { drawForm(it) }
        setupCourtsSheriffsLoad()
        setupSubmitEvent()
        setupSubmitFormEvent()
    }

    private fun drawForm(formResponse: FormResponse) {
        elementParser = ElementParser(this,
            formResponse.data, viewDataBinding.layoutContainer, object :
                ElementsActivityRequestCallback {
                override fun requestPermission(permission: String) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(arrayOf(permission), 123)
                    }
                }

                override fun onPhotoTaken(result: MutableLiveData<Intent>) {
                    cameraResult = result
                }

                override fun courtListNeeded(courtList: MutableLiveData<List<CourtResponse.Court>>) {
                    this@SubmitFormFragment.courtList = courtList
                    viewModel.getCourts()
                }

                override fun sheriffListNeeded(sheriffList: MutableLiveData<List<SheriffResponse.Sheriff>>) {
                    this@SubmitFormFragment.sheriffList = sheriffList
                    viewModel.getSheriffs()
                }
            })
    }

    private fun setupCourtsSheriffsLoad() {
        viewModel.getCourtsEvent.observe(this, EventObserver {
            (it as? Result.Success)?.let { courtList.value = it.response.data }
            (it as? Result.Error)?.let { error -> btnSubmit.snack(error.errorHandling) }
        })
        viewModel.getSheriffsEvent.observe(this, EventObserver {
            (it as? Result.Success)?.let { sheriffList.value = it.response.data }
            (it as? Result.Error)?.let { error -> btnSubmit.snack(error.errorHandling) }
        })
    }

    private fun setupSubmitEvent() {
        viewModel.submitEvent.observe(this, EventObserver {
            if (elementParser.isItemsValid()) {
                MainScope().launch {
                    val formResult = FormResult().apply {
                        val documents = mutableListOf<DocumentsInfoItem>()
                        viewModel.getDocumentList().forEach {
                            documents.add(DocumentsInfoItem(it.docRefNo))
                        }
                        this.documentsInfo = documents
                        //set type
                        if (args.successful != null)
                            this.responseType = "Successful"
                        else
                            this.responseType = "Unsuccessful"
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
                btnSubmit.snack(error.response.errorHandling, onDismissAction = {
                    val action = findNavController().graph.startDestination
                    findNavController().navigate(action)
                })
                viewModel.removeAllDocuments()
            }
            (it as? Result.Error)?.let { error -> btnSubmit.snack(error.errorHandling) }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == FileView.cameraRequest)
            cameraResult.value = data
    }

}
