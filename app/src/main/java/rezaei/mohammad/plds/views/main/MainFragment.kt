package rezaei.mohammad.plds.views.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_main.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import rezaei.mohammad.plds.BuildConfig
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.model.response.ErrorHandling
import rezaei.mohammad.plds.util.setActivityTitle
import rezaei.mohammad.plds.util.snack
import rezaei.mohammad.plds.util.tryNavigate

class MainFragment : Fragment() {

    private val globalViewModel: GlobalViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActivityTitle(getString(R.string.app_name))
        btnUpdateDocProgress.setOnClickListener {
            navigateToGetDocRef()
        }

        btnReportIssueGeneral.setOnClickListener {
//            navigateToReportIssueInGeneral()
            it.snack(ErrorHandling(errorMessage = "Coming soon...", isSuccessful = true))
        }

        btnReportIssueDocument.setOnClickListener {
            navigateToReportIssuePerDocument()
        }

        btnManageDocument.setOnClickListener {
            navigateToManageDoc()
        }

        setVersionName()
    }

    private fun navigateToGetDocRef() {
        val action = if (isCheckedIn())
            MainFragmentDirections.actionMainActivityFragmentToGetDocReferenceFragment()
        else
            MainFragmentDirections.actionMainActivityFragmentToCheckInFragment2(
                null,
                chekinPartName = "UpdateDocumentProgress"
            )
        findNavController().tryNavigate(action)
    }

    private fun navigateToReportIssuePerDocument() {
        val action = if (isCheckedIn())
            MainFragmentDirections.actionMainActivityFragmentToReportIssueFragment()
        else
            MainFragmentDirections.actionMainActivityFragmentToCheckInFragment2(
                null,
                chekinPartName = "ReportIssuePerDocument"
            )
        findNavController().tryNavigate(action)
    }

    private fun navigateToReportIssueInGeneral() {
        val action = if (isCheckedIn())
            MainFragmentDirections.actionMainActivityFragmentToReportIssueInGeneralFragment()
        else
            MainFragmentDirections.actionMainActivityFragmentToCheckInFragment2(
                null,
                chekinPartName = "ReportIssueInGeneral"
            )
        findNavController().tryNavigate(action)
    }

    private fun navigateToManageDoc() {
        val action = MainFragmentDirections.actionMainActivityFragmentToManageDocumentFragment()
        findNavController().tryNavigate(action)
    }

    private fun isCheckedIn(): Boolean {
        val checkInService = (requireActivity() as MainActivity).checkInService
        return checkInService != null && checkInService.isCheckedIn
    }


    private fun setVersionName() {
        txtVersion.text = "v${BuildConfig.VERSION_NAME}"
    }

}
