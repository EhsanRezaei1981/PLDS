package rezaei.mohammad.plds.views.login

import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.ext.android.inject
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.preference.PreferenceManager

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
        MaterialDialog(this).show {
            title(text = "Change base url")
            input(
                hint = "Base URL", allowEmpty = false, prefill = prefs.baseURL,
                waitForPositiveButton = true
            ) { dialog, text ->
                if (!Patterns.WEB_URL.matcher(text).matches()) {
                    dialog.getInputField().error = "Not valid"
                } else {
                    prefs.baseURL = text.toString()
                    finish()
                }
            }
            positiveButton(text = "Save and exit")
            noAutoDismiss()
        }
    }
}
