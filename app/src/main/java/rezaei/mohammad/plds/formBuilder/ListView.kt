package rezaei.mohammad.plds.formBuilder

import android.Manifest.permission
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.core.view.isVisible
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
import kotlinx.android.synthetic.main.view_list.view.*
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.model.request.*
import rezaei.mohammad.plds.data.model.response.CourtResponse
import rezaei.mohammad.plds.data.model.response.FormResponse
import rezaei.mohammad.plds.data.model.response.SheriffResponse
import java.lang.ref.WeakReference


open class ListView(
    context: Fragment,
    private val structure: FormResponse.DataItem,
    private val onListItemSelectedCallback: OnListItemSelectedCallback?,
    readOnly: Boolean = false
) : LinearLayout(context.requireContext()), FormView {

    private val fragment = WeakReference(context)

    private var selectedItem: FormResponse.ListItem? = null
    private val courtList = MutableLiveData<List<CourtResponse.Court>>()
    private val sheriffList = MutableLiveData<List<SheriffResponse.Sheriff>>()
    private var selectedCourt: CourtResponse.Court? = null
    private var selectedSheriff: SheriffResponse.Sheriff? = null
    private var selectedGps: Pair<Double, Double>? = null

    var isReadOnly: Boolean = false
        set(value) {
            spnItems.isEnabled = !value
            spnCustomAction.isEnabled = !value
            inputComment.isEnabled = !value
            inputComment.isFocusable = !value
            inputComment.isFocusableInTouchMode = !value
            field = value
        }

    init {
        isSaveEnabled = true
        View.inflate(context.requireContext(), R.layout.view_list, this)
        isReadOnly = readOnly
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
                onListItemSelectedCallback?.onItemSelected(selectedItem?.ignoredStatusQueryJson?.map { it.statusQueryId })
                initViewForSelectedItem()
            }

            override fun onNothingSelected(parent: MaterialSpinner) {
            }
        }

        structure.value?.let { value ->
            spnItems.selection = structure.list
                ?.indexOfFirst { it.listId == value.listSelectedId ?: 0 } ?: 0
            if (value.listComment?.isNotEmpty() == true) {
                inputComment.isVisible = true
                inputComment.editText?.setText(structure.value.listComment)
            }
        }
    }

    private fun setItems() {
        val items = mutableListOf<String?>()
        structure.list?.mapTo(items, {
            it.description
        })

        val adapter = ArrayAdapter<String>(context, R.layout.item_spinner, items)
        spnItems.adapter = adapter
    }

    private fun initViewForSelectedItem() {
        inputComment.visibility = View.GONE
        spnCustomAction.visibility = View.GONE
        progressBar.visibility = View.GONE

        if (selectedItem?.commentIsNeeded == 1) {
            inputComment.visibility = View.VISIBLE
        }
        if (selectedItem?.gPSIsNeeded == 1 && !isReadOnly) {
            initGps()
        }
        if (selectedItem?.customActionCode?.contains("ChangeCourt") == true && !isReadOnly) {
            onListItemSelectedCallback?.courtListNeeded(courtList)
            initCourtList()
        }
        if (selectedItem?.customActionCode?.contains("ChangeSheriff") == true && !isReadOnly) {
            onListItemSelectedCallback?.sheriffListNeeded(sheriffList)
            initSheriffList()
        }

    }

    private fun initGps() {
        selectedGps = null
        val awesomeConfiguration = LocationConfiguration.Builder()
            .keepTracking(false)
            .askForPermission(
                PermissionConfiguration.Builder()
                    .rationaleMessage(context.getString(R.string.accept_loc_permission))
                    .requiredPermissions(arrayOf(permission.ACCESS_FINE_LOCATION))
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
                if (selectedItem?.customActionCode?.contains("ChangeCourt") == false) {
                    return@Observer
                }
                if (courts.isNotEmpty()) {
                    spnCustomAction.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    spnCustomAction.hint = context.getString(R.string.select_right_court)

                    val items = mutableListOf<Pair<String, String?>>()
                    courts?.mapTo(items, {
                        Pair(it.courtName ?: "", null)
                    })

                    val adapter = SearchAdapter(items.toList())
                    spnCustomAction.adapter = adapter

                    spnCustomAction.onItemSelectedListener =
                        object : MaterialSpinner.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: MaterialSpinner,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                //in searchable mode id is item position
                                selectedCourt = courts[id.toInt()]
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
                if (selectedItem?.customActionCode?.contains("ChangeSheriff") == false) {
                    return@Observer
                }
                if (sheriffs.isNotEmpty()) {
                    spnCustomAction.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    spnCustomAction.hint = context.getString(R.string.select_right_sherrif)

                    val items = mutableListOf<Pair<String, String?>>()
                    sheriffs?.mapTo(items, {
                        Pair(it.sheriffAreaName ?: "", it.courtName ?: "")
                    })

                    val adapter = SearchAdapter(items.toList())
                    spnCustomAction.adapter = adapter

                    spnCustomAction.onItemSelectedListener =
                        object : MaterialSpinner.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: MaterialSpinner,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                //in searchable mode id is item position
                                selectedSheriff = sheriffs[id.toInt()]
                            }

                            override fun onNothingSelected(parent: MaterialSpinner) {
                            }
                        }
                }
            })
        }
    }

    override fun validate(): Boolean {
        val errors = mutableListOf<Boolean>()
        if (structure.isMandatory == 0) {
            errors.add(true)
        } else {
            if (selectedItem == null) {
                spnItems.error = context.getString(R.string.field_mandatory)
                errors.add(false)
            } else {
                if (selectedItem?.gPSIsNeeded == 1) {
                    if (selectedGps == null || selectedGps?.first == 0.0) {
                        spnItems.error = context.getString(R.string.gps_not_available)
                        initGps()
                        errors.add(false)
                    } else {
                        spnItems.error = null
                        errors.add(true)
                    }
                }
                if (selectedItem?.commentIsNeeded == 1) {
                    if (inputComment.editText?.text.toString().isEmpty()) {
                        inputComment.error = context.getString(R.string.field_mandatory)
                        errors.add(false)
                    } else {
                        inputComment.error = null
                        errors.add(true)
                    }
                }
                if (spnCustomAction.visibility == View.VISIBLE) {
                    if (selectedCourt == null && selectedSheriff == null) {
                        spnCustomAction.error = context.getString(R.string.field_mandatory)
                        errors.add(false)
                    } else {
                        spnCustomAction.error = null
                        errors.add(true)
                    }
                } else {
                    spnCustomAction.error = null
                    errors.add(true)
                }
            }
        }
        return errors.all { it }
    }

    override val elementId: Int = structure.statusQueryId ?: 0

    override val result: ElementResult?
        get() {
            return if (selectedItem?.customActionCode != "Issue")
                ElementResult.ListResult(
                    elementId,
                    ListItem(
                        id = selectedItem?.listId,
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
                    structure.value?.vTMTId,
                    structure.value?.mTId,
                    if (selectedGps != null)
                        Gps(
                            selectedGps?.first,
                            selectedGps?.second
                        ) else null
                )
            else
                ElementResult.IssueResult(
                    inputComment.editText?.text.toString(),
                    null,
                    selectedItem?.listId,
                    selectedItem?.description,
                    null,
                    gps = if (selectedGps != null)
                        Gps(
                            selectedGps?.first,
                            selectedGps?.second
                        ) else null
                )
        }

}

interface OnListItemSelectedCallback {
    fun onItemSelected(ignoreViewIds: List<Int?>?)
    fun courtListNeeded(courtList: MutableLiveData<List<CourtResponse.Court>>)
    fun sheriffListNeeded(sheriffList: MutableLiveData<List<SheriffResponse.Sheriff>>)
}