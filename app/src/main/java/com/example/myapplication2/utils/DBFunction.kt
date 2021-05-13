package com.example.myapplication2.utils

import android.database.sqlite.SQLiteDatabase

class DBFunction(val database: SQLiteDatabase) {
    fun getTime(day: Int): String{
        return ""
    }

    fun getIs(day: Int): Boolean{
        return true
    }

    fun getAllTime(): Array<String>{
        return arrayOf()
    }

    fun getAllIs(): Array<Boolean>{
        return arrayOf()
    }

    fun delete(): Boolean{
        return true
    }

    fun updateIs(day: Int, targetIs: Boolean): Boolean{
        return true
    }

    fun updatePreTime(preTime: Int): Boolean{
        return true
    }

    fun updateAllIs(targetIs: Boolean): Boolean{
        return true
    }

    fun insertData(timeArray: Array<String>, isArray: Array<Boolean>,preTime: Int): Boolean{
        return true
    }


}