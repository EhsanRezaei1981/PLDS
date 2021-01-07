package rezaei.mohammad.plds.views.main

import android.Manifest
import android.app.ProgressDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
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
import com.yayandroid.locationmanager.LocationManager
import com.yayandroid.locationmanager.configuration.DefaultProviderConfiguration
import com.yayandroid.locationmanager.configuration.GooglePlayServicesConfiguration
import com.yayandroid.locationmanager.configuration.LocationConfiguration
import com.yayandroid.locationmanager.configuration.PermissionConfiguration
import com.yayandroid.locationmanager.listener.LocationListener
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import rezaei.mohammad.plds.BuildConfig
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.ApiResult
import rezaei.mohammad.plds.data.local.Environment
import rezaei.mohammad.plds.data.model.request.Gps
import rezaei.mohammad.plds.data.model.response.CheckInResponse
import rezaei.mohammad.plds.data.model.response.ErrorHandling
import rezaei.mohammad.plds.data.preference.PreferenceManager
import rezaei.mohammad.plds.service.CheckInService
import rezaei.mohammad.plds.service.CheckInViewCallbacks
import rezaei.mohammad.plds.util.ChangeLog
import rezaei.mohammad.plds.util.snack
import rezaei.mohammad.plds.views.checkin.CheckInFragment
import rezaei.mohammad.plds.views.login.LoginActivity
import rezaei.mohammad.plds.views.loginInfo.LoginInfoFragment


class MainActivity : AppCompatActivity(), CheckInViewCallbacks {

    private val viewModel: GlobalViewModel by viewModel()
    private val prefs: PreferenceManager by inject()
    private var isBound = false
    var checkInService: CheckInService? = null
    private var gps: Gps? = null
    private val progressDialog: ProgressDialog by lazy {
        ProgressDialog(this).apply {
            this.setTitle(R.string.loading)
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(p0: ComponentName?) {
            isBound = false
            checkInService = null
        }

        override fun onServiceConnected(className: ComponentName?, service: IBinder?) {
            val binder = service as CheckInService.CheckInBinder
            checkInService = binder.service
            checkInService!!.viewCallbacks = this@MainActivity
            isBound = true
            checkCheckedIn()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startService()
        viewModel.findIfAnyCheckInExist()

        setSupportActionBar(toolbar)
        showChangeLog()
        showEnvironment()
        setOnResetCheckInStatusResult()

        findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener { controller, destination, arguments ->
            hideNote()
            if (destination.label == "fragment_main") {
                viewModel.docRefNo.postValue(null)
            }
        }
    }

    private fun startService() {
        viewModel.checkInResponseEntity.observe(this) {
            //start service
            if (!isBound)
                Intent(this, CheckInService::class.java).also { intent ->
                    if (it != null)
                        intent.action = CheckInService.RESUME_PREVIOUS_CHECK_IN
                    startService(intent)
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
        menu?.findItem(R.id.action_checkOut)?.isVisible = checkInService
            ?.isCheckedIn ?: false
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
                        if (checkInService?.isCheckedIn == true) {
                            this@MainActivity.toolbar.snack(
                                ErrorHandling(
                                    true,
                                    errorMessage = getString(R.string.please_check_out_first),
                                    isSuccessful = false
                                )
                            )
                        } else {
                            viewModel.signOut()
                            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                            finish()
                        }
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
                showLoading()
                checkInService?.checkOut()
            }
            R.id.action_reser_check_in -> {
                initGps()
                MaterialDialog(this).show {
                    title(R.string.reset_check_in_title)
                    message(R.string.reset_check_in_message)
                    noAutoDismiss()
                    positiveButton(R.string.reset) {
                        if (gps == null) {
                            initGps()
                            this@MainActivity.toolbar.snack(
                                ErrorHandling(
                                    true,
                                    errorMessage = getString(R.string.gps_not_available),
                                    isSuccessful = false
                                )
                            )
                            return@positiveButton
                        }
                        dismiss()
                        checkInService?.setCheckedOut()
                        viewModel.resetCheckIn(gps!!)
                        showLoading()
                    }
                    negativeButton(R.string.cancel) {
                        dismiss()
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setOnResetCheckInStatusResult() {
        viewModel.resetCheckInResult.observe(this) {
            (it as? ApiResult.Error)?.let { toolbar.snack(it.errorHandling) }
            (it as? ApiResult.Success)?.let {

                toolbar.snack(it.response.errorHandling)
                onCheckedOut()
            }
            hideLoading()
        }
    }

    private fun showLoading() {
        try {
            if (!progressDialog.isShowing)
                progressDialog.show()
        } catch (e: Exception) {
        }
    }

    private fun hideLoading() {
        try {
            if (progressDialog.isShowing)
                progressDialog.dismiss()
        } catch (e: Exception) {
        }
    }

    private fun initGps() {
        val awesomeConfiguration = LocationConfiguration.Builder()
            .keepTracking(false)
            .askForPermission(
                PermissionConfiguration.Builder()
                    .rationaleMessage(getString(R.string.accept_loc_permission))
                    .requiredPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
                    .build()
            )
            .useGooglePlayServices(
                GooglePlayServicesConfiguration.Builder()
                    .fallbackToDefault(true)
                    .askForGooglePlayServices(false)
                    .askForSettingsApi(true)
                    .failOnSettingsApiSuspended(false)
                    .ignoreLastKnowLocation(false)
                    .build()
            )
            .useDefaultProviders(
                DefaultProviderConfiguration.Builder()
                    .build()
            )
            .build()
        val manager = LocationManager.Builder(this.applicationContext)
            .activity(this)
            .configuration(awesomeConfiguration)
            .notify(object : LocationListener {
                override fun onLocationChanged(location: Location?) {
                    if (location?.latitude != null)
                        gps = Gps(location.latitude, location.longitude)
                }

                override fun onPermissionGranted(alreadyHadPermission: Boolean) {
                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                }

                override fun onProviderEnabled(provider: String?) {
                }

                override fun onProviderDisabled(provider: String?) {
                }

                override fun onProcessTypeChanged(processType: Int) {
                }

                override fun onLocationFailed(type: Int) {
                }
            })
            .build()
        if (!manager.isAnyDialogShowing)
            manager.get()
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

    private fun checkCheckedIn() {
        if (checkInService?.isCheckedIn == true) {
            toolbar?.menu?.findItem(R.id.action_checkOut)?.isVisible = true
            supportActionBar?.subtitle = checkInService?.checkedInLocation?.locationName
        }
    }

    override fun onCheckedIn(checkedInLocation: CheckInResponse.Data) = runOnUiThread {
        checkCheckedIn()
        getCheckInFragment()?.onCheckedIn(checkedInLocation)
    }

    override fun onCheckedOut() = runOnUiThread {
        toolbar?.menu?.findItem(R.id.action_checkOut)?.isVisible = false
        supportActionBar?.subtitle = null
        findNavController(R.id.nav_host_fragment)
            .popBackStack(R.id.mainActivityFragment, false)
        hideLoading()
    }

    override fun showLocationList(locationList: List<CheckInResponse.LocationItem>) =
        runOnUiThread {
            getCheckInFragment()?.showLocationList(locationList)
        }

    override fun onNoLocationFound() = runOnUiThread {
        getCheckInFragment()?.onNoLocationFound()
    }

    override fun onError(errorHandling: ErrorHandling?) = runOnUiThread {
        toolbar?.snack(errorHandling)
        hideLoading()
    }

    fun unbindService() {
        if (!isChangingConfigurations && isBound) {
            unbindService(connection)
            isBound = false
        }
    }

    override fun onResume() {
        super.onResume()
        bindService()
    }

    override fun onPause() {
        super.onPause()
        unbindService()
    }

    private fun getCheckInFragment(): CheckInFragment? {
        return supportFragmentManager
            .fragments[0]
            .childFragmentManager
            .fragments.find { it is CheckInFragment } as? CheckInFragment
    }
}
