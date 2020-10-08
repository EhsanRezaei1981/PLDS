package rezaei.mohammad.plds.views.main

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.afollestad.materialdialogs.MaterialDialog
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import rezaei.mohammad.plds.BuildConfig
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.local.Environment
import rezaei.mohammad.plds.data.preference.PreferenceManager
import rezaei.mohammad.plds.service.CheckInService
import rezaei.mohammad.plds.util.ChangeLog
import rezaei.mohammad.plds.util.EventObserver
import rezaei.mohammad.plds.util.snack
import rezaei.mohammad.plds.views.login.LoginActivity
import rezaei.mohammad.plds.views.loginInfo.LoginInfoFragment


class MainActivity : AppCompatActivity() {

    private val viewModel: GlobalViewModel by viewModel()
    private val prefs: PreferenceManager by inject()
    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(p0: ComponentName?) {
            isBound = false
            viewModel.checkInService.value = null
        }

        override fun onServiceConnected(className: ComponentName?, service: IBinder?) {
            val binder = service as CheckInService.CheckInBinder
            viewModel.checkInService.value = binder.service
            isBound = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //start service
        if (!isBound)
            Intent(this, CheckInService::class.java).also { intent ->
                startService(intent)
            }

        setSupportActionBar(toolbar)
        showChangeLog()
        showEnvironment()
        setupServiceValuesObservers()

        findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener { controller, destination, arguments ->
            hideNote()
            if (destination.label == "fragment_main") {
                viewModel.docRefNo.postValue(null)
            }
        }
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

    private fun showEnvironment() {
        if (prefs.activeEnvironment != Environment.Live)
            toolbar.setTitleTextColor(
                if (prefs.activeEnvironment == Environment.Uat)
                    resources.getColor(R.color.colorWarning)
                else
                    resources.getColor(R.color.colorFail)
            )
//            supportActionBar?.subtitle = "${prefs.activeEnvironment.name.toUpperCase()} Environment"
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        when (prefs.nighMode) {
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
        menu?.findItem(R.id.action_checkOut)?.isVisible = viewModel.checkInService.value
            ?.isCheckedIn?.value ?: false
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
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
            R.id.action_checkOut -> {
                viewModel.checkInService.value?.checkOut()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun enableTransition() {
        val autoTransition = AutoTransition()
        autoTransition.excludeChildren(toolbar, true)
        TransitionManager.beginDelayedTransition(appBar, autoTransition)
    }

    fun showNote() {
        enableTransition()
        txtNote.visibility = View.VISIBLE
    }

    fun hideNote() {
        enableTransition()
        txtNote.visibility = View.GONE
    }

    private fun bindService() {
        if (!isBound) {
            Intent(this, CheckInService::class.java).also { intent ->
                bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }
        }
    }

    private fun setupServiceValuesObservers() {
        viewModel.checkInService.observe(this) { checkInService ->
            if (checkInService == null)
                return@observe

            checkInService.isCheckedIn.observe(this) {
                setActivityCheckInStatus(it)

                if (!it)// on checkout
                    findNavController(R.id.nav_host_fragment)
                        .popBackStack(R.id.mainActivityFragment, false)
            }

            checkInService.errorHandling.observe(this, EventObserver {
                toolbar?.snack(it)
            })
        }
    }

    private fun setActivityCheckInStatus(isCheckedIn: Boolean) {
        toolbar?.menu?.findItem(R.id.action_checkOut)?.isVisible = isCheckedIn
        if (isCheckedIn) {
            supportActionBar?.subtitle = viewModel.checkInService.value
                ?.checkedInLocation?.value?.locationName
        } else {
            supportActionBar?.subtitle = null
        }
    }

    fun unbindService() {
        if (!isChangingConfigurations && isBound) {
            unbindService(connection)
            isBound = false
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService()
    }

    override fun onStart() {
        super.onStart()
        bindService()
    }

}
