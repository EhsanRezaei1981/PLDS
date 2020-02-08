package rezaei.mohammad.plds.views.loginInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.login_info_fragment.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import rezaei.mohammad.plds.data.preference.PreferenceManager
import rezaei.mohammad.plds.databinding.LoginInfoFragmentBinding

class LoginInfoFragment : DialogFragment() {

    private val viewModel: LoginInfoViewModel by viewModel()
    private val prefs: PreferenceManager by inject()
    private lateinit var viewDataBinding: LoginInfoFragmentBinding

    override fun onStart() {
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        super.onStart()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = LoginInfoFragmentBinding.inflate(inflater, container, false).apply {
            this.viemodel = viewModel
            this.txtUserName.text = "Username: ${prefs.username}"
        }
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btnClose.setOnClickListener { dismiss() }
    }

}
