package rezaei.mohammad.plds.views.docProgress

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import rezaei.mohammad.plds.R

class DocProgressFragment : Fragment() {

    companion object {
        fun newInstance() = DocProgressFragment()
    }

    private lateinit var viewModel: DocProgressViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.doc_progress_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(DocProgressViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
