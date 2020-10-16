package rezaei.mohammad.plds.views.checkin

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import rezaei.mohammad.plds.data.model.request.CheckInRequest
import rezaei.mohammad.plds.data.model.request.Gps
import rezaei.mohammad.plds.data.model.response.CheckInResponse
import rezaei.mohammad.plds.databinding.FragmentCheckInBinding
import rezaei.mohammad.plds.service.CheckInService
import rezaei.mohammad.plds.util.LocationHelper
import rezaei.mohammad.plds.util.tryNavigate
import rezaei.mohammad.plds.views.main.MainActivity


class CheckInFragment : Fragment() {

    private lateinit var viewDataBinding: FragmentCheckInBinding
    private val viewModel: CheckInViewModel by viewModel()
    private val args: CheckInFragmentArgs by navArgs()
    private lateinit var locationHelper: LocationHelper
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
        setRecyclerView()
        updateLocation()
        if (viewModel.locationList.value?.isNotEmpty() != false)
            checkIn(args.location)
    }

    private fun setRecyclerView() {
        val adapter = LocationAdapter {
            checkIn(it)
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

    private fun updateLocation() {
        locationHelper = LocationHelper(requireContext(), requireActivity())
            .apply {
                start()
                liveLocation.observe(viewLifecycleOwner) { location ->
                    if (location == null)
                        return@observe
                    gps = Gps(location.latitude, location.longitude)
                }
            }

    }

    private fun checkIn(locationItem: CheckInResponse.LocationItem? = null) {
        if (!isAdded)
            return

        if (gps == null || !LocationHelper.isGpsEnable(requireContext())) {
            GlobalScope.launch(Dispatchers.Main) {
                delay(2000)
                locationHelper.start()
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
        locationHelper.stop()
        viewModel._locationList.value = null
    }
}