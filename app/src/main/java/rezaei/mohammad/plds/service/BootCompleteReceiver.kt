package rezaei.mohammad.plds.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import rezaei.mohammad.plds.PLDSapp
import rezaei.mohammad.plds.data.LocalRepository

class BootCompleteReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(this::class.java.simpleName, intent.action ?: "")
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
/*            val localRepository: LocalRepository? by (context.applicationContext as? PLDSapp)
                ?.inject<rezaei.mohammad.plds.data.local.LocalRepository>()

            GlobalScope.launch {
                if (localRepository?.getCheckInResponse() != null)
                    Intent(context, CheckInService::class.java).also { intent ->
                        intent.action = CheckInService.RESUME_PREVIOUS_CHECK_IN
                        context.startService(intent)
                    }
            }*/
        }
    }
}
