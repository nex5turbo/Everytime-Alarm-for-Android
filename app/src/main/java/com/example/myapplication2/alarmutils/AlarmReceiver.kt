package com.example.myapplication2.alarmutils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.myapplication2.R
import com.example.myapplication2.activities.AlarmReceiveActivity
import com.example.myapplication2.dbutils.DBFunction
import com.example.myapplication2.dbutils.DBHelper
import java.util.*

@Suppress("DEPRECATION")
class AlarmReceiver : BroadcastReceiver() {
    companion object {
        const val TAG = "AlarmReceiver"
        const val NOTIFICATION_ID = 0
        const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
    }

    private var sCpuWakeLock: PowerManager.WakeLock? = null
    private var sWifiLock: WifiManager.WifiLock? = null
    private var manager: ConnectivityManager? = null
    private lateinit var notificationManager: NotificationManager

    override fun onReceive(context: Context, intent: Intent) {
        createNextAlarm(context)
        Log.d(TAG, "Received intent : $intent")
        if (sCpuWakeLock != null) {
            return
        }

        if (sWifiLock != null) {
            return
        }

        val wifiManager = context.applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        sWifiLock = wifiManager.createWifiLock("wifilock")
        sWifiLock!!.setReferenceCounted(true)
        sWifiLock!!.acquire()

        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager

        sCpuWakeLock = pm.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK or
                        PowerManager.ACQUIRE_CAUSES_WAKEUP or
                        PowerManager.ON_AFTER_RELEASE, "app:alarm")

        sCpuWakeLock!!.acquire()


        manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager


        val alarmIntent = Intent("android.intent.action.sec")

        alarmIntent.setClass(context, AlarmReceiveActivity::class.java)
        alarmIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        // 액티비티를 띄운다

        context.startActivity(alarmIntent)

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
        val contentIntent = Intent(context, AlarmReceiveActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(
                context,
                NOTIFICATION_ID,
                contentIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder =
                NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle("Alert")
                        .setContentText("This is repeating alarm")
                        .setContentIntent(contentPendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                    PRIMARY_CHANNEL_ID,
                    "Stand up notification",
                    NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "AlarmManager Tests"
            notificationManager.createNotificationChannel(
                    notificationChannel)
        }
    }

    private fun getDay(): Int = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul")).get(Calendar.DAY_OF_WEEK)
}

//        notificationManager = context.getSystemService(
//                Context.NOTIFICATION_SERVICE) as NotificationManager
