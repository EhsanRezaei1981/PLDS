package rezaei.mohammad.plds.formBuilder

import android.Manifest.permission
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.tiper.MaterialSpinner
import com.yayandroid.locationmanager.LocationManager
import com.yayandroid.locationmanager.configuration.DefaultProviderConfiguration
import com.yayandroid.locationmanager.configuration.GooglePlayServicesConfiguration
import com.yayandroid.locationmanager.configuration.LocationConfiguration
import com.yayandroid.locationmanager.configuration.PermissionConfiguration
import com.yayandroid.locationmanager.listener.LocationListener
import kotlinx.android.synthetic.main.list_view.view.*
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.model.request.*
import rezaei.mohammad.plds.data.model.response.CourtResponse
import rezaei.mohammad.plds.data.model.response.FormResponse
import rezaei.mohammad.plds.data.model.response.SheriffResponse
import java.lang.ref.WeakReference


open class ListView(
    context: Fragment,
    private val structure: FormResponse.DataItem,
    private val onListItemSelectedCallback: OnListItemSelectedCallback
) : LinearLayout(context.requireContext()), FormView {

    private val fragment = WeakReference(context)

    private var selectedItem: FormResponse.ListItem? = null
    private val courtList = MutableLiveData<List<CourtResponse.Court>>()
    private val sheriffList = MutableLiveData<List<SheriffResponse.Sheriff>>()
    private var selectedCourt: CourtResponse.Court? = null
    private var selectedSheriff: SheriffResponse.Sheriff? = null
    private var selectedGps: Pair<Double, Double>? = null

    init {
        View.inflate(context.requireContext(), R.layout.list_view, this)
        setStructure()
    }

    private fun setStructure() {
        spnItems.hint = structure.label
        setItems()
        spnItems.onItemSelectedListener = object : MaterialSpinner.OnItemSelectedListener {
            override fun onItemSelected(
                parent: MaterialSpinner,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedItem = structure.list?.get(position)
                onListItemSelectedCallback.onItemSelected(selectedItem?.ignoredStatusQueryJson?.map { it.statusQueryId })
                initViewForSelectedItem()
            }

            override fun onNothingSelected(parent: MaterialSpinner) {
            }
        }
    }

    private fun setItems() {
        val items = mutableListOf<String?>()
        structure.list?.mapTo(items, {
            it.description
        })

        val adapter = ArrayAdapter<String>(context, R.layout.spinner_item, items)
        spnItems.adapter = adapter
    }

    private fun initViewForSelectedItem() {
        inputComment.visibility = View.GONE
        spnCustomAction.visibility = View.GONE
        progressBar.visibility = View.GONE

        if (selectedItem?.commentIsNeeded == 1) {
            inputComment.visibility = View.VISIBLE
        }
        if (selectedItem?.gPSIsNeeded == 1) {
            initGps()
        }
        if (selectedItem?.customActionCode?.contains("WrongCourt") == true) {
            onListItemSelectedCallback.courtListNeeded(courtList)
            initCourtList()
        }
        if (selectedItem?.customActionCode?.contains("WrongSheriff") == true) {
            onListItemSelectedCallback.sheriffListNeeded(sheriffList)
            initSheriffList()
        }

    }

    private fun initGps() {
        selectedGps = null
        val awesomeConfiguration = LocationConfiguration.Builder()
            .keepTracking(false)
            .askForPermission(
                PermissionConfiguration.Builder()
                    .rationaleMessage("Please accept location permission.")
                    .requiredPermissions(arrayOf(permission.ACCESS_FINE_LOCATION))
                    .build()
            )
            .useGooglePlayServices(
                GooglePlayServicesConfiguration.Builder()
                    .fallbackToDefault(true)
                    .askForGooglePlayServices(false)
                    .askForSettingsApi(true)
                    .failOnConnectionSuspended(true)
                    .failOnSettingsApiSuspended(false)
                    .ignoreLastKnowLocation(false)
                    .build()
            )
            .useDefaultProviders(
                DefaultProviderConfiguration.Builder()
                    .build()
            )
            .build()
        LocationManager.Builder(context.applicationContext)
            .fragment(fragment.get())
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

    private fun initCourtList() {
        progressBar.visibility = View.VISIBLE
        selectedCourt = null
        fragment.get()?.let {
            courtList.observe(it, Observer<List<CourtResponse.Court>> { courts ->
                if (courts.isNotEmpty()) {
                    spnCustomAction.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    spnCustomAction.hint = "Select right court"

                    val items = mutableListOf<String?>()
                    courts?.mapTo(items, {
                        it.courtName
                    })

                    val adapter = ArrayAdapter<String>(context, R.layout.spinner_item, items)
                    spnCustomAction.adapter = adapter

                    spnCustomAction.onItemSelectedListener =
                        object : MaterialSpinner.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: MaterialSpinner,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                selectedCourt = courts[position]
                            }

                            override fun onNothingSelected(parent: MaterialSpinner) {
                            }
                        }
                }
            })
        }
    }

    private fun initSheriffList() {
        progressBar.visibility = View.VISIBLE
        selectedSheriff = null
        fragment.get()?.let {
            sheriffList.observe(it, Observer<List<SheriffResponse.Sheriff>> { sheriffs ->
                if (sheriffs.isNotEmpty()) {
                    spnCustomAction.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    spnCustomAction.hint = "Select right sheriff"

                    val items = mutableListOf<String?>()
                    sheriffs?.mapTo(items, {
                        it.sheriffAreaName
                    })

                    val adapter = ArrayAdapter<String>(context, R.layout.spinner_item, items)
                    spnCustomAction.adapter = adapter

                    spnCustomAction.onItemSelectedListener =
                        object : MaterialSpinner.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: MaterialSpinner,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                selectedSheriff = sheriffs[position]
                            }

                            override fun onNothingSelected(parent: MaterialSpinner) {
                            }
                        }
                }
            })
        }
    }

    override fun validate(): Boolean {
        return if (structure.isMandatory == 0) {
            true
        } else {
            if (selectedItem == null) {
                spnItems.error = "This field is mandatory."
                false
            } else {
                if (selectedItem?.gPSIsNeeded == 1) {
                    return if (selectedGps == null) {
                        spnItems.error = "Gps data not available."
                        initGps()
                        false
                    } else {
                        spnItems.error = null
                        true
                    }
                }
                if (selectedItem?.commentIsNeeded == 1) {
                    return if (inputComment.editText?.text.toString().isEmpty()) {
                        inputComment.error = "This field is mandatory."
                        false
                    } else {
                        inputComment.error = null
                        true
                    }
                }
                return if (spnCustomAction.visibility == View.VISIBLE) {
                    if (selectedCourt == null || selectedSheriff == null) {
                        spnCustomAction.error = "This field is mandatory."
                        false
                    } else {
                        spnCustomAction.error = null
                        true
                    }
                } else {
                    spnItems.error = null
                    true
                }
            }
        }
    }

    override val elementId: Int = structure.statusQueryId ?: 0

    override val result: ElementResult?
        get() = ElementResult.ListResult(
            elementId,
            ListItem(
                id = selectedItem?.statusQueryIssueId,
                text = selectedItem?.description,
                comment = inputComment.editText?.text.toString(),
                customAction = if (selectedCourt != null || selectedSheriff != null)
                    CustomAction(
                        Data(
                            courtId = selectedCourt?.courtId,
                            courtName = selectedCourt?.courtName,
                            sheriffAreaName = selectedSheriff?.sheriffAreaName,
                            sheriffOfficeId = selectedSheriff?.sheriffOfficeId
                        )
                    ) else null
            ),
            if (selectedGps != null)
                Gps(
                    selectedGps?.first,
                    selectedGps?.second
                ) else null
        )

}

interface OnListItemSelectedCallback {
    fun onItemSelected(ignoreViewIds: List<Int?>?)
    fun courtListNeeded(courtList: MutableLiveData<List<CourtResponse.Court>>)
    fun sheriffListNeeded(sheriffList: MutableLiveData<List<SheriffResponse.Sheriff>>)
}