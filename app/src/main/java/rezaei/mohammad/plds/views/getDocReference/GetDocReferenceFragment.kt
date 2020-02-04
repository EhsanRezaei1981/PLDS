package rezaei.mohammad.plds.views.getDocReference

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionManager
import kotlinx.android.synthetic.main.get_doc_reference_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.Result
import rezaei.mohammad.plds.data.model.response.DocumentStatusResponse
import rezaei.mohammad.plds.databinding.GetDocReferenceFragmentBinding
import rezaei.mohammad.plds.util.EventObserver
import rezaei.mohammad.plds.util.setActivityTitle
import rezaei.mohammad.plds.util.snack
import rezaei.mohammad.plds.views.addMultiDoc.AddMultiDocFragment

class GetDocReferenceFragment : Fragment() {


    private val viewModel: GetDocReferenceViewModel by viewModel()
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
        setActivityTitle(getString(R.string.check_document_status))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupForDocumentStatusResponse()
        documentListChangeListener()
    }

    private fun setupForDocumentStatusResponse() {
        viewModel.documentStatusEvent.observe(this, EventObserver {
            (it as? Result.Success)?.let { navigateToDocProgress(it.response.data!!) }
            (it as? Result.Error)?.let { error -> btnCheckProgress.snack(error.errorHandling) }
        })
    }

    private fun navigateToDocProgress(documentStatusResponse: DocumentStatusResponse.Data) {
        val action =
            GetDocReferenceFragmentDirections.actionGetDocReferenceFragmentToDocProgressFragment(
                documentStatusResponse
            )
        findNavController().navigate(action)
    }

    private fun documentListChangeListener() {
        (childFragmentManager.findFragmentById(R.id.multiAddDoc) as? AddMultiDocFragment)?.let { fragment ->
            fragment.documentList.observe(this, Observer {
                TransitionManager.beginDelayedTransition(viewDataBinding.root as ViewGroup)
                if (it.isNotEmpty())
                    layCheckProgress.visibility = View.VISIBLE
                else
                    layCheckProgress.visibility = View.GONE
            })
        }
    }

}
