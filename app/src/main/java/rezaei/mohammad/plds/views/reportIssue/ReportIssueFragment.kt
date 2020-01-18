package rezaei.mohammad.plds.views.reportIssue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import rezaei.mohammad.plds.R

class ReportIssueFragment : Fragment() {

    companion object {
        fun newInstance() = ReportIssueFragment()
    }

    private lateinit var viewModel: ReportIssueViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.report_issue_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ReportIssueViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
