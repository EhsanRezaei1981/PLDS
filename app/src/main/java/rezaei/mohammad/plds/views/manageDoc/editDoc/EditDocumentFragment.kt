package rezaei.mohammad.plds.views.manageDoc.editDoc

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.yayandroid.locationmanager.LocationManager
import com.yayandroid.locationmanager.configuration.DefaultProviderConfiguration
import com.yayandroid.locationmanager.configuration.GooglePlayServicesConfiguration
import com.yayandroid.locationmanager.configuration.LocationConfiguration
import com.yayandroid.locationmanager.configuration.PermissionConfiguration
import com.yayandroid.locationmanager.listener.LocationListener
import kotlinx.android.synthetic.main.fragment_edit_document.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.ApiResult
import rezaei.mohammad.plds.data.model.request.FormResult
import rezaei.mohammad.plds.data.model.request.GetFileRequest
import rezaei.mohammad.plds.data.model.request.Gps
import rezaei.mohammad.plds.data.model.response.CourtResponse
import rezaei.mohammad.plds.data.model.response.FormResponse
import rezaei.mohammad.plds.data.model.response.SheriffResponse
import rezaei.mohammad.plds.databinding.FragmentEditDocumentBinding
import rezaei.mohammad.plds.formBuilder.ElementParser
import rezaei.mohammad.plds.formBuilder.ElementsActivityRequestCallback
import rezaei.mohammad.plds.formBuilder.FileView
import rezaei.mohammad.plds.util.EventObserver
import rezaei.mohammad.plds.util.snack
import rezaei.mohammad.plds.util.tryNavigate

class EditDocumentFragment : Fragment() {

    private val viewModel: EditDocumentViewModel by viewModel()
    private lateinit var viewDataBinding: FragmentEditDocumentBinding
    private val args: EditDocumentFragmentArgs by navArgs()
    private lateinit var elementParser: ElementParser
    private lateinit var cameraResult: MutableLiveData<Intent>
    private var selectedGps: Pair<Double, Double>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = FragmentEditDocumentBinding.inflate(inflater, container, false)
            .apply {
                this.viewmodel = viewModel
                this.lifecycleOwner = this@EditDocumentFragment.viewLifecycleOwner
            }

        if (args.readOnly) {
            viewDataBinding.rootView.removeView(viewDataBinding.btnSubmit)
            viewDataBinding.txtCardTitle.text = getString(R.string.responded_fields)
        }
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setGetFieldsResult()
        if (!args.readOnly) {
            setupSubmitEvent()
            setupSubmitFormEvent()
        }
        if (viewModel.fieldsResult.value == null)
            viewModel.getRespondedFields(args.DocumentStatusId, args.VT, args.readOnly, args.type)

        if (!args.readOnly && args.gpsNeeded && selectedGps == null)
            initGps()
    }

    private fun setGetFieldsResult() {
        viewModel.fieldsResult.observe(this.viewLifecycleOwner, Observer {
            when (it) {
                is ApiResult.Success -> {
                    drawForm(
                        if (args.type == "Query")
                            if (it.response.data?.get(0)?.commonIssue == null)
                                it.response.data?.get(0)?.statusQuery
                            else
                                prepareCommonIssueFields(it.response.data)
                        else
                            it.response.data
                    )
                }
                is ApiResult.Error -> viewDataBinding.root.snack(it.errorHandling)
            }
        })
    }

    private fun prepareCommonIssueFields(data: List<FormResponse.DataItem>): List<FormResponse.DataItem>? {
        val result = mutableListOf<FormResponse.DataItem>()
        data.forEach { Item ->
            result.add(
                FormResponse.DataItem(
                    isMandatory = 0,
                    dataType = "Date",
                    label = "Date",
                    value = FormResponse.Value
                        (
                        reply = Item.date
                    )
                )
            )
            result.add(
                FormResponse.DataItem(
                    isMandatory = 0,
                    dataType = "String",
                    label = "Reason",
                    value = FormResponse.Value
                        (
                        reply = """${Item.commonIssue?.commonIssue} ${Item.commonIssue?.commentValue?.let { ", $it" } ?: ""}"""

                    )
                )
            )
        }
        return result
    }

    private fun drawForm(data: List<FormResponse.DataItem>?) {
        elementParser = ElementParser(
            this,
            data, viewDataBinding.layoutContainer, object :
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
                }

                override fun sheriffListNeeded(sheriffList: MutableLiveData<List<SheriffResponse.Sheriff>>) {
                }

                override fun onPreviewImageClicked(fileId: Int?, fileVT: String?, base64: String?) {
                    findNavController().tryNavigate(
                        EditDocumentFragmentDirections
                            .actionEditDocumentFragmentToImageViewerFragment(
                                GetFileRequest(
                                    args.documentBaseInfo.vTDocumentId,
                                    args.documentBaseInfo.documentId,
                                    fileVT,
                                    fileId,
                                    args.documentBaseInfo.vTServiceId,
                                    args.documentBaseInfo.serviceId
                                ), base64
                            )
                    )
                }
            }, args.readOnly
        )
    }

    private fun setupSubmitEvent() {
        viewModel.submitEvent.observe(this.viewLifecycleOwner, EventObserver {
            if (args.gpsNeeded && selectedGps == null) {
                initGps()
                if (selectedGps == null) {
                    initGps()
                    txtFormError.text = getString(R.string.gps_not_available)
                    return@EventObserver
                } else {
                    txtFormError.text = ""
                }
            }
            if (elementParser.isItemsValid()) {
                MainScope().launch {
                    val formResult = FormResult.RespondedFields().also {
                        it.documentStatusId = args.DocumentStatusId
                        it.vT = args.VT
                    }
                    val result = elementParser.getResult(formResult)

                    if (args.gpsNeeded)
                        formResult.gps = Gps(selectedGps?.first, selectedGps?.second)

                    viewModel.submitForm(result as FormResult.RespondedFields)
                }
            }
        })
    }

    private fun setupSubmitFormEvent() {
        viewModel.submitFormEvent.observe(this.viewLifecycleOwner, EventObserver {
            (it as? ApiResult.Success)?.let { error ->
                btnSubmit.snack(error.response.errorHandling, onDismissAction = {
                    if (isAdded)
                        findNavController().popBackStack()
                })
            }
            (it as? ApiResult.Error)?.let { error -> btnSubmit.snack(error.errorHandling) }
        })
    }

    private fun initGps() {
        val awesomeConfiguration = LocationConfiguration.Builder()
            .keepTracking(false)
            .askForPermission(
                PermissionConfiguration.Builder()
                    .rationaleMessage("Please accept location permission.")
                    .requiredPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
                    .build()
            )
            .useGooglePlayServices(
                GooglePlayServicesConfiguration.Builder()
                    .fallbackToDefault(true)
                    .askForGooglePlayServices(false)
                    .askForSettingsApi(true)
                    .failOnSettingsApiSuspended(false)
                    .ignoreLastKnowLocation(false)
                    .build()
            )
            .useDefaultProviders(
                DefaultProviderConfiguration.Builder()
                    .build()
            )
            .build()
        LocationManager.Builder(requireContext().applicationContext)
            .fragment(this)
            .configuration(awesomeConfiguration)
            .notify(object : LocationListener {
                override fun onLocationChanged(location: Location?) {
                    if (location?.latitude != null)
                        selectedGps = Pair(location.latitude, location.longitude)
                }

                override fun onPermissionGranted(alreadyHadPermission: Boolean) {
                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                }

                override fun onProviderEnabled(provider: String?) {
                }

                override fun onProviderDisabled(provider: String?) {
                }

                override fun onProcessTypeChanged(processType: Int) {
                }

                override fun onLocationFailed(type: Int) {
                }
            })
            .build().get()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == FileView.cameraRequest) {
            cameraResult.value = data
        }
    }

}