package rezaei.mohammad.plds.views.manualfunctionality

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.INVALID_POSITION
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tiper.MaterialSpinner
import kotlinx.android.synthetic.main.fragment_manual_functionality.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.ApiResult
import rezaei.mohammad.plds.data.model.response.CheckInResponse
import rezaei.mohammad.plds.data.model.response.CourtResponse
import rezaei.mohammad.plds.data.model.response.ErrorHandling
import rezaei.mohammad.plds.data.model.response.SheriffResponse
import rezaei.mohammad.plds.databinding.FragmentManualFunctionalityBinding
import rezaei.mohammad.plds.formBuilder.SearchAdapter
import rezaei.mohammad.plds.util.EventObserver
import rezaei.mohammad.plds.util.snack
import rezaei.mohammad.plds.util.tryNavigate

class ManualFunctionalityFragment : Fragment() {

    private val viewModel: ManualFunctionalityViewModel by viewModel()
    private lateinit var viewDataBinding: FragmentManualFunctionalityBinding
    private val args: ManualFunctionalityFragmentArgs by navArgs()
    private var selectedCourt: CourtResponse.Court? = null
    private var selectedSheriff: SheriffResponse.Sheriff? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = FragmentManualFunctionalityBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = viewModel
                lifecycleOwner = this@ManualFunctionalityFragment.viewLifecycleOwner
            }
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setSubmitEventObserver()
        setRadioGroupChangeListener()
        setSpinnerListsObserver()
    }

    private fun setRadioGroupChangeListener() {
        radgManualFunc.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radCourt -> {
                    spnCourtSheriffList.isVisible = false
                    viewModel.getCourts()
                }
                R.id.radSheriff -> {
                    spnCourtSheriffList.isVisible = false
                    viewModel.getSheriffs()
                }
                R.id.radAttorney, R.id.radManual -> {
                    spnCourtSheriffList.isGone = true
                }
            }
        }
    }

    private fun setSpinnerListsObserver() {
        viewModel.getCourtsEvent.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ApiResult.Success -> {
                    val courts = it.response.data
                    if (courts?.isNotEmpty() == true) {
                        spnCourtSheriffList.hint = getString(R.string.select_right_court)
                        spnCourtSheriffList.isVisible = true
                        spnCourtSheriffList.selection = INVALID_POSITION

                        val items = mutableListOf<Pair<String, String?>>()
                        courts.mapTo(items, {
                            Pair(it.courtName ?: "", null)
                        })

                        val adapter = SearchAdapter(items.toList())
                        spnCourtSheriffList.adapter = adapter

                        spnCourtSheriffList.onItemSelectedListener =
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
                }
                is ApiResult.Error -> {
                    btnSubmit.snack(it.errorHandling)
                }
            }
        })
        viewModel.getSheriffsEvent.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ApiResult.Success -> {
                    val sheriffs = it.response.data
                    if (sheriffs?.isNotEmpty() == true) {
                        spnCourtSheriffList.hint = getString(R.string.select_right_sherrif)
                        spnCourtSheriffList.isVisible = true
                        spnCourtSheriffList.selection = INVALID_POSITION

                        val items = mutableListOf<Pair<String, String?>>()
                        sheriffs.mapTo(items, {
                            Pair(it.sheriffAreaName ?: "", it.courtName ?: "")
                        })

                        val adapter = SearchAdapter(items.toList())
                        spnCourtSheriffList.adapter = adapter

                        spnCourtSheriffList.onItemSelectedListener =
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
                }
                is ApiResult.Error -> {
                    btnSubmit.snack(it.errorHandling)
                }
            }
        })
    }

    private fun setSubmitEventObserver() {
        viewModel.submitEvent.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                R.id.radCourt -> {
                    if (!validateSpinner())
                        return@EventObserver
                    findNavController().tryNavigate(
                        ManualFunctionalityFragmentDirections
                            .actionManualFunctionalityFragmentToCheckInFragment(
                                CheckInResponse.LocationItem(
                                    selectedCourt?.courtId,
                                    "Court",
                                    selectedCourt?.courtName
                                ),
                                args.chekinPartName
                            )
                    )
                }
                R.id.radSheriff -> {
                    if (!validateSpinner())
                        return@EventObserver
                    findNavController().tryNavigate(
                        ManualFunctionalityFragmentDirections
                            .actionManualFunctionalityFragmentToCheckInFragment(
                                CheckInResponse.LocationItem(
                                    selectedSheriff?.sheriffOfficeId,
                                    "Sheriff",
                                    selectedSheriff?.sheriffAreaName
                                ),
                                args.chekinPartName
                            )
                    )
                }
                R.id.radAttorney -> {
                    findNavController().tryNavigate(
                        ManualFunctionalityFragmentDirections
                            .actionManualFunctionalityFragmentToCheckInFragment(
                                CheckInResponse.LocationItem(
                                    null,
                                    "PLDSAttorney",
                                    null
                                ),
                                args.chekinPartName
                            )
                    )
                }
                R.id.radManual -> {
                    findNavController().tryNavigate(
                        ManualFunctionalityFragmentDirections
                            .actionManualFunctionalityFragmentToCheckInFragment(
                                CheckInResponse.LocationItem(
                                    null,
                                    "Manual",
                                    null
                                ),
                                args.chekinPartName
                            )
                    )
                }
                else -> {
                    btnSubmit.snack(
                        ErrorHandling(
                            true,
                            "Warning",
                            0,
                            "Please select one item",
                            false
                        )
                    )
                }
            }
        })
    }

    private fun validateSpinner(): Boolean {
        return if (selectedCourt == null && selectedSheriff == null) {
            spnCourtSheriffList.error = getString(R.string.field_mandatory)
            false
        } else {
            spnCourtSheriffList.error = null
            true
        }

    }

}