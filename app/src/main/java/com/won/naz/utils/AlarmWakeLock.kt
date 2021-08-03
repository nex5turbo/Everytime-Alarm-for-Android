package com.won.naz.utils

import android.content.Context
import android.os.PowerManager

object AlarmWakeLock {
    private var sCpuWakeLock: PowerManager.WakeLock? = null
    fun acquireCpuWakeLock(context: Context) {
        if (sCpuWakeLock != null) {
            return
        }
        val pm: PowerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        sCpuWakeLock = pm.newWakeLock(
            PowerManager.FULL_WAKE_LOCK or
                    PowerManager.ACQUIRE_CAUSES_WAKEUP or
                    PowerManager.ON_AFTER_RELEASE, "my:app"
        )
        sCpuWakeLock!!.acquire(60_000)
    }

    fun releaseCpuLock() {
        if (sCpuWakeLock != null) {
            sCpuWakeLock!!.release()
            sCpuWakeLock = null
        }
    }
}