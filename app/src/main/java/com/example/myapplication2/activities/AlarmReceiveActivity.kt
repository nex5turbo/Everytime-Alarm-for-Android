package com.example.myapplication2.activities

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.media.*
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication2.R
import com.example.myapplication2.utils.NetworkStatus
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class AlarmReceiveActivity : AppCompatActivity() {
    private lateinit var adView: AdView
    private lateinit var adRequest: AdRequest

    private lateinit var stopButton: Button
    private var alarmPlayer: MediaPlayer? = MediaPlayer()
    private lateinit var vibrator:Vibrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_receive)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        initAdvertisement()

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
        startRingtone()
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

    private fun initVibrator() {
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val pattern = longArrayOf(100,200,300,400,300,200)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val amplitude = intArrayOf(20,20,20,20,20,20)
            val waveform = VibrationEffect.createWaveform(pattern, amplitude, 0)
            vibrator.vibrate(waveform)
        } else {
            vibrator.vibrate(pattern, 0)
        }
    }

    private fun initListener() {
        stopButton.setOnClickListener {
            if (alarmPlayer == null) return@setOnClickListener
            if (alarmPlayer!!.isPlaying) {
                alarmPlayer!!.stop()
                vibrator.cancel()
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
        alarmPlayer!!.setDataSource(this, mediaURI)
        alarmPlayer!!.isLooping = true
        alarmPlayer!!.setAudioStreamType(AudioManager.STREAM_ALARM)
        alarmPlayer!!.prepare()
    }

    private fun startRingtone() {
        alarmPlayer!!.start()
        initVibrator()
    }

    override fun onBackPressed() {
        if (alarmPlayer!!.isPlaying) return
        super.onBackPressed()
    }

    override fun onDestroy() {
        if (alarmPlayer != null && alarmPlayer!!.isPlaying) {
            alarmPlayer!!.stop()
        }
        super.onDestroy()
    }
}