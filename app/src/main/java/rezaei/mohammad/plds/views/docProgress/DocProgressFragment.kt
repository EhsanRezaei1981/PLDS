package rezaei.mohammad.plds.views.docProgress

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.doc_progress_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.Result
import rezaei.mohammad.plds.databinding.DocProgressFragmentBinding
import rezaei.mohammad.plds.util.EventObserver
import rezaei.mohammad.plds.util.snack
import rezaei.mohammad.plds.views.addMultiDoc.AddMultiDocFragment

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
        setupButtonsCallback()
        setupMultiAddDocView()
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
                        unsuccessful = null,
                        isMultipleDocument = args.documentStatus.isAbleToAcceptMultipleDocuments == 1,
                        docRefNo = args.documentStatus.documentReferenceNo
                    )
                findNavController().navigate(action)
            }
            (it as? Result.Error)?.let { error -> btnBack.snack(error.errorHandling?.errorMessage) }
        })
        viewModel.onNoPressEvent.observe(this, EventObserver {
            (it as? Result.Success)?.let {
                val action =
                    DocProgressFragmentDirections.actionDocProgressFragmentToSubmitFormFragment(
                        unsuccessful = it.response,
                        successful = null,
                        isMultipleDocument = args.documentStatus.isAbleToAcceptMultipleDocuments == 1,
                        docRefNo = args.documentStatus.documentReferenceNo
                    )
                findNavController().navigate(action)
            }
            (it as? Result.Error)?.let { error -> btnBack.snack(error.errorHandling?.errorMessage) }
        })
    }

    private fun setupMultiAddDocView() {
        viewModel.documentStatus.observe(this, Observer {
            if (it.isAbleToAcceptMultipleDocuments == 1)
                childFragmentManager.beginTransaction()
                    .replace(viewDataBinding.multiAddDoc.id, AddMultiDocFragment())
                    .commit()
        })
    }

}
