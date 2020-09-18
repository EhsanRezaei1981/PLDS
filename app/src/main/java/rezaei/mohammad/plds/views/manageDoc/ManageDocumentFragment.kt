package rezaei.mohammad.plds.views.manageDoc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.manage_document_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.ApiResult
import rezaei.mohammad.plds.databinding.ManageDocumentFragmentBinding
import rezaei.mohammad.plds.util.EventObserver
import rezaei.mohammad.plds.util.setActivityTitle
import rezaei.mohammad.plds.util.snack
import rezaei.mohammad.plds.views.main.GlobalViewModel

class ManageDocumentFragment : Fragment() {

    private val globalViewModel: GlobalViewModel by sharedViewModel()
    private val viewModel: ManageDocumentViewModel by viewModel {
        parametersOf(globalViewModel.docRefNo)
    }
    private lateinit var viewDataBinding: ManageDocumentFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setActivityTitle(getString(R.string.manage_document))
        viewDataBinding = ManageDocumentFragmentBinding
            .inflate(inflater, container, false)
            .apply {
                this.viewmodel = viewModel
            }
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btnReadQR.setOnClickListener {
            navigateToQrScanner()
        }
        setupDocumentInfoReceiver()
    }

    private fun navigateToQrScanner() {
        findNavController().navigate(
            ManageDocumentFragmentDirections
                .actionManageDocumentFragmentToQrReaderFragment()
        )
    }

    private fun setupDocumentInfoReceiver() {
        viewModel.documentBaseInfo.observe(this.viewLifecycleOwner, EventObserver {
            when (it) {
                is ApiResult.Success -> {
                    it.response.data?.let {
                        findNavController()
                            .navigate(
                                ManageDocumentFragmentDirections
                                    .actionManageDocumentFragmentToDocumentStatusHistoryFragment(it)
                            )
                    }
                }
                is ApiResult.Error -> {
                    btnTackingHistory.snack(it.errorHandling)
                }
            }
        })
    }

}