package rezaei.mohammad.plds.views.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.layout_environment.*
import kotlinx.android.synthetic.main.layout_environment.view.*
import org.koin.android.ext.android.inject
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.local.Environment
import rezaei.mohammad.plds.data.preference.PreferenceManager
import kotlin.system.exitProcess

class LoginActivity : AppCompatActivity() {

    private val prefs: PreferenceManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportFragmentManager.beginTransaction()
            .replace(container.id, LoginFragment.newInstance())
            .commit()

        imgLogo.setOnLongClickListener {
            setupChangeUrlDialog()
            false
        }
    }

    private fun setupChangeUrlDialog() {
        var selectEnvironment = prefs.activeEnvironment
        val dialog = MaterialDialog(this).show {
            title(text = getString(R.string.environments))
            customView(R.layout.layout_environment, scrollable = true)
            positiveButton(text = getString(R.string.save_and_restart)) {
                val view = it.getCustomView() as ViewGroup
                val inputLoginUrl = view.inputLoginUrl
                val inputMainUrl = view.inputBaseUrl

                var inputsInvalid = false
                if (!Patterns.WEB_URL.matcher(inputLoginUrl.editText?.text.toString()).matches()) {
                    inputLoginUrl.error = getString(R.string.not_valid)
                    inputsInvalid = true
                }
                if (!Patterns.WEB_URL.matcher(inputMainUrl.editText?.text.toString()).matches()) {
                    inputBaseUrl.error = getString(R.string.not_valid)
                    inputsInvalid = true
                }


                if (!inputsInvalid) {
                    prefs.setEnvironment(
                        selectEnvironment,
                        inputLoginUrl.editText?.text.toString(),
                        inputMainUrl.editText?.text.toString()
                    )

                    startActivity(Intent(this@LoginActivity, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                    exitProcess(0)
                }
            }
            noAutoDismiss()
        }

        val view = dialog.getCustomView() as ViewGroup
        val inputLoginUrl = view.inputLoginUrl
        val inputMainUrl = view.inputBaseUrl
        val radgEnvironment = view.radgEnvironment

        val currentEnvironment: Int = when (prefs.activeEnvironment) {
            Environment.Live -> R.id.radLive
            Environment.Dev -> R.id.radDev
            Environment.Uat -> R.id.radUAT
        }
        radgEnvironment.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radLive -> {
                    inputLoginUrl.editText?.setText(prefs.liveLoginURL)
                    inputMainUrl.editText?.setText(prefs.liveBaseURL)
                    selectEnvironment = Environment.Live
                }
                R.id.radDev -> {
                    inputLoginUrl.editText?.setText(prefs.devLoginURL)
                    inputMainUrl.editText?.setText(prefs.devBaseURL)
                    selectEnvironment = Environment.Dev
                }
                R.id.radUAT -> {
                    inputLoginUrl.editText?.setText(prefs.uatLoginURL)
                    inputMainUrl.editText?.setText(prefs.uatBaseURL)
                    selectEnvironment = Environment.Uat
                }
            }
            inputLoginUrl.isErrorEnabled = false
            inputMainUrl.isErrorEnabled = false
        }
        radgEnvironment.check(currentEnvironment)
    }
}
