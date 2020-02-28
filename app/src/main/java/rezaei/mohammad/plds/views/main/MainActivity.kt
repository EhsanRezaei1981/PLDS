package rezaei.mohammad.plds.views.main

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.afollestad.materialdialogs.MaterialDialog
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import rezaei.mohammad.plds.BuildConfig
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.preference.PreferenceManager
import rezaei.mohammad.plds.util.ChangeLog
import rezaei.mohammad.plds.views.login.LoginActivity
import rezaei.mohammad.plds.views.loginInfo.LoginInfoFragment

class MainActivity : AppCompatActivity() {

    private val viewModel: GlobalViewModel by viewModel()
    private val prefs: PreferenceManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        showChangeLog()
    }

    private fun showChangeLog() {
        val changeLog: ChangeLog by inject()
        val logs = changeLog.getLogFor(BuildConfig.VERSION_CODE)
        logs?.let {
            MaterialDialog(this).show {
                title(text = "Changes Of Version ${BuildConfig.VERSION_NAME}")
                message(text = it.joinToString("\n\n"))
                positiveButton(text = "Close")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        when(prefs.nighMode){
            AppCompatDelegate.MODE_NIGHT_NO -> {
                menu?.findItem(R.id.theme_day)?.isChecked = true
            }
            AppCompatDelegate.MODE_NIGHT_YES -> {
                menu?.findItem(R.id.theme_night)?.isChecked = true
            }
            else -> {
                menu?.findItem(R.id.theme_default)?.isChecked = true
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_login_info -> {
                LoginInfoFragment().show(supportFragmentManager, null)
            }
            R.id.action_sign_out -> {
                MaterialDialog(this).show {
                    title(R.string.sign_out)
                    message(R.string.sign_out_question)
                    positiveButton(R.string.no)
                    negativeButton(R.string.yes) {
                        viewModel.signOut()
                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                        finish()
                    }
                }
            }
            R.id.theme_default -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                prefs.nighMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
            R.id.theme_day -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                prefs.nighMode = AppCompatDelegate.MODE_NIGHT_NO
            }
            R.id.theme_night -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                prefs.nighMode = AppCompatDelegate.MODE_NIGHT_YES
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
