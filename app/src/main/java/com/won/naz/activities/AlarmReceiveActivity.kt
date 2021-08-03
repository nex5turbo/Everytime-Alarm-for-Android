package com.won.naz.activities

import android.app.KeyguardManager
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.media.*
import android.net.Uri
import android.os.*
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.won.naz.R
import com.won.naz.utils.AlarmWakeLock
import com.won.naz.utils.NetworkStatus
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class AlarmReceiveActivity : AppCompatActivity() {
    private lateinit var adView: AdView
    private lateinit var adRequest: AdRequest

    private lateinit var stopButton: ImageView

    private var isPlaying = false
    private var mediaPlayer: MediaPlayer? = null

    private var vibrator: Vibrator? = null
    private val pattern = longArrayOf(100,200,300,400,300,200)

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

        setContentView(R.layout.activity_alarm_receive)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        initAdvertisement()
        initListener()
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        mediaPlayer = MediaPlayer()
        play()
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
            stop()

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancelAll()

            AlarmWakeLock.releaseCpuLock()
            finish()
        }
    }

    override fun onBackPressed() {
        if (isPlaying) return
        super.onBackPressed()
    }

    private fun play() {
        isPlaying = true
        val preference = PreferenceManager.getDefaultSharedPreferences(this)
        val musicPath = preference.getString("musicPath", "")
        val mediaURI = if (musicPath == "") {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        } else {
            Uri.parse(musicPath)
        }
        mediaPlayer!!.setDataSource(this, mediaURI)
        startAlarm(mediaPlayer!!)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val amplitude = intArrayOf(20,20,20,20,20,20)
            val waveform = VibrationEffect.createWaveform(pattern, amplitude, 0)
            vibrator!!.vibrate(waveform)
        } else {
            vibrator!!.vibrate(pattern, 0)
        }
    }

    private fun startAlarm(player: MediaPlayer) {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, (audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM) * 0.7).toInt(), AudioManager.FLAG_PLAY_SOUND)
        player.setAudioStreamType(AudioManager.STREAM_ALARM)
        player.isLooping = true
        player.prepare()
        player.start()
    }

    private fun stop() {
        if (isPlaying) {
            isPlaying = false
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            mediaPlayer = null
            vibrator!!.cancel()
        }
    }

}