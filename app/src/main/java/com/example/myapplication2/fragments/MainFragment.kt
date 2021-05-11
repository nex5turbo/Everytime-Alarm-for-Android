package com.example.myapplication2.fragments

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.example.myapplication2.R
import java.time.DayOfWeek
import java.util.*

class MainFragment(val database: SQLiteDatabase):Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_main, container, false) as ViewGroup
        val tomWeek = getDayOfWeek()
        setText(tomWeek, rootView)
        setFragmentResultListener("requestKey") { _, _ ->
            setText(tomWeek, rootView)
        }
        return rootView
    }

    private fun setText(tomWeek: Int, rootView: ViewGroup){
        if (tomWeek == 1 || tomWeek == 7){
            (rootView.findViewById(R.id.mainText) as TextView).text = "내일은 주말이에요!! ${getDayOfWeek()} ${Calendar.HOUR+Calendar.MINUTE}"
        } else {
            var resultText = getDbTime(tomWeek-2)
            (rootView.findViewById(R.id.mainText) as TextView).text = resultText
        }
    }

    private fun getDbTime(tomWeek: Int): String {
        val sql = "select mon,tue,wed,thu,fri from timetable;"
        val cursor = database.rawQuery(sql,null)
        var resultText = ""
        while (cursor.moveToNext()){
            var time =cursor.getString(tomWeek)
            resultText = transferTime(time)
        }
        cursor.close()

        if (resultText == "") {
            resultText = "아직 등록된 시간표가 없어요."
        }
        return resultText
    }

    private fun getDayOfWeek(): Int {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
        var todayWeek = calendar.get(Calendar.DAY_OF_WEEK)
        if (todayWeek == 7) todayWeek = 1
        else todayWeek+=1
        return todayWeek
    }

    private fun transferTime(time: String): String {
        if (time == "no") {
            return "내일은 공강이에요!"
        }
        var alarmTime = time.split(",")
        var hour = alarmTime[0]
        var minute = alarmTime[1]
        return "내일의 첫 수업은 "+hour+":"+minute+"에요!"
    }
}
