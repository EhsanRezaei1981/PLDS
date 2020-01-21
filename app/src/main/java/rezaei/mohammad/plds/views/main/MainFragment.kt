package rezaei.mohammad.plds.views.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_main.*
import rezaei.mohammad.plds.R

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnUpdateDocProgress.setOnClickListener {
            navigateToGetDocRef()
        }

        btnReportIssue.setOnClickListener {
            navigateToReportIssue()
        }
    }

    private fun navigateToGetDocRef() {
        val action = MainFragmentDirections.actionMainActivityFragmentToGetDocReferenceFragment()
        findNavController().navigate(action)
    }

    private fun navigateToReportIssue() {
        val action = MainFragmentDirections.actionMainActivityFragmentToReportIssueFragment()
        findNavController().navigate(action)
    }
}
