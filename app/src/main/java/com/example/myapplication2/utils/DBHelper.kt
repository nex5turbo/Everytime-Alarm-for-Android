package com.example.myapplication2.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper as SQLiteOpenHelper

class DBHelper(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
): SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase?) {
        val apisql = "create table if not exists apitable("+
                  "_id integer primary key autoincrement,"+
                  "txt text);"
        val timesql = "create table if not exists timetable("+
                      "_id integer primary key autoincrement,"+
                    "mon text, tue text, wed text, thu text, fri text);"
        db!!.execSQL(apisql)
        db.execSQL(timesql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        var sql = ""
        sql = "drop table if exists apitable;"
        db!!.execSQL(sql)
        sql = "drop table if exists timetable;"
        db.execSQL(sql)
    }
}