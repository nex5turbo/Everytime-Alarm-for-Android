package com.example.myapplication2

import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.myapplication2.databinding.ActivityMainBinding
import com.example.myapplication2.utils.DBHelper
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {
    lateinit var weatherRetrofit: Retrofit
    lateinit var weatherApi: WeatherApi

    lateinit var dbHelper: DBHelper
    lateinit var database: SQLiteDatabase

    lateinit var binding: ActivityMainBinding

    private val tabTextList = arrayListOf("Home", "Image", "Setting")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DBHelper(this, "mydb.db", null, 1)
        database = dbHelper.writableDatabase

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewPager.adapter = ViewPagerAdapter(this, database)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) {
            tab, position ->
            tab.text = tabTextList[position]
        }.attach()
    }
}

//val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
//
//val intent = Intent(this, Alarm::class.java)  // 1
//val pendingIntent = PendingIntent.getBroadcast(     // 2
//        this, Calendar.DAY_OF_WEEK, intent,
//        PendingIntent.FLAG_UPDATE_CURRENT)
//val calendar = Calendar.getInstance()
//calendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH))
//calendar.set(Calendar.DAY_OF_WEEK,3)
//calendar.set(Calendar.HOUR_OF_DAY,21)
//calendar.set(Calendar.MINUTE, 2)
//calendar.set(Calendar.SECOND,18)
//binding.button.setOnClickListener{
//    val toastMessage = if (isTrue) {   // 3
//        isTrue = false
//        val triggerTime = (SystemClock.elapsedRealtime()  // 4
//                + 10 * 1000)
//        alarmManager.setExact(   // 5
//                AlarmManager.RTC_WAKEUP,
//                calendar.timeInMillis,
//                pendingIntent
//        )
//        "Onetime Alarm On"
//    } else {
//        isTrue = true
//        alarmManager.cancel(pendingIntent)    // 6
//        "Onetime Alarm Off"
//    }
//    Log.d("TAG", toastMessage)
//    Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
//}