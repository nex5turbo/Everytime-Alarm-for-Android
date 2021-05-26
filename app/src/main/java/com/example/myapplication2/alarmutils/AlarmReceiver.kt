package com.example.myapplication2.alarmutils

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Build
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.myapplication2.R
import com.example.myapplication2.activities.AlarmReceiveActivity
import com.example.myapplication2.dbutils.DBFunction
import com.example.myapplication2.dbutils.DBHelper
import java.lang.Exception
import java.util.*

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        const val TAG = "AlarmReceiver"
        const val NOTIFICATION_ID = 101
        const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
    }

    private var sCpuWakeLock: PowerManager.WakeLock? = null
    private var sWifiLock: WifiManager.WifiLock? = null
    private lateinit var notificationManager: NotificationManager

    override fun onReceive(context: Context, intent: Intent) {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val km = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (!km.isKeyguardLocked || pm.isInteractive) { //잠금 안걸린 상태거나 화면 켜진 상태
            deliverNotification(context)
        } else {
            val alarmIntent = Intent(context, AlarmReceiveActivity::class.java)
            alarmIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            alarmIntent.action = Intent.ACTION_MAIN
            alarmIntent.addCategory(Intent.CATEGORY_LAUNCHER)

            val pIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            pIntent.send()

            deliverNotification(context, alarmIntent)
        }
    }

    private fun createNextAlarm(context: Context){
        val dbHelper = DBHelper(context, "mydb.db", null, 1)
        val database = dbHelper.writableDatabase
        val db = DBFunction(database)

        val day = getDay()
        val time = db.getTime(day)

        val preferences = context.getSharedPreferences("isFirst", Activity.MODE_PRIVATE)
        val preTime = preferences.getInt("preTime", 30)

        AlarmFunction.setAlarm(day, time, context, preTime)
    }

    private fun deliverNotification(context: Context) {
        val builder =
                NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("학교 갈 시간이에요!")
                        .setContentText("서둘러서 준비해요!")
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setVibrate(longArrayOf(200,100,200))
                        .setAutoCancel(false)
                        .setDefaults(Notification.DEFAULT_ALL)
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    private fun deliverNotification(context: Context, intent:Intent) {
        val pIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, intent, PendingIntent.FLAG_NO_CREATE)
        val builder =
                NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("학교 갈 시간이에요!")
                        .setContentText("서둘러서 준비해요!")
                        .setContentIntent(pIntent)
                        .setPriority(NotificationCompat.PRIORITY_LOW)
                        .setAutoCancel(false)
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
    private fun getDay(): Int = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul")).get(Calendar.DAY_OF_WEEK)
}