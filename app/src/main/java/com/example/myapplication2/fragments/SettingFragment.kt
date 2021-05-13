package com.example.myapplication2.fragments

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.example.myapplication2.R
import com.example.myapplication2.utils.AlarmFunction

class SettingFragment(val database: SQLiteDatabase, val mContext: Context): Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_setting, container, false) as ViewGroup
        setNowDb(rootView)
        setFragmentResultListener("requestKey") { _, bundle ->
            val mon = bundle.getString("mon")!!
            val tue = bundle.getString("tue")!!
            val wed = bundle.getString("wed")!!
            val thu = bundle.getString("thu")!!
            val fri = bundle.getString("fri")!!
            resultFragment(mon, tue, wed, thu, fri, rootView)
            Log.d("###", AlarmFunction.setAlarms(mon, tue, wed, thu, fri, mContext, 0).toString()) //성공여부에 따라 다이얼로그 띄우기
        }
        return rootView
    }

    private fun setNowDb(rootView: ViewGroup){
        val sql = "select mon,tue,wed,thu,fri from timeTable;"
        val cursor = database.rawQuery(sql,null)
        var mon = ""
        var tue = ""
        var wed = ""
        var thu = ""
        var fri = ""
        while (cursor.moveToNext()){
            mon =cursor.getString(0)
            tue =cursor.getString(1)
            wed =cursor.getString(2)
            thu =cursor.getString(3)
            fri =cursor.getString(4)
        }
        cursor.close()
        resultFragment(mon, tue, wed, thu, fri, rootView)
    }

    private fun timeToString(time: String): String {
        if (time == "no") {
            return "공강!"
        }
        val timeSplit = time.split(",")
        var hour: String = timeSplit[0]
        var minute: String = timeSplit[1]
        if (hour.length == 1) {
            hour = "0"+hour
        }
        if (minute.length == 1) {
            minute = "0"+minute
        }
        return "${hour[0]} ${hour[1]} : ${minute[0]} ${minute[1]}"
    }

    private fun resultFragment(mon: String, tue: String, wed: String, thu: String, fri: String, rootView: ViewGroup) {
        (rootView.findViewById(R.id.monTimeText) as TextView).text = timeToString(mon)
        (rootView.findViewById(R.id.tueTimeText) as TextView).text = timeToString(tue)
        (rootView.findViewById(R.id.wedTimeText) as TextView).text = timeToString(wed)
        (rootView.findViewById(R.id.thuTimeText) as TextView).text = timeToString(thu)
        (rootView.findViewById(R.id.friTimeText) as TextView).text = timeToString(fri)
    }

    private fun setListener(button: Button){
        button.setOnClickListener {

        }
    }
}