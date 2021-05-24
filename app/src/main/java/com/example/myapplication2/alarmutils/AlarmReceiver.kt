package com.example.myapplication2.alarmutils

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.example.myapplication2.R
import com.example.myapplication2.activities.AlarmReceiveActivity
import com.example.myapplication2.dbutils.DBFunction
import com.example.myapplication2.dbutils.DBHelper
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
        createNextAlarm(context)

        if (sCpuWakeLock != null) {
            return
        }

        if (sWifiLock != null) {
            return
        }

        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        sWifiLock = wifiManager.createWifiLock("wifilock")
        sWifiLock!!.setReferenceCounted(true)
        sWifiLock!!.acquire()

        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager

        sCpuWakeLock = pm.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK or
                        PowerManager.ACQUIRE_CAUSES_WAKEUP or
                        PowerManager.ON_AFTER_RELEASE, "app:alarm")

        sCpuWakeLock!!.acquire()

        val km = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (!km.isKeyguardLocked || pm.isInteractive) { //잠금 안걸린 상태거나 화면 켜진 상태
            notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            deliverNotification(context)
        } else {
            val alarmIntent = Intent("android.intent.action.sec")

            alarmIntent.setClass(context, AlarmReceiveActivity::class.java)
            alarmIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            context.startActivity(alarmIntent)
        }

        if(sWifiLock != null) {
            sWifiLock!!.release()
            sWifiLock = null
        }

        if (sCpuWakeLock != null) {
            sCpuWakeLock!!.release()
            sCpuWakeLock = null
        }
    }

    private fun createNextAlarm(context: Context){
        val dbHelper = DBHelper(context, "mydb.db", null, 1)
        val database = dbHelper.writableDatabase
        val db = DBFunction(database)

        val day = getDay()
        val time = db.getTime(day)
        val preTime = db.getPreTime()

        AlarmFunction.setAlarm(day, time, context, preTime)
    }

    private fun deliverNotification(context: Context) {
        val builder =
                NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("잠시후에 1교시 수업이 시작돼요!")
                        .setContentText("서둘러서 준비해요!")
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setAutoCancel(false)
                        .setDefaults(Notification.DEFAULT_ALL)
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    private fun getDay(): Int = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul")).get(Calendar.DAY_OF_WEEK)
}