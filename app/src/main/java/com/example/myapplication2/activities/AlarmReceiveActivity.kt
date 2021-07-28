package com.example.myapplication2.activities

import android.app.Activity
import android.app.KeyguardManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.*
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.example.myapplication2.R
import com.example.myapplication2.alarmutils.AlarmKlaxon
import com.example.myapplication2.utils.AlarmWakeLock
import com.example.myapplication2.utils.NetworkStatus
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class AlarmReceiveActivity : AppCompatActivity() {
    private lateinit var adView: AdView
    private lateinit var adRequest: AdRequest

    private lateinit var stopButton: ImageView
    private var isPlaying = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            setTurnScreenOn(true)
            setShowWhenLocked(true)
            val km = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            km.requestDismissKeyguard(this, null)
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        }
//        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
//                or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
//                or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        setContentView(R.layout.activity_alarm_receive)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        initAdvertisement()
        initListener()
    }

    private fun initAdvertisement() {
        if (NetworkStatus.getConnectivityStatus(this) != NetworkStatus.TYPE_NOT_CONNECTED) {
            MobileAds.initialize(this){}
            adView = findViewById(R.id.alarmAdView)
            adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
            stopButton = findViewById(R.id.stopButton)
        }
    }

    private fun initListener() {
        stopButton.setOnClickListener {
            isPlaying = false
            val stopAlarm = Intent(this, AlarmKlaxon::class.java)
            stopAlarm.action = "alarm_alert"
            stopService(stopAlarm)
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancelAll()
            finish()
        }
    }

    override fun onBackPressed() {
        if (isPlaying) return
        super.onBackPressed()
    }
}