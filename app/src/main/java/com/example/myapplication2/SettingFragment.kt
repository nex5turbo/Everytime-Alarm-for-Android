package com.example.myapplication2

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener

class SettingFragment(val database: SQLiteDatabase): Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_setting, container, false) as ViewGroup
        setNowDb(rootView)
        setFragmentResultListener("requestKey") { requestKey, bundle ->
            val monTime = bundle.getString("mon")
            val tueTime = bundle.getString("tue")
            val wedTime = bundle.getString("wed")
            val thuTime = bundle.getString("thu")
            val friTime = bundle.getString("fri")
            val result = monTime+"\n"+tueTime+"\n"+wedTime+"\n"+thuTime+"\n"+friTime
            (rootView.findViewById(R.id.textView3) as TextView).text = result
        }
        return rootView
    }
    fun setNowDb(rootView: ViewGroup){
        val sql = "select mon,tue,wed,thu,fri from timeTable;"
        val cursor = database.rawQuery(sql,null)
        var resultText = ""
        while (cursor.moveToNext()){
            var mon =cursor.getString(0)
            var tue =cursor.getString(1)
            var wed =cursor.getString(2)
            var thu =cursor.getString(3)
            var fri =cursor.getString(4)
            resultText = mon + tue + wed + thu + fri
        }
        if (resultText.equals("")) {
            (rootView.findViewById(R.id.textView3) as TextView).text = "설정된 알람이 없습니다."
        } else {
            (rootView.findViewById(R.id.textView3) as TextView).text = resultText
        }
    }
}