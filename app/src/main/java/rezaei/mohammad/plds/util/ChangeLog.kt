package rezaei.mohammad.plds.util

import rezaei.mohammad.plds.data.preference.PreferenceManager


class ChangeLog(private val pref: PreferenceManager) {

    //Int versionCode String log
    private val logs: HashMap<Int, List<String>> = hashMapOf()

    init {
        logs[3] =
            listOf("Check document step on Check Document Progress page.", "Fix user image bug.")
    }

    fun getLogFor(versionCode: Int): List<String>? {
        val lastVersionLog = pref.lastVersionChangeLog
        if (lastVersionLog == versionCode)
            return null

        pref.lastVersionChangeLog = versionCode

        return logs[versionCode]
    }
}