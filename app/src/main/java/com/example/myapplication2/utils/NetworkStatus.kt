package com.example.myapplication2.utils

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.NetworkInfo



object NetworkStatus {
    const val TYPE_WIFI = 1
    const val TYPE_MOBILE = 2
    const val TYPE_NOT_CONNECTED = 3
    fun getConnectivityStatus(context: Context): Int { //해당 context의 서비스를 사용하기위해서 context객체를 받는다.
        val manager: ConnectivityManager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = manager.getActiveNetworkInfo()

        if (networkInfo != null) {
            val type: Int = networkInfo.getType()
            if (type == ConnectivityManager.TYPE_MOBILE) { //쓰리지나 LTE로 연결된것(모바일을 뜻한다.)
                return TYPE_MOBILE
            } else if (type == ConnectivityManager.TYPE_WIFI) { //와이파이 연결된것
                return TYPE_WIFI
            }
        }
        return TYPE_NOT_CONNECTED //연결이 되지않은 상태
    }}