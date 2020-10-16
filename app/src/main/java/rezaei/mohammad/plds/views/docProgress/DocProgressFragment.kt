package rezaei.mohammad.plds.views.docProgress

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_doc_progress.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.ApiResult
import rezaei.mohammad.plds.databinding.FragmentDocProgressBinding
import rezaei.mohammad.plds.util.EventObserver
import rezaei.mohammad.plds.util.setActivityTitle
import rezaei.mohammad.plds.util.snack
import rezaei.mohammad.plds.util.tryNavigate

class DocProgressFragment : Fragment() {

    private val viewModel: DocProgressViewModel by viewModel()
    private lateinit var viewDataBinding: FragmentDocProgressBinding
    private val args: DocProgressFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_doc_progress, container, false)
        viewDataBinding = FragmentDocProgressBinding.bind(root).apply {
            this.viewmodel = this@DocProgressFragment.viewModel
        }
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        viewModel.start(args.documentStatus)
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setActivityTitle("Document Reference NO: ${args.documentStatus.documentReferenceNo}")

        //hide yes no buttons when stage is null
        viewModel.yesNoButtonsIsVisible.value =
            (args.documentStatus.statusId != null && args.documentStatus.stage == null)

        setupButtonsCallback()
    }

    private fun setupButtonsCallback() {
        viewModel.onBackPressEvent.observe(this.viewLifecycleOwner, EventObserver {
            findNavController().popBackStack()
        })
        viewModel.onYesPressEvent.observe(this.viewLifecycleOwner, EventObserver {
            (it as? ApiResult.Success)?.let {
                val action =
                    DocProgressFragmentDirections.actionDocProgressFragmentToSubmitFormFragment(
                        successful = it.response,
                        unsuccessful = null,
                        gpsNeeded = args.documentStatus.gpsIsNeeded == 1
                    )
                findNavController().tryNavigate(action)
            }
            (it as? ApiResult.Error)?.let { error -> btnBack.snack(error.errorHandling) }
        })
        viewModel.onNoPressEvent.observe(this.viewLifecycleOwner, EventObserver {
            (it as? ApiResult.Success)?.let {
                val action =
                    DocProgressFragmentDirections.actionDocProgressFragmentToSubmitFormFragment(
                        unsuccessful = it.response,
                        successful = null,
                        gpsNeeded = args.documentStatus.gpsIsNeeded == 1
                    )
                findNavController().tryNavigate(action)
            }
            (it as? ApiResult.Error)?.let { error -> btnBack.snack(error.errorHandling) }
        })
    }

}
