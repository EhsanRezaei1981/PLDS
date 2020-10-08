package rezaei.mohammad.plds.views.checkin

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
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import rezaei.mohammad.plds.data.model.request.CheckInRequest
import rezaei.mohammad.plds.data.model.request.Gps
import rezaei.mohammad.plds.data.model.response.CheckInResponse
import rezaei.mohammad.plds.data.preference.PreferenceManager
import rezaei.mohammad.plds.databinding.FragmentCheckInBinding
import rezaei.mohammad.plds.util.EventObserver
import rezaei.mohammad.plds.util.LocationHelper
import rezaei.mohammad.plds.views.main.GlobalViewModel

class CheckInFragment : Fragment() {


    private lateinit var viewDataBinding: FragmentCheckInBinding
    private val globalViewModel: GlobalViewModel by sharedViewModel()
    private val args: CheckInFragmentArgs by navArgs()
    private var gps: Gps? = null
    private val prefs: PreferenceManager by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = FragmentCheckInBinding.inflate(layoutInflater, container, false)
            .apply {
                this.viewModel = globalViewModel
                this.lifecycleOwner = this@CheckInFragment.viewLifecycleOwner
            }
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setRecyclerView()
        updateLocation()
        setupServiceValuesObservers()
        //set initial state when service is ran from before
        globalViewModel.checkInService.value?._dataLoading?.postValue(true)
        globalViewModel.checkInService.value?._locationList?.postValue(emptyList())
    }

    private fun setRecyclerView() {
        val adapter = LocationAdapter {
            checkIn(it)
            viewDataBinding.listLocations.isGone = true
        }
        viewDataBinding.listLocations.adapter = adapter
    }

    private fun setupServiceValuesObservers() {
        globalViewModel.checkInService.observe(this.viewLifecycleOwner) { checkInService ->
            if (checkInService == null)
                return@observe

            checkIn(args.location)

            checkInService.isCheckedIn.observe(this.viewLifecycleOwner) {
                if (it)
                    continueLoadSourceFragment()
            }

            checkInService.goToManualFunctionalityEvent.observe(
                this.viewLifecycleOwner,
                EventObserver {
                    findNavController().navigate(
                        CheckInFragmentDirections
                            .actionCheckInFragmentToManualFunctionalityFragment(args.chekinPartName)
                    )
                })
        }
    }

    private fun continueLoadSourceFragment() {
        val action = when (args.chekinPartName) {
            "UpdateDocumentProgress" -> CheckInFragmentDirections.actionCheckInFragmentToGetDocReferenceFragment()
            "ReportIssuePerDocument" -> CheckInFragmentDirections.actionCheckInFragmentToReportIssueFragment()
            "ReportIssueInGeneral" -> CheckInFragmentDirections.actionCheckInFragmentToReportIssueInGeneralFragment()
            else -> CheckInFragmentDirections.actionCheckInFragmentToGetDocReferenceFragment()
        }
        findNavController().navigate(action)
    }

    private fun updateLocation() {
        with(LocationHelper(requireContext(), requireActivity())) {
            start()
            liveLocation.observe(viewLifecycleOwner) { location ->
                gps = Gps(location.latitude, location.longitude)
                stop()
                liveLocation.removeObservers(viewLifecycleOwner)
            }
        }

    }

    private fun checkIn(locationItem: CheckInResponse.LocationItem? = null) {
        if (gps == null) {
            GlobalScope.launch(Dispatchers.Main) {
                delay(2000)
                checkIn(locationItem)
            }
            return
        }
        globalViewModel.checkInService.value?.checkIn(
            CheckInRequest(
                args.chekinPartName,
                locationItem?.locationId ?: prefs.currentLocation?.locationId,
                gps,
                locationItem?.locationType ?: prefs.currentLocation?.locationType,
                locationItem?.locationName ?: prefs.currentLocation?.locationName
            )
        )
    }

}