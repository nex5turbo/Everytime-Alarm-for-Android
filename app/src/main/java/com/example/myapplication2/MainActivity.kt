package com.example.myapplication2

import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.myapplication2.databinding.ActivityMainBinding
import com.example.myapplication2.utils.DBFunction
import com.example.myapplication2.utils.DBHelper
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {
    private lateinit var weatherRetrofit: Retrofit
    private lateinit var weatherApi: WeatherApi

    private lateinit var dbHelper: DBHelper
    private lateinit var database: SQLiteDatabase
    private lateinit var db: DBFunction

    private lateinit var binding: ActivityMainBinding

    private val tabTextList = arrayListOf("Home", "Image", "Setting", "Temp1", "Temp2")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DBHelper(this, "mydb.db", null, 1)
        database = dbHelper.writableDatabase
        db = DBFunction(database)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewPager.adapter = ViewPagerAdapter(this, database, db)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) {
            tab, position ->
            tab.text = tabTextList[position]
        }.attach()
    }
}

