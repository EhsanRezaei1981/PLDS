package rezaei.mohammad.plds.views.reportIssue.commonaction

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.yayandroid.locationmanager.LocationManager
import com.yayandroid.locationmanager.configuration.DefaultProviderConfiguration
import com.yayandroid.locationmanager.configuration.GooglePlayServicesConfiguration
import com.yayandroid.locationmanager.configuration.LocationConfiguration
import com.yayandroid.locationmanager.configuration.PermissionConfiguration
import com.yayandroid.locationmanager.listener.LocationListener
import kotlinx.android.synthetic.main.fragment_common_action.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.ApiResult
import rezaei.mohammad.plds.data.model.request.FormResult
import rezaei.mohammad.plds.data.model.request.Gps
import rezaei.mohammad.plds.data.model.response.CommonActionReasonsResponse
import rezaei.mohammad.plds.data.model.response.CourtResponse
import rezaei.mohammad.plds.data.model.response.FormResponse
import rezaei.mohammad.plds.data.model.response.SheriffResponse
import rezaei.mohammad.plds.databinding.FragmentCommonActionBinding
import rezaei.mohammad.plds.formBuilder.ElementParser
import rezaei.mohammad.plds.formBuilder.ElementsActivityRequestCallback
import rezaei.mohammad.plds.util.EventObserver
import rezaei.mohammad.plds.util.snack
import rezaei.mohammad.plds.util.tryNavigate
import rezaei.mohammad.plds.views.main.MainActivity

class CommonActionFragment : Fragment() {

    private val viewModel: CommonActionViewModel by viewModel()
    private lateinit var viewDataBinding: FragmentCommonActionBinding
    private lateinit var elementParser: ElementParser
    private lateinit var imageResult: MutableLiveData<Intent>
    private var reasonList: List<CommonActionReasonsResponse.Data>? = null
    private var selectedGps: Pair<Double, Double>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = FragmentCommonActionBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = viewModel
                lifecycleOwner = viewLifecycleOwner
            }
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupReasonListObserver()
        setupFormSubmitObserver()
        setupSubmitFormResultObserver()
        if (!container.isVisible) {
            viewModel.getReasonList(getLocationType())
            initGps()
        }
    }

    private fun setupFormSubmitObserver() {
        viewModel.submitFormEvent.observe(viewLifecycleOwner, EventObserver {
            if (selectedGps == null) {
                initGps()
                if (selectedGps == null) {
                    initGps()
                    txtFormError.text = getString(R.string.gps_not_available)
                    return@EventObserver
                } else {
                    txtFormError.text = ""
                }
            }
            if (!elementParser.isItemsValid())
                return@EventObserver

            val formResult = FormResult.CommonAction()
            elementParser.getResult(formResult)
            val chosenReason = reasonList?.find { it.commonActionId == formResult.commonActionId }
            val checkInService = (requireActivity() as MainActivity).checkInService
            formResult.vTCommonActionId = chosenReason?.vT
            formResult.locationId = checkInService?.checkedInLocation?.locationId
            formResult.gps = Gps(selectedGps?.first, selectedGps?.second)
            formResult.locationType = getLocationType()
            viewModel.submitForm(formResult)
        })
    }

    private fun setupSubmitFormResultObserver() {
        viewModel.submitFormResult.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResult.Success -> {
                    view?.snack(it.response.errorHandling, onDismissAction = {
                        if (isAdded)
                            requireActivity().onBackPressed()
                    })
                }
                is ApiResult.Error -> view?.snack(it.errorHandling)
            }
        }
    }

    private fun setupReasonListObserver() {
        viewModel.reasonList.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResult.Success -> {
                    it.response.data?.let {
                        drawForm(prepareFields(it.map { item ->
                            FormResponse.ListItem(
                                description = item.description,
                                listId = item.commonActionId
                            )
                        }))
                        container.isVisible = true
                        reasonList = it
                    } ?: kotlin.run {
                        view?.snack(it.response.errorHandling, onDismissAction = {
                            if (isAdded)
                                findNavController().popBackStack()
                        })
                    }
                }
                is ApiResult.Error -> view?.snack(it.errorHandling, onDismissAction = {
                    if (isAdded)
                        findNavController().popBackStack()
                })
            }
        }
    }

    private fun prepareFields(reasonItems: List<FormResponse.ListItem>): List<FormResponse.DataItem> =
        listOf(
            FormResponse.DataItem(
                isMandatory = 1,
                dataType = "Date",
                label = "Date"
            ),
            FormResponse.DataItem(
                isMandatory = 1,
                dataType = "List",
                label = "Reason",
                list = reasonItems
            ),
            FormResponse.DataItem(
                isMandatory = 0,
                dataType = "File",
                label = "Image",
                dataTypeSetting = FormResponse.DataTypeSetting(
                    FormResponse.File(cameraIsNeeded = true, isFileBrowserNeeded = true)
                )
            ),
            FormResponse.DataItem(
                isMandatory = 0,
                dataType = "String",
                label = "Comment"
            )
        )

    private fun drawForm(data: List<FormResponse.DataItem>?) {
        elementParser = ElementParser(
            this,
            data, viewDataBinding.container, object :
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

                override fun onPreviewImageClicked(fileId: Int?, fileVT: String?, base64: String?) {
                    findNavController().tryNavigate(
                        CommonActionFragmentDirections
                            .actionCommonActionFragmentToImageViewerFragment(
                                base64 = base64,
                                getFileRequest = null
                            )
                    )
                }

                override fun onListItemSelected(
                    elementId: Int,
                    selectedItem: FormResponse.ListItem
                ) {}
            }
        )
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
        if (resultCode == Activity.RESULT_OK) {
            imageResult.value = data
        }
    }

    private fun getLocationType(): String? =
        (requireActivity() as MainActivity).checkInService?.checkedInLocation?.locationType


}
