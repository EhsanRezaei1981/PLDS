package rezaei.mohammad.plds.data.preference

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager


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

}