package rezaei.mohammad.plds.views.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragmen_login.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.Result
import rezaei.mohammad.plds.databinding.FragmenLoginBinding
import rezaei.mohammad.plds.util.EventObserver
import rezaei.mohammad.plds.views.main.MainActivity

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private val viewModel: LoginViewModel by viewModel()

    private lateinit var viewDataBinding: FragmenLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragmen_login, container, false)
        viewDataBinding = FragmenLoginBinding.bind(root).apply {
            this.viewmodel = viewModel
        }
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupForLoginResponse()
    }


    private fun setupForLoginResponse() {
        viewModel.loginResultEvent.observe(this, EventObserver {
            (it as? Result.Success)?.let { loginSuccess() }
            (it as? Result.Error)?.let { error -> showSnackBar(error.errorHandling?.errorMessage) }
        })
    }

    private fun showSnackBar(message: String?) {
        Snackbar.make(btnLogin, message ?: "Unknown error", Snackbar.LENGTH_LONG).show()
    }

    private fun loginSuccess() {
        startActivity(Intent(requireContext(), MainActivity::class.java))
    }

}
