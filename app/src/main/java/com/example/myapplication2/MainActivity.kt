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