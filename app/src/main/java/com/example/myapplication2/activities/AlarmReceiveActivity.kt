package com.example.myapplication2.activities

import android.app.KeyguardManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication2.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class AlarmReceiveActivity : AppCompatActivity() {

    private lateinit var adView: AdView
    private lateinit var adRequest: AdRequest

    private lateinit var stopButton: Button
    private var r: Ringtone? = null

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_receive)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        MobileAds.initialize(this){}
        adView = findViewById(R.id.alarmAdView)
        adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        stopButton = findViewById(R.id.stopButton)

        setTurnScreenOn(true)
        setShowWhenLocked(true)
        val km = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        km.requestDismissKeyguard(this, null)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        initRingtone()
        initListener()

    }

    private fun initListener() {
        stopButton.setOnClickListener {
            if (r == null) return@setOnClickListener
            if (r!!.isPlaying) {
                r!!.stop()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun initRingtone() {
        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        r = RingtoneManager.getRingtone(this, notification)
        r!!.setStreamType(AudioManager.STREAM_MUSIC)
        r!!.isLooping = true
        r!!.play()
    }
}