package rezaei.mohammad.plds.views.checkin

import android.Manifest
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.yayandroid.locationmanager.LocationManager
import com.yayandroid.locationmanager.configuration.DefaultProviderConfiguration
import com.yayandroid.locationmanager.configuration.GooglePlayServicesConfiguration
import com.yayandroid.locationmanager.configuration.LocationConfiguration
import com.yayandroid.locationmanager.configuration.PermissionConfiguration
import com.yayandroid.locationmanager.listener.LocationListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.model.request.CheckInRequest
import rezaei.mohammad.plds.data.model.request.Gps
import rezaei.mohammad.plds.data.model.response.CheckInResponse
import rezaei.mohammad.plds.databinding.FragmentCheckInBinding
import rezaei.mohammad.plds.service.CheckInService
import rezaei.mohammad.plds.util.LocationHelper
import rezaei.mohammad.plds.util.setActivityTitle
import rezaei.mohammad.plds.util.tryNavigate
import rezaei.mohammad.plds.views.main.MainActivity


class CheckInFragment : Fragment() {

    private lateinit var viewDataBinding: FragmentCheckInBinding
    private val viewModel: CheckInViewModel by viewModel()
    private val args: CheckInFragmentArgs by navArgs()
    private var gps: Gps? = null
    var checkInService: CheckInService? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        checkInService = (context as MainActivity).checkInService
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = FragmentCheckInBinding.inflate(layoutInflater, container, false)
            .apply {
                this.viewModel = this@CheckInFragment.viewModel
                this.lifecycleOwner = this@CheckInFragment.viewLifecycleOwner
            }
        return viewDataBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setActivityTitle(getString(R.string.check_in))
        setRecyclerView()
        initGps()
        if (viewModel.locationList.value?.isNotEmpty() != false)
            checkIn(args.location)
    }

    private fun setRecyclerView() {
        val adapter = LocationAdapter { location ->
            if (location == null) {
                onNoLocationFound()
                return@LocationAdapter
            }
            checkIn(location)
            viewDataBinding.listLocations.isGone = true
            viewModel._locationList.value = null
        }
        viewDataBinding.listLocations.adapter = adapter
    }

    private fun continueLoadSourceFragment() {
        val action = when (args.chekinPartName) {
            "UpdateDocumentProgress" -> CheckInFragmentDirections.actionCheckInFragmentToGetDocReferenceFragment()
            "ReportIssuePerDocument" -> CheckInFragmentDirections.actionCheckInFragmentToReportIssueFragment()
            "ReportIssueInGeneral" -> CheckInFragmentDirections.actionCheckInFragmentToReportIssueInGeneralFragment()
            else -> CheckInFragmentDirections.actionCheckInFragmentToGetDocReferenceFragment()
        }
        findNavController().tryNavigate(action)
    }

    private fun initGps() {
        val awesomeConfiguration = LocationConfiguration.Builder()
            .keepTracking(false)
            .askForPermission(
                PermissionConfiguration.Builder()
                    .rationaleMessage(getString(R.string.accept_loc_permission))
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
        val manager = LocationManager.Builder(requireContext().applicationContext)
            .fragment(this)
            .configuration(awesomeConfiguration)
            .notify(object : LocationListener {
                override fun onLocationChanged(location: Location?) {
                    if (location?.latitude != null)
                        gps = Gps(location.latitude, location.longitude)
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
            .build()
        if (!manager.isAnyDialogShowing)
            manager.get()
    }

    private fun checkIn(locationItem: CheckInResponse.LocationItem? = null) {
        if (!isAdded)
            return

        if (gps == null || !LocationHelper.isGpsEnable(requireContext())) {
            GlobalScope.launch(Dispatchers.Main) {
                delay(2000)
                initGps()
                checkIn(locationItem)
            }
            return
        }
        checkInService?.checkIn(
            CheckInRequest(
                args.chekinPartName,
                locationItem?.locationId,
                gps,
                locationItem?.locationType,
                locationItem?.locationName
            )
        )
    }

    fun onCheckedIn(checkedInLocation: CheckInResponse.Data) {
        continueLoadSourceFragment()
    }

    fun showLocationList(locationList: List<CheckInResponse.LocationItem>) {
        viewModel._locationList.value = locationList
        gps = null
    }

    fun onNoLocationFound() {
        findNavController().tryNavigate(
            CheckInFragmentDirections
                .actionCheckInFragmentToManualFunctionalityFragment(args.chekinPartName)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel._locationList.value = null
    }
}