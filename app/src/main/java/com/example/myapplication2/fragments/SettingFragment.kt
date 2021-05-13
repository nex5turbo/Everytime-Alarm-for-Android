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
import com.example.myapplication2.utils.DBFunction

class SettingFragment(val database: SQLiteDatabase, val mContext: Context, val db: DBFunction): Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_setting, container, false) as ViewGroup

        val monButton = rootView.findViewById(R.id.monButton) as Button
        val tueButton = rootView.findViewById(R.id.tueButton) as Button
        val wedButton = rootView.findViewById(R.id.wedButton) as Button
        val thuButton = rootView.findViewById(R.id.thuButton) as Button
        val friButton = rootView.findViewById(R.id.friButton) as Button
        val buttonArray: Array<Button> = arrayOf(monButton, tueButton, wedButton, thuButton, friButton)

        setButtonText(buttonArray)
        setListener(buttonArray)
        setNowDb(rootView)
        setFragmentResultListener("requestKey") { _, bundle ->
            val mon = bundle.getString("mon")!!
            val tue = bundle.getString("tue")!!
            val wed = bundle.getString("wed")!!
            val thu = bundle.getString("thu")!!
            val fri = bundle.getString("fri")!!
            resultFragment(mon, tue, wed, thu, fri, rootView)
            setButtonText(buttonArray)
            Log.d("###", AlarmFunction.setAlarms(mon, tue, wed, thu, fri, mContext, 0).toString()) //성공여부에 따라 다이얼로그 띄우기
        }

        return rootView
    }

    private fun setNowDb(rootView: ViewGroup){
        val timeStringArray = db.getAllTime()

        var mon = timeStringArray[0]
        var tue = timeStringArray[1]
        var wed = timeStringArray[2]
        var thu = timeStringArray[3]
        var fri = timeStringArray[4]

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

    private fun setListener(buttons: Array<Button>){
        for (i in 0..4) {
            val day = i+2
            val idx = i
            buttons[idx].setOnClickListener {
                val isDay = db.getIs(day)
                if (isDay) {
                    val result = db.updateIs(day, false)
                    Log.d("###","$result")
                    AlarmFunction.cancelAlarm(day, mContext)
                    buttons[idx].text = "Off"
                } else {
                    val result = db.updateIs(day, true)
                    Log.d("###","$result")
                    val preTime = db.getPreTime()
                    val time = db.getTime(day)
                    AlarmFunction.setAlarm(day, time, mContext, preTime)
                    buttons[idx].text = "On"
                }
            }
        }
    }

    private fun setButtonText(buttons: Array<Button>){
        for (i in 0..4) {
            val day = i+2
            val isDay = db.getIs(day)
            buttons[i].text = if (isDay) {"On"} else {"Off"}
        }
    }
}