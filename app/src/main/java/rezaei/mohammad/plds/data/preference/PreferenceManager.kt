package rezaei.mohammad.plds.data.preference

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import rezaei.mohammad.plds.BuildConfig


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

    var baseURL: String
        get() {
            return pref.getString("BASE_URL", BuildConfig.BaseUrl) ?: BuildConfig.BaseUrl
        }
        set(value) {
            pref.edit(commit = true) { putString("BASE_URL", value) }
        }

    var lastVersionChangeLog: Int
        get() {
            return pref.getInt("LAST_VERSION_LOG", 0)
        }
        set(value) {
            pref.edit { putInt("LAST_VERSION_LOG", value) }
        }

}