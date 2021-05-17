package com.example.myapplication2.dbutils

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.myapplication2.utils.TimeData

class DBFunction(private val database: SQLiteDatabase) {
    private val dayStringArray = TimeData.dayStringArrayEng

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
        data.put("is${this.dayStringArray[day-2]}", intIs)
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
        data.put("is${this.dayStringArray[0]}", intIs)
        data.put("is${this.dayStringArray[1]}", intIs)
        data.put("is${this.dayStringArray[2]}", intIs)
        data.put("is${this.dayStringArray[3]}", intIs)
        data.put("is${this.dayStringArray[4]}", intIs)
        val result = database.update("timetable", data, null, null)
        return result != -1
    }

    fun insertData(timeArray: Array<String>, isArray: Array<Boolean>, preTime: Int): Boolean{
        val data = ContentValues()
        for (i in 0..4) {
            data.put(this.dayStringArray[i], timeArray[i])
            data.put("is${this.dayStringArray[i]}", boolToInt(isArray[i]))
        }
        data.put("preTime", preTime)
        val result = database.insert("timetable",null, data)
        if (result != -1L) return false
        return true
    }

    fun insertPath(path: String): Boolean{
        val data = ContentValues()
        data.put("path", path)
        val result = database.insert("pathtable", null, data)
        return result != -1L
    }

    fun deletePath(): Boolean{
        val result = database.delete("pathtable", null, null)
        return result != -1
    }

    fun updatePath(path: String): Boolean{
        val data = ContentValues()
        data.put("path", path)
        val result = database.update("pathtable", data, null, null)
        return result != -1
    }

    fun getPath(): String{
        val sql = "select path from pathtable;"
        val result = database.rawQuery(sql, null)
        var path = ""
        while (result.moveToNext()) {
            path = result.getString(0)
        }
        result.close()
        return path
    }

    private fun boolToInt(boolIs: Boolean): Int{
        if (boolIs) return 1
        return 0
    }

}