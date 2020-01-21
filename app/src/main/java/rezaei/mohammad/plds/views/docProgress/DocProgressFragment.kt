package rezaei.mohammad.plds.views.docProgress

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import org.koin.androidx.viewmodel.ext.android.viewModel
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.databinding.DocProgressFragmentBinding

class DocProgressFragment : Fragment() {

    private val viewModel: DocProgressViewModel by viewModel()
    private lateinit var viewDataBinding: DocProgressFragmentBinding
    private val args: DocProgressFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.doc_progress_fragment, container, false)
        viewDataBinding = DocProgressFragmentBinding.bind(root).apply {
            this.viewmodel = this@DocProgressFragment.viewModel
        }
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        viewModel.start(args.documentStatus)
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

}
