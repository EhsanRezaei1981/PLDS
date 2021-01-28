package rezaei.mohammad.plds.data.preference

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import rezaei.mohammad.plds.data.local.Environment


class PreferenceManager(context: Context) {
    private val pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    var username: String?
        get() {
            return pref.getString("USERNAME", null)
        }
        set(value) {
            pref.edit { putString("USERNAME", value) }
        }

    var password: String?
        get() {
            return pref.getString("PASSWORD", null)
        }
        set(value) {
            pref.edit { putString("PASSWORD", value) }
        }

    var authToken: String?
        get() {
            return pref.getString("AUTH_TOKEN", null)
        }
        set(value) {
            pref.edit { putString("AUTH_TOKEN", value) }
        }

    var liveBaseURL: String
        get() {
            return pref.getString("LIVE_BASE_URL", "https://api.plds.co.za/")!!
        }
        set(value) {
            pref.edit(commit = true) { putString("LIVE_BASE_URL", value) }
        }

    var liveLoginURL: String
        get() {
            return pref.getString("LIVE_LOGIN_URL", "https://Jarvis.Prosource.co.za/")!!
        }
        set(value) {
            pref.edit(commit = true) { putString("LIVE_LOGIN_URL", value) }
        }

    var uatBaseURL: String
        get() {
            return pref.getString("UAT_BASE_URL", "https://apiuat.plds.co.za/")!!
        }
        set(value) {
            pref.edit(commit = true) { putString("UAT_BASE_URL", value) }
        }

    var uatLoginURL: String
        get() {
            return pref.getString("UAT_LOGIN_URL", "https://JarvisUAT.Prosource.co.za/")!!
        }
        set(value) {
            pref.edit(commit = true) { putString("UAT_LOGIN_URL", value) }
        }

    var devBaseURL: String
        get() {
            return pref.getString("DEV_BASE_URL", "https://apidev.plds.co.za/")!!
        }
        set(value) {
            pref.edit(commit = true) { putString("DEV_BASE_URL", value) }
        }

    var devLoginURL: String
        get() {
            return pref.getString("DEV_LOGIN_URL", "https://JarvisDEV.Prosource.co.za/")!!
        }
        set(value) {
            pref.edit(commit = true) { putString("DEV_LOGIN_URL", value) }
        }

    var activeEnvironment: Environment
        get() {
            return Environment.valueOf(
                pref.getString(
                    "ACTIVE_ENVIRONMENT",
                    Environment.Live.name
                )!!
            )
        }
        set(value) {
            pref.edit(commit = true) { putString("ACTIVE_ENVIRONMENT", value.name) }
        }

    fun setEnvironment(environment: Environment, loginUrl: String, mainUrl: String) {
        activeEnvironment = environment
        when (environment) {
            Environment.Live -> {
                liveLoginURL = loginUrl
                liveBaseURL = mainUrl
            }
            Environment.Dev -> {
                devLoginURL = loginUrl
                devBaseURL = mainUrl
            }
            Environment.Uat -> {
                uatLoginURL = loginUrl
                uatBaseURL = mainUrl
            }
        }
    }

    /**
     * The first element is login url and the second is base url
     */
    fun getActiveEnvironment(): Pair<String, String> {
        return when (activeEnvironment) {
            Environment.Live -> Pair(liveLoginURL, liveBaseURL)
            Environment.Dev -> Pair(devLoginURL, devBaseURL)
            Environment.Uat -> Pair(uatLoginURL, uatBaseURL)
        }
    }

    var lastVersionChangeLog: Int
        get() {
            return pref.getInt("LAST_VERSION_LOG", 0)
        }
        set(value) {
            pref.edit { putInt("LAST_VERSION_LOG", value) }
        }

    var nighMode: Int
        get() {
            return pref.getInt("NIGHT_MODE", AppCompatDelegate.MODE_NIGHT_UNSPECIFIED)
        }
        set(value) {
            pref.edit { putInt("NIGHT_MODE", value) }
        }

}