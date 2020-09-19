package rezaei.mohammad.plds.views.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_main.*
import rezaei.mohammad.plds.BuildConfig
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.model.response.ErrorHandling
import rezaei.mohammad.plds.util.setActivityTitle
import rezaei.mohammad.plds.util.snack

class MainFragment : Fragment() {

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
        val action = MainFragmentDirections.actionMainActivityFragmentToGetDocReferenceFragment()
        findNavController().navigate(action)
    }

    private fun navigateToReportIssuePerDocument() {
        val action = MainFragmentDirections.actionMainActivityFragmentToReportIssueFragment()
        findNavController().navigate(action)
    }

    private fun navigateToReportIssueInGeneral() {
        val action =
            MainFragmentDirections.actionMainActivityFragmentToReportIssueInGeneralFragment()
        findNavController().navigate(action)
    }

    private fun navigateToManageDoc() {
        val action = MainFragmentDirections.actionMainActivityFragmentToManageDocumentFragment()
        findNavController().navigate(action)
    }

    private fun setVersionName() {
        txtVersion.text = "v${BuildConfig.VERSION_NAME}"
    }

}
