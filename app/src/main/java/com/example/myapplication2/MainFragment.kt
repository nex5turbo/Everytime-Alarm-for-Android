package com.example.myapplication2

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import java.time.DayOfWeek
import java.util.*

class MainFragment(val database: SQLiteDatabase):Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_main, container, false) as ViewGroup
        val tomWeek = 2
        setText(tomWeek, rootView)
        setFragmentResultListener("requestKey") { requestKey, bundle ->
            setText(tomWeek, rootView)
        }
        return rootView
    }

    fun setText(tomWeek: Int, rootView: ViewGroup){
        if (tomWeek == -1 || tomWeek == 5){
            (rootView.findViewById(R.id.mainText) as TextView).text = "내일은 주말이에요!!"
        } else {
            var resultText = getDbTime(tomWeek)
            (rootView.findViewById(R.id.mainText) as TextView).text = resultText
        }
    }

    fun getDbTime(tomWeek: Int): String {
        val sql = "select mon,tue,wed,thu,fri from timeTable;"
        val cursor = database.rawQuery(sql,null)
        var resultText = ""
        while (cursor.moveToNext()){
            var time =cursor.getString(tomWeek)
            resultText = transferTime(time)
        }
        return resultText
    }

    fun getDayOfWeek(): Int{
        val tomWeek = Calendar.DAY_OF_WEEK - 1
        return tomWeek
    }

    fun transferTime(time: String): String {
        if (time.equals("no")) {
            return "내일은 공강이에요!"
        }
        var alarmTime = time.split(",")
        var hour = alarmTime[0]
        var minute = alarmTime[1]
        return "내일의 첫 수업은 "+hour+":"+minute+"에요!"
    }
}
