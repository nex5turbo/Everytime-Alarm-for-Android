package com.example.myapplication2.activities

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication2.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import java.net.URI

class AlarmReceiveActivity : AppCompatActivity() {

    private lateinit var adView: AdView
    private lateinit var adRequest: AdRequest

    private lateinit var stopButton: Button
    private var mp: MediaPlayer? = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_receive)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        MobileAds.initialize(this){}
        adView = findViewById(R.id.alarmAdView)
        adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        stopButton = findViewById(R.id.stopButton)

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

        initRingtone()
        initListener()
    }

    private fun initListener() {
        stopButton.setOnClickListener {
            if (mp == null) return@setOnClickListener
            if (mp!!.isPlaying) {
                mp!!.stop()
            }
        }
    }

    private fun initRingtone() {
        val preference = getSharedPreferences("isFirst", Activity.MODE_PRIVATE)
        val musicPath = preference.getString("musicPath", "")
        val mediaURI = if (musicPath == "") {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        } else {
            Uri.parse(musicPath)
        }
        mp!!.setDataSource(this, mediaURI)
        mp!!.isLooping = true
        mp!!.setAudioStreamType(AudioManager.STREAM_ALARM)
        mp!!.prepare()
        mp!!.start()
    }

    override fun onDestroy() {
        if (mp != null && mp!!.isPlaying) {
            mp!!.stop()
        }
        super.onDestroy()
    }
}