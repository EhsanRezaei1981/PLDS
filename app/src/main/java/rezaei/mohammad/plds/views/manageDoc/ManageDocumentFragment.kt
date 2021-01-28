package rezaei.mohammad.plds.views.manageDoc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_manage_document.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.ApiResult
import rezaei.mohammad.plds.databinding.FragmentManageDocumentBinding
import rezaei.mohammad.plds.util.EventObserver
import rezaei.mohammad.plds.util.setActivityTitle
import rezaei.mohammad.plds.util.snack
import rezaei.mohammad.plds.util.tryNavigate
import rezaei.mohammad.plds.views.main.GlobalViewModel

class ManageDocumentFragment : Fragment() {

    private val globalViewModel: GlobalViewModel by sharedViewModel()
    private val viewModel: ManageDocumentViewModel by viewModel {
        parametersOf(globalViewModel.docRefNo)
    }
    private lateinit var viewDataBinding: FragmentManageDocumentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setActivityTitle(getString(R.string.manage_document))
        viewDataBinding = FragmentManageDocumentBinding
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
        findNavController().tryNavigate(
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
                            .tryNavigate(
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