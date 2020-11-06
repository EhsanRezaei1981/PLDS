package rezaei.mohammad.plds

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import rezaei.mohammad.plds.data.preference.PreferenceManager
import rezaei.mohammad.plds.di.Module
import java.util.*

class PLDSapp : MultiDexApplication() {

    companion object {
        const val VERSION_NAME = BuildConfig.VERSION_NAME
        const val APPLICATION_ID = BuildConfig.APPLICATION_ID
        val userAgent by lazy {
            String.format(
                Locale.US,
                " (Android %s %s; %s; %s %s;)",
                VERSION_NAME,
                Build.VERSION.RELEASE,
                Build.MODEL,
                Build.BRAND,
                Build.DEVICE
            )
        }
    }


    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PLDSapp)
            modules(Module.pldsModule)
        }
        setDefaultTheme()
    }

    private fun setDefaultTheme(){
        val prefs: PreferenceManager by inject()
        when(prefs.nighMode){
            AppCompatDelegate.MODE_NIGHT_NO -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            AppCompatDelegate.MODE_NIGHT_YES -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            else -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }
}