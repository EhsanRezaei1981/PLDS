package rezaei.mohammad.plds.views.addMultiDoc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.add_multi_doc_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.databinding.AddMultiDocFragmentBinding

class AddMultiDocFragment : Fragment() {

    private val viewModel: AddMultiDocViewModel by viewModel()
    private lateinit var viewDataBinding: AddMultiDocFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.add_multi_doc_fragment, container, false)
        viewDataBinding = AddMultiDocFragmentBinding.bind(root).apply {
            this.viewmodel = this@AddMultiDocFragment.viewModel
        }
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btnReadQR.setOnClickListener {
            navigateToQrScanner()
        }
    }

    private fun navigateToQrScanner() {
        val action =
            AddMultiDocFragmentDirections.actionAddMultiDocFragmentToQrReaderFragment()
        findNavController().navigate(action)
    }

}
