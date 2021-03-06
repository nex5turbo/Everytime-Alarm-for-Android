package com.won.naz.alarmutils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.preference.PreferenceManager
import com.won.naz.receiver.AlarmReceiver
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

const val TAG="AlarmFunction"

object AlarmFunction {
    private const val TEST_CODE = 101
    fun splitArr(array:IntArray):ArrayList<ArrayList<Int>>{
        val rt = ArrayList<ArrayList<Int>>()
        val monday = ArrayList<Int>()
        val tuesday = ArrayList<Int>()
        val thursday = ArrayList<Int>()
        val wednesday = ArrayList<Int>()
        val friday = ArrayList<Int>()
        var count = 0
        if(array.size%2 == 0) {
            while (true) {
                if (count == (array.size / 5) - 1) {
                    monday.add(array[count])
                    count++
                    break
                }
                monday.add(array[count])
                count++
            }
            while (true) {
                if (count == ((array.size / 5) * 2) - 1) {
                    tuesday.add(array[count])
                    count++
                    break
                }
                tuesday.add(array[count])
                count++
            }
            while (true) {
                if (count == ((array.size / 5) * 3) - 1) {
                    wednesday.add(array[count])
                    count++
                    break
                }
                wednesday.add(array[count])
                count++
            }
            while (true) {
                if (count == ((array.size / 5) * 4) - 1) {
                    thursday.add(array[count])
                    count++
                    break
                }
                thursday.add(array[count])
                count++
            }
            while (true) {
                if (count == (array.size) - 1) {
                    friday.add(array[count])
                    break
                }
                friday.add(array[count])
                count++
            }
        }else{
            while (true) {
                if (count == (array.size / 5) - 2) {
                    monday.add(array[count])
                    count++
                    break
                }
                monday.add(array[count])
                count++
            }
            count++
            while (true) {
                if (count == ((array.size / 5) * 2) - 2) {
                    tuesday.add(array[count])
                    count++
                    break
                }
                tuesday.add(array[count])
                count++
            }
            count++
            while (true) {
                if (count == ((array.size / 5) * 3) - 2) {
                    wednesday.add(array[count])
                    count++
                    break
                }
                wednesday.add(array[count])
                count++
            }
            count++
            while (true) {
                if (count == ((array.size / 5) * 4) - 2) {
                    thursday.add(array[count])
                    count++
                    break
                }
                thursday.add(array[count])
                count++
            }
            count++
            while (true) {
                if (count == (array.size) - 2) {
                    friday.add(array[count])
                    break
                }
                friday.add(array[count])
                count++
            }
        }
        rt.add(monday)
        rt.add(tuesday)
        rt.add(wednesday)
        rt.add(thursday)
        rt.add(friday)
        return rt
    }

    fun getFirstEachDay(arrList:ArrayList<ArrayList<Int>>):Array<Int>{
        val rtArray = Array(5){-1}
        var count = 0
        for(temp in arrList[0]){
            if(temp != 0){
                rtArray[0] = count
                break
            }
            count++
        }
        count = 0
        for(temp in arrList[1]){
            if(temp != 0){
                rtArray[1] = count
                break
            }
            count++
        }
        count = 0
        for(temp in arrList[2]){
            if(temp != 0){
                rtArray[2] = count
                break
            }
            count++
        }
        count = 0
        for(temp in arrList[3]){
            if(temp != 0){
                rtArray[3] = count
                break
            }
            count++
        }
        count = 0
        for(temp in arrList[4]){
            if(temp != 0){
                rtArray[4] = count
                break
            }
        }
        return rtArray
    }

    fun setAlarm(day: Int, time: String, mContext: Context, preTime: Int): Boolean{
        if (time == "no") return false
        if (day == 1 || day == 7) return false
        Log.d("###", "set 1 alarm")
        try {
            val f=SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val alarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(mContext, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(mContext, day, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            val dayTime = time.split(",")
            val hour = dayTime[0].toInt()
            val minute = dayTime[1].toInt() - preTime

            val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
            val alarmCalendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
            alarmCalendar.set(Calendar.DAY_OF_WEEK, day)
            alarmCalendar.set(Calendar.HOUR_OF_DAY, hour)
            alarmCalendar.set(Calendar.MINUTE, minute)
            alarmCalendar.set(Calendar.SECOND, 0)

            if (alarmCalendar.timeInMillis <= calendar.timeInMillis) {
                alarmCalendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, alarmCalendar.get(Calendar.DAY_OF_WEEK_IN_MONTH) + 1)
            }
            Log.d(TAG, "set time = ${f.format(alarmCalendar.timeInMillis)}")
            val aci = AlarmManager.AlarmClockInfo(alarmCalendar.timeInMillis, pendingIntent)
            alarmManager.setAlarmClock(aci, pendingIntent)

        } catch (e:Exception) {
            return false
        }
        return true
    }

    fun setTest(mContext: Context, second: Int): Boolean{
        Log.d("###", "set test alarm")

        try {
            val alarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(mContext, AlarmReceiver::class.java)
            intent.putExtra("isTest", true)

            val alarmCalendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
            alarmCalendar.set(Calendar.SECOND, alarmCalendar.get(Calendar.SECOND) + second)
            val pendingIntent = PendingIntent.getBroadcast(mContext, TEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            val preferences = PreferenceManager.getDefaultSharedPreferences(mContext)
            val aci = AlarmManager.AlarmClockInfo(alarmCalendar.timeInMillis, pendingIntent)
            alarmManager.setAlarmClock(aci, pendingIntent)

            val editor = preferences.edit()
            editor.putBoolean("isTestOn", true)
            editor.putLong("testTime", alarmCalendar.timeInMillis)
            editor.apply()
        } catch (e:Exception) {
            Log.d("###", e.toString())
            return false
        }
        return true
    }

    fun setTest(mContext: Context, testTime: Long): Boolean{
        Log.d("###", "set test alarm")

        try {
            val alarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(mContext, AlarmReceiver::class.java)
            intent.putExtra("isTest", true)

            val pendingIntent = PendingIntent.getBroadcast(mContext, TEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            alarmManager.setExact(   // 5
                    AlarmManager.RTC_WAKEUP,
                    testTime,
                    pendingIntent)

            val preferences = PreferenceManager.getDefaultSharedPreferences(mContext)
            val editor = preferences.edit()
            editor.putBoolean("isTestOn", true)
            editor.putLong("testTime", testTime)
            editor.apply()
        } catch (e:Exception) {
            Log.d("###", e.toString())
            return false
        }
        return true
    }

    fun cancelAlarm(day: Int, mContext: Context): Boolean {
        try {
            val alarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(mContext, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(mContext, day, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager.cancel(pendingIntent)
        } catch (e:Exception) {
            return false
        }
        return true
    }

    fun setAlarms(timeArray: Array<String>, mContext: Context, preTime: Int): Boolean{
        Log.d("###", "set alarms")
        try {
            val alarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            for (i in 0..4) {
                if (timeArray[i] == "no") {
                    continue
                }
                val f=SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

                val dayTime = timeArray[i].split(",")
                val hour = dayTime[0].toInt()
                val minute = dayTime[1].toInt() - preTime

                val intent = Intent(mContext, AlarmReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(     // 2
                        mContext, i + 2, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT)

                val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
                val alarmCalendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
                alarmCalendar.set(Calendar.DAY_OF_WEEK, i+2)
                alarmCalendar.set(Calendar.HOUR_OF_DAY, hour)
                alarmCalendar.set(Calendar.MINUTE, minute)
                alarmCalendar.set(Calendar.SECOND, 0)
                if (alarmCalendar.timeInMillis <= calendar.timeInMillis) {
                    alarmCalendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, alarmCalendar.get(Calendar.DAY_OF_WEEK_IN_MONTH) + 1)
                }
                Log.d(TAG, "set time = ${f.format(alarmCalendar.timeInMillis)}")
                val aci = AlarmManager.AlarmClockInfo(alarmCalendar.timeInMillis, pendingIntent)
                alarmManager.setAlarmClock(aci, pendingIntent)

            }

        } catch (e: Exception){
            return false
        }
        return true
    }
}