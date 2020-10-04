package rezaei.mohammad.plds.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.views.main.MainActivity

class NotificationBuilder(
    private val context: Context,
    private val title: String?,
    private val content: String?
) {
    private val notificationManager = NotificationManagerCompat.from(context)
    val ChannelName = "PLDS CheckIn"
    val ChannelID = "PLDS_CheckIn"
    val notificationChannel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationChannel(ChannelID, ChannelName, NotificationManager.IMPORTANCE_LOW)
    } else {
        null
    }

    val notificationId = 1223

    fun getNotification(): NotificationCompat.Builder {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(notificationChannel!!)
        }

        val mainActivityIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 223, mainActivityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val checkOut = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //we are running Nougat
            val intent = Intent(context, CheckInService::class.java)
            intent.action = CHECK_OUT
            PendingIntent.getService(context, 6589, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        } else {
            //we are running a version prior to Nougat
            mainActivityIntent.action = CHECK_OUT
            PendingIntent.getActivity(
                context,
                6589,
                mainActivityIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
        }

        val notification = NotificationCompat.Builder(context, ChannelID).apply {
            setSmallIcon(R.drawable.plds_not)
            color = ContextCompat.getColor(context, R.color.colorAccent)
            setContentTitle(title)
            setContentText(content)
            setOngoing(true)
            setOnlyAlertOnce(true)
            setCategory(NotificationCompat.CATEGORY_SERVICE)
            addAction(
                NotificationCompat.Action.Builder(
                    R.drawable.ic_check_out
                    , context.getString(R.string.check_out), checkOut
                ).build()
            )
            priority = NotificationCompat.PRIORITY_DEFAULT
            setContentIntent(pendingIntent)
        }
        return notification
    }

    fun setCheckingOutStatus() {
        notificationManager.notify(notificationId, getNotification()
            .setContentText("Checking out...")
            .also { it.mActions.clear() }.build()
        )
    }

    fun closeNotification() {
        notificationManager.cancel(notificationId)
    }

    companion object {
        const val CHECK_OUT = "CHECK_OUT"
    }
}