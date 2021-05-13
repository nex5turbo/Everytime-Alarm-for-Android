package com.example.myapplication2.utils

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log

class DBFunction(val database: SQLiteDatabase) {
    val dayStringArray = TimeData.dayStringArrayEng
    fun getTime(day: Int): String{
        val sql = "select ${dayStringArray[day-2]} from timetable"
        val result = database.rawQuery(sql, null)
        var time = ""
        while (result.moveToNext()) {
            time = result.getString(0)
        }
        result.close()
        return time
    }

    fun getIs(day: Int): Boolean{
        val sql = "select is${dayStringArray[day-2]} from timetable"
        val result = database.rawQuery(sql, null)
        var boolInt = 1
        while (result.moveToNext()) {
            boolInt = result.getInt(0)
        }
        val bool = boolInt == 1
        result.close()
        return bool
    }

    fun getPreTime(): Int{
        val sql = "select pretime from timetable"
        val result = database.rawQuery(sql, null)
        var preTime = -1
        while (result.moveToNext()) {
            preTime = result.getInt(0)
        }
        result.close()
        return preTime
    }

    fun getAllTime(): Array<String>{
        val sql = "select * from timetable"
        val result = database.rawQuery(sql, null)
        var timeArray = arrayOf("")
        while (result.moveToNext()) {
            val mon = result.getString(1)
            val tue = result.getString(2)
            val wed = result.getString(3)
            val thu = result.getString(4)
            val fri = result.getString(5)
            timeArray = arrayOf(mon, tue, wed, thu, fri)
        }
        result.close()
        return timeArray
    }

    fun getAllIs(): Array<Boolean>{
        val sql = "select * from timetable"
        val result = database.rawQuery(sql, null)
        var isArray: Array<Boolean> = arrayOf()
        while (result.moveToNext()) {
            val mon = result.getInt(6) == 1
            val tue = result.getInt(7) == 1
            val wed = result.getInt(8) == 1
            val thu = result.getInt(9) == 1
            val fri = result.getInt(10) == 1
            isArray = arrayOf(mon, tue, wed, thu, fri)
        }
        result.close()
        return isArray
    }

    fun delete(): Boolean{
        val result = database.delete("timetable", null,null)
        return result != -1
    }

    fun updateIs(day: Int, targetIs: Boolean): Boolean{
        val data = ContentValues()
        val intIs = if (targetIs) {1} else {0}
        Log.d("###", "is${dayStringArray[day-2]} $intIs")
        data.put("is${dayStringArray[day-2]}", intIs)
        val result = database.update("timetable", data, null, null)
        return result != -1
    }

    fun updatePreTime(preTime: Int): Boolean{
        val data = ContentValues()
        data.put("preTime", preTime)
        val result = database.update("timetable", data, null, null)
        return result != -1
    }

    fun updateAllIs(targetIs: Boolean): Boolean{
        val data = ContentValues()
        val intIs = if (targetIs) {1} else {0}
        data.put("is${dayStringArray[0]}", intIs)
        data.put("is${dayStringArray[1]}", intIs)
        data.put("is${dayStringArray[2]}", intIs)
        data.put("is${dayStringArray[3]}", intIs)
        data.put("is${dayStringArray[4]}", intIs)
        val result = database.update("timetable", data, null, null)
        return result != -1
    }

    fun insertData(timeArray: Array<String>, isArray: Array<Boolean>,preTime: Int): Boolean{
        return true
    }


}