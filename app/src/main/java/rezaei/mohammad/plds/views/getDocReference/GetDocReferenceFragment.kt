package rezaei.mohammad.plds.views.getDocReference

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import rezaei.mohammad.plds.R

class GetDocReferenceFragment : Fragment() {

    companion object {
        fun newInstance() = GetDocReferenceFragment()
    }

    private lateinit var viewModel: GetDocReferenceViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.get_doc_reference_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(GetDocReferenceViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
