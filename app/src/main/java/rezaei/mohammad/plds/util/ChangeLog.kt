package rezaei.mohammad.plds.util

import rezaei.mohammad.plds.data.preference.PreferenceManager


class ChangeLog(private val pref: PreferenceManager) {

    //Int versionCode String log
    private val logs: HashMap<Int, List<String>> = hashMapOf()

    init {
        logs[3] =
            listOf(
                "Check document step on Check Document Progress page.",
                "Fix user image bug.",
                "Add Username to user info dialog."
            )
        logs[10] = listOf(
            "Fix change theme bug.",
            "Fix toolbar show title bug."
        )
        logs[12] = listOf(
            "Add check-in and check-out process",
            "Add common action page",
            "Add document list by location page",
            "Add manage document page"
        )
        logs[16] = listOf(
            "Taking photos added",
            "Selecting photos taken added",
            "CaseNo & Plaintiff on the list of documents added",
            "Image in report issue added"
        )
        logs[17] = listOf(
                "Fixed the bug in the Report Issue"
        )
    }

    fun getLogFor(versionCode: Int): List<String>? {
        val lastVersionLog = pref.lastVersionChangeLog
        if (lastVersionLog == versionCode)
            return null

        pref.lastVersionChangeLog = versionCode

        return logs[versionCode]
    }
}