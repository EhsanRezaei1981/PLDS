package rezaei.mohammad.plds.views.getDocReference

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.get_doc_reference_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.Result
import rezaei.mohammad.plds.data.model.response.DocumentStatusResponse
import rezaei.mohammad.plds.databinding.GetDocReferenceFragmentBinding
import rezaei.mohammad.plds.util.EventObserver
import rezaei.mohammad.plds.util.snack
import rezaei.mohammad.plds.views.main.GlobalViewModel

class GetDocReferenceFragment : Fragment() {

    private val globalViewModel: GlobalViewModel by sharedViewModel()
    private val viewModel: GetDocReferenceViewModel by viewModel { parametersOf(globalViewModel.docRefNo) }
    private lateinit var viewDataBinding: GetDocReferenceFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.get_doc_reference_fragment, container, false)
        viewDataBinding = GetDocReferenceFragmentBinding.bind(root).apply {
            viewModel = this@GetDocReferenceFragment.viewModel
        }
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnReadQR.setOnClickListener {
            navigateToQrScanner()
        }
    }

    private fun navigateToQrScanner() {
        val action =
            GetDocReferenceFragmentDirections.actionGetDocReferenceFragmentToQrReaderFragment()
        findNavController().navigate(action)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupForDocumentStatusResponse()
    }

    private fun setupForDocumentStatusResponse() {
        viewModel.documentStatusEvent.observe(this, EventObserver {
            (it as? Result.Success)?.let { navigateToDocProgress(it.response.data!!) }
            (it as? Result.Error)?.let { error -> btnCheckProgress.snack(error.errorHandling?.errorMessage) }
        })
    }

    private fun navigateToDocProgress(documentStatusResponse: DocumentStatusResponse.Data) {
        val action =
            GetDocReferenceFragmentDirections.actionGetDocReferenceFragmentToDocProgressFragment(
                documentStatusResponse
            )
        findNavController().navigate(action)
    }

}
