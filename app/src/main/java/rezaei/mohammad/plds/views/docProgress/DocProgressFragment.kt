package rezaei.mohammad.plds.views.docProgress

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.doc_progress_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.Result
import rezaei.mohammad.plds.databinding.DocProgressFragmentBinding
import rezaei.mohammad.plds.util.EventObserver
import rezaei.mohammad.plds.util.setActivityTitle
import rezaei.mohammad.plds.util.snack

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
        setActivityTitle("Document reference NO: ${args.documentStatus.documentReferenceNo}")
        setupButtonsCallback()
    }

    private fun setupButtonsCallback() {
        viewModel.onBackPressEvent.observe(this, EventObserver {
            findNavController().popBackStack()
        })
        viewModel.onYesPressEvent.observe(this, EventObserver {
            (it as? Result.Success)?.let {
                val action =
                    DocProgressFragmentDirections.actionDocProgressFragmentToSubmitFormFragment(
                        successful = it.response,
                        unsuccessful = null
                    )
                findNavController().navigate(action)
            }
            (it as? Result.Error)?.let { error -> btnBack.snack(error.errorHandling) }
        })
        viewModel.onNoPressEvent.observe(this, EventObserver {
            (it as? Result.Success)?.let {
                val action =
                    DocProgressFragmentDirections.actionDocProgressFragmentToSubmitFormFragment(
                        unsuccessful = it.response,
                        successful = null
                    )
                findNavController().navigate(action)
            }
            (it as? Result.Error)?.let { error -> btnBack.snack(error.errorHandling) }
        })
    }

}
