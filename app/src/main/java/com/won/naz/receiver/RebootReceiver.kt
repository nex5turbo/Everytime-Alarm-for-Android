package com.won.naz.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.won.naz.alarmutils.AlarmFunction
import com.won.naz.dbutils.DBFunction
import com.won.naz.dbutils.DBHelper

class RebootReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            val dbHelper = DBHelper(context, "mydb.db", null, 1)
            val db = dbHelper.readableDatabase
            val database = DBFunction(db)

            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val isTestOn = preferences.getBoolean("isTestOn", false)
            if (isTestOn) {
                val testTime = preferences.getLong("testTime", 0L)
                if (testTime != 0L) {
                    AlarmFunction.setTest(context, testTime)
                }
            }
            val timeArray = database.getAllTime()
            val isArray = database.getAllIs()
            if (timeArray.isNotEmpty()) {
                val preTime = preferences.getInt("preTime", 30)
                for (i in 0..4) {
                    if (!isArray[i]) {
                        timeArray[i] = "no"
                    }
                }
                val isSuccess = AlarmFunction.setAlarms(timeArray, context, preTime)
                if (!isSuccess) {
                    Toast.makeText(context, "나즈 알람 설정에 에러가 있습니다. 앱 실행 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}