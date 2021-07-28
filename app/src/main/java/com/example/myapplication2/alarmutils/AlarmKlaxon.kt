package com.example.myapplication2.alarmutils

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.preference.PreferenceManager
import com.example.myapplication2.utils.AlarmWakeLock

class AlarmKlaxon: Service() {
    private var isPlaying = false
    private var vibrator: Vibrator? = null
    private var mediaPlayer: MediaPlayer? = null
    private val pattern = longArrayOf(100,200,300,400,300,200)

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        play()
        return START_STICKY
    }

    override fun onCreate() {
        AlarmWakeLock.acquireCpuWakeLock(this)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator


        super.onCreate()
    }

    override fun onDestroy() {
        stop()
        AlarmWakeLock.releaseCpuLock()
        super.onDestroy()
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

    private fun play() {
        isPlaying = true
        val preference = PreferenceManager.getDefaultSharedPreferences(this)
        val musicPath = preference.getString("musicPath", "")
        val mediaURI = if (musicPath == "") {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        } else {
            Uri.parse(musicPath)
        }
        mediaPlayer = MediaPlayer()
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


}