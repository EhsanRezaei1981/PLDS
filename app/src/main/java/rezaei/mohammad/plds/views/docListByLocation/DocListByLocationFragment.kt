package rezaei.mohammad.plds.views.docListByLocation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import rezaei.mohammad.plds.R

class DocListByLocationFragment : Fragment() {

    companion object {
        fun newInstance() = DocListByLocationFragment()
    }

    private lateinit var viewModel: DocListByLocationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_doc_list_by_location, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(DocListByLocationViewModel::class.java)
        // TODO: Use the ViewModel
    }

}