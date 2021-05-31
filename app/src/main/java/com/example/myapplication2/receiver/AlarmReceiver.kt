package com.example.myapplication2.receiver

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import com.example.myapplication2.R
import com.example.myapplication2.activities.AlarmReceiveActivity
import com.example.myapplication2.alarmutils.AlarmFunction
import com.example.myapplication2.dbutils.DBFunction
import com.example.myapplication2.dbutils.DBHelper
import java.util.*

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        const val NOTIFICATION_ID = 101
        const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
    }

    private lateinit var notificationManager: NotificationManager

    override fun onReceive(context: Context, intent: Intent) {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val isScreenOn = pm.isInteractive
        val km = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        Log.d("###", "${km.isKeyguardLocked}, ${km.isKeyguardSecure}, $isScreenOn")
        val isLocked = if (km.isKeyguardSecure) {km.isKeyguardLocked} else {true}
        val wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE, "my:app")
        wakeLock.acquire(10000L)
        val isTest = intent.getBooleanExtra("itTest", false)

        if (!isTest) {
            createNextAlarm(context)
        } else {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = preferences.edit()
            editor.putBoolean("isTestOn", false)
            editor.putLong("testTime", 0L)
            editor.apply()
        }
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (!isLocked || isScreenOn) { //잠금 안걸린 상태거나 화면 켜진 상태
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
        wakeLock.release()
    }

    private fun createNextAlarm(context: Context){
        val dbHelper = DBHelper(context, "mydb.db", null, 1)
        val db = dbHelper.readableDatabase
        val database = DBFunction(db)

        val day = getDay()
        if (day == 1 || day == 7) return
        val isOn = database.getIs(day)
        if (!isOn) return
        val time = database.getTime(day)

        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
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
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setAutoCancel(false)
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
    private fun getDay(): Int = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul")).get(Calendar.DAY_OF_WEEK)
}