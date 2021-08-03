package com.won.naz.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.won.naz.R
import com.won.naz.dbutils.DBFunction
import com.won.naz.utils.NetworkStatus
import java.util.*

private const val TYPE_WIFI = 1
private const val TYPE_MOBILE = 2
private const val TYPE_NOT_CONNECTED = 3

class MainFragment(private val mContext: Context, private val database: DBFunction):Fragment() {
    private var networkStatus = TYPE_NOT_CONNECTED

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_main, container, false) as ViewGroup
        val tomWeek = getDayOfWeek()
        setText(tomWeek, rootView)
        setNetworkStatus()
        setFragmentResultListener("requestKey") { _, _ ->
            setText(tomWeek, rootView)
        }

        return rootView
    }

    private fun setNetworkStatus() {networkStatus = NetworkStatus.getConnectivityStatus(mContext)}

    private fun setText(day: Int, rootView: ViewGroup){
        if (day == 1 || day == 7){
            (rootView.findViewById(R.id.mainText) as TextView).text = "내일은 주말이에요!!"
        } else {
            val resultText = getDbTime(day)
            (rootView.findViewById(R.id.mainText) as TextView).text = resultText
        }
    }

    private fun getDbTime(day: Int): String {
        val time = database.getTime(day)
        if (time == "") {
            return "등록된 시간표가 없어요."
        }
        return timeToString(time)
    }

    private fun getDayOfWeek(): Int {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
        var todayWeek = calendar.get(Calendar.DAY_OF_WEEK)
        if (todayWeek == 7) todayWeek = 1
        else todayWeek+=1
        return todayWeek
    }

    private fun timeToString(time: String): String {
        if (time == "no") {
            return "내일은 공강이에요!"
        }
        val alarmTime = time.split(",")
        val hour = alarmTime[0]
        val minute = alarmTime[1]
        return "내일의 첫 수업은 ${hour}:${minute}에요!"
    }
}
