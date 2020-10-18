package rezaei.mohammad.plds.views.docListByLocation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import org.koin.androidx.viewmodel.ext.android.viewModel
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.databinding.FragmentDocListByLocationBinding
import rezaei.mohammad.plds.util.EventObserver
import rezaei.mohammad.plds.util.setActivityTitle
import rezaei.mohammad.plds.util.snack

class DocListByLocationFragment : Fragment() {

    private val viewModel: DocListByLocationViewModel by viewModel()
    private lateinit var viewDataBinding: FragmentDocListByLocationBinding
    private val args: DocListByLocationFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = FragmentDocListByLocationBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = viewModel
                lifecycleOwner = viewLifecycleOwner
            }
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setActivityTitle(getString(R.string.doc_list_on_location))
        setRecyclerView()
        setEventObserver()
        if (viewModel.documentList.value?.isEmpty() == true)
            viewModel.getDocuments(args.location)
    }

    private fun setRecyclerView() {
        val adapter = DocumentOnLocationAdapter(viewModel)
        viewDataBinding.listLocations.adapter = adapter
    }

    private fun setEventObserver() {
        viewModel.documentEvent.observe(viewLifecycleOwner, EventObserver {
            view?.snack(it)
        })
    }

}