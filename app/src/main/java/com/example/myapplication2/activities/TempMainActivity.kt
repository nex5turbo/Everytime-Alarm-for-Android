package com.example.myapplication2.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication2.OpenCvModule
import com.example.myapplication2.R
import com.example.myapplication2.alarmutils.AlarmFunction
import com.example.myapplication2.dbutils.DBFunction
import com.example.myapplication2.dbutils.DBHelper
import com.example.myapplication2.utils.RealPath
import com.example.myapplication2.utils.SpannableText
import com.example.myapplication2.utils.TimeData
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.suke.widget.SwitchButton
import org.opencv.android.Utils
import org.opencv.core.Mat
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

const val TIME_TABLE_REQUEST_CODE = 200
const val SETTING_REQUEST_CODE = 100
const val TOMORROW = 1
const val TODAY = 0

class TempMainActivity : AppCompatActivity() {
    private lateinit var adView:AdView
    private lateinit var adRequest: AdRequest

    private lateinit var matResult: Mat
    private lateinit var matInput: Mat

    private lateinit var settingButton: ImageView
    private lateinit var timeTableButton: ImageView

    private lateinit var weatherTextView: TextView
    private lateinit var weatherImageView: ImageView

    private lateinit var infoTextView: TextView

    private lateinit var mondayTimeTextView: TextView
    private lateinit var tuesdayTimeTextView: TextView
    private lateinit var wednesdayTimeTextView: TextView
    private lateinit var thursdayTimeTextView: TextView
    private lateinit var fridayTimeTextView: TextView
    private lateinit var timeTextViewArray: Array<TextView>

    private lateinit var dayMonTextView: TextView
    private lateinit var dayTueTextView: TextView
    private lateinit var dayWedTextView: TextView
    private lateinit var dayThuTextView: TextView
    private lateinit var dayFriTextView: TextView
    private lateinit var dayTextViewArray: Array<TextView>

    private lateinit var mondaySwitch: SwitchButton
    private lateinit var tuesdaySwitch: SwitchButton
    private lateinit var wednesdaySwitch: SwitchButton
    private lateinit var thursdaySwitch: SwitchButton
    private lateinit var fridaySwitch: SwitchButton
    private lateinit var switchArray: Array<SwitchButton>

    private lateinit var testButton: Button

    private lateinit var dbHelper: DBHelper
    private lateinit var db: SQLiteDatabase
    private lateinit var database: DBFunction

    private var alarmArray:ArrayList<ArrayList<Int>>? = null
    private val timeArray = TimeData.timeArray
    private var resultPath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temp_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        MobileAds.initialize(this){}
        adView = findViewById(R.id.adView)
        adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        initWidgets()
        initListener()
        initDatabase()
        initAlarmUI()
        initTest()
    }

    private fun initTest() {
        testButton = findViewById(R.id.testButton)
        testButton.setOnClickListener {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
            val formatter1 = SimpleDateFormat("HH,mm")
            val formatted = formatter1.format(calendar.timeInMillis).split(",")
            val hour = formatted[0].toInt()
            val minute = formatted[1].toInt() + 1
            val testTime = "$hour,$minute"
            val day = calendar.get(Calendar.DAY_OF_WEEK)
            AlarmFunction.setAlarm(day, testTime, this, 0)
            Toast.makeText(this, "1분후 알람", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initAlarmUI() {
        setAlarmUI()
    }

    private fun setAlarmUI() {
        val dbStringArray = database.getAllTime()
        if (dbStringArray.isEmpty()) {
            return
        }

        val timeStringArray = convertDBArrayToTimeStringArray(dbStringArray)
        val isArray = database.getAllIs()
        val preTime = database.getPreTime()
        setTimeUI(timeStringArray, isArray)
        val tomorrowDayOfWeek = getDayOfWeek(TOMORROW)
        val infoText = if (tomorrowDayOfWeek == 1 || tomorrowDayOfWeek == 7) {
            val text = "내일은 주말이에요! \n 편히 쉬세요!"
            SpannableText.convertToSpannable(text, 4, 6, Color.RED, true, 20)
        } else if (getTime(tomorrowDayOfWeek) == "no") {
            val text = "내일은 공강이에요! \n 편히 쉬세요!"
            SpannableText.convertToSpannable(text, 4, 6, Color.RED, true, 20)
        } else if (!isArray[tomorrowDayOfWeek-2]) {
            SpannableString("알람이 꺼져있어요! \n 편히 쉬세요!")
        } else {
            val tomorrowTime = getTime(tomorrowDayOfWeek).split(",")
            val hour = tomorrowTime[0]
            val minute = tomorrowTime[1]
            val tomorrowAlarmTime = formatTimeString(hour, minute, preTime)
            val tomorrowClassTime = formatTimeString(hour, minute)
            val text = "내일(${TimeData.dayStringArray[tomorrowDayOfWeek-2][0]}) 첫 수업은 ${tomorrowClassTime}에요.\n"+
                    "${tomorrowAlarmTime}에 깨워드릴게요!"
            var spannableString = SpannableString(text)
            val dayStart = text.indexOf("(${TimeData.dayStringArray[tomorrowDayOfWeek-2][0]})")
            val dayEnd = dayStart+3
            val classStart = text.indexOf(tomorrowClassTime)
            val classEnd = classStart+tomorrowClassTime.length
            val alarmStart = text.indexOf(tomorrowAlarmTime)
            val alarmEnd = alarmStart+tomorrowAlarmTime.length
            spannableString = SpannableText.setSpans(spannableString, dayStart, dayEnd, Color.BLACK, true, 20)
            spannableString = SpannableText.setSpans(spannableString, classStart, classEnd, Color.BLACK, true, 20)
            spannableString = SpannableText.setSpans(spannableString, alarmStart, alarmEnd, Color.BLACK, true, 20)
            spannableString
        }
        infoTextView.text = infoText
    }

    private fun initDatabase() {
        dbHelper = DBHelper(this, "mydb.db", null, 1)
        db = dbHelper.writableDatabase
        database = DBFunction(db)
    }

    private fun initListener() {
        settingButton.setOnClickListener {

        }
        timeTableButton.setOnClickListener {
            openGallery()
        }
        for (i in 0..4) {
            val tempSwitch = switchArray[i]
            tempSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                if (!buttonView.isEnabled) return@setOnCheckedChangeListener
                if (isChecked) {
                    database.updateIs(i+2, isChecked)
                    dayTextViewArray[i].setTextColor(Color.BLACK)
                } else {
                    database.updateIs(i+2, isChecked)
                    dayTextViewArray[i].setTextColor(Color.BLUE)
                }
            }
        }
    }

    private fun initWidgets() {
        settingButton = findViewById(R.id.settingButton)
        timeTableButton = findViewById(R.id.timetableButton)

        weatherTextView = findViewById(R.id.weatherTextView)
        weatherImageView = findViewById(R.id.weatherImageView)

        infoTextView = findViewById(R.id.infoTextView)

        mondayTimeTextView = findViewById(R.id.mondayTimeTextView)
        tuesdayTimeTextView = findViewById(R.id.tuesdayTimeTextView)
        wednesdayTimeTextView = findViewById(R.id.wednesdayTimeTextView)
        thursdayTimeTextView = findViewById(R.id.thursdayTimeTextView)
        fridayTimeTextView = findViewById(R.id.fridayTimeTextView)
        timeTextViewArray = arrayOf(
            mondayTimeTextView,
            tuesdayTimeTextView,
            wednesdayTimeTextView,
            thursdayTimeTextView,
            fridayTimeTextView)

        dayMonTextView = findViewById(R.id.dayMonTextView)
        dayTueTextView = findViewById(R.id.dayTueTextView)
        dayWedTextView = findViewById(R.id.dayWedTextView)
        dayThuTextView = findViewById(R.id.dayThuTextView)
        dayFriTextView = findViewById(R.id.dayFriTextView)
        dayTextViewArray = arrayOf(
            dayMonTextView,
            dayTueTextView,
            dayWedTextView,
            dayThuTextView,
            dayFriTextView)

        mondaySwitch = findViewById(R.id.mondaySwitch)
        tuesdaySwitch = findViewById(R.id.tuesdaySwitch)
        wednesdaySwitch = findViewById(R.id.wednesdaySwitch)
        thursdaySwitch = findViewById(R.id.thursdaySwitch)
        fridaySwitch = findViewById(R.id.fridaySwitch)
        switchArray = arrayOf(
            mondaySwitch,
            tuesdaySwitch,
            wednesdaySwitch,
            thursdaySwitch,
            fridaySwitch)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(intent, TIME_TABLE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == TIME_TABLE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null && data.data != null){
            val inputStream: InputStream? =
                contentResolver.openInputStream(data.data!!)
            val realPath = RealPath()
            val uri = data.data
            resultPath = realPath.getRealPath(this, uri!!)!!

            val myBit: Bitmap = BitmapFactory.decodeStream(inputStream)
            convertTimeTable(myBit)

            if(alarmArray != null){
                val timeStringArray = arrayToTimeStringArray()
                val dbStringArray = arrayToDbStringArray()
                database.deletePath()
                database.insertPath(resultPath)
                setDialog(timeStringArray, dbStringArray, false)
            } else {
                val emptyArray: Array<String> = arrayOf()
                setDialog(emptyArray, emptyArray,true)
            }
        } else if (requestCode == SETTING_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            //TODO
        }
    }

    private fun convertTimeTable(myBit: Bitmap){
        matInput = Mat()
        matResult = Mat()
        Utils.bitmapToMat(myBit, matInput)

        matResult.release()
        matResult = Mat(matInput.rows(), matInput.cols(), matInput.type())
        val resultArray = OpenCvModule().ConvertRGBtoGray(matInput.nativeObjAddr, matResult.nativeObjAddr)

        alarmArray = AlarmFunction.splitArr(resultArray)
    }

    private fun arrayToTimeStringArray(): Array<String>{
        if (alarmArray == null) {
            return arrayOf()
        } else {
            val eachDay = AlarmFunction.getFirstEachDay(alarmArray!!)
            val timeStringArray = Array(5){""}

            for (i in 0..4) {
                if (eachDay[i] == -1) {
                    timeStringArray[i] = "오늘 공강!"
                } else {
                    val classTime = eachDay[i]
                    val hour = timeArray[classTime][0].toString()
                    val minute = if(timeArray[classTime][1] == 0){"00"}else{"30"}
                    timeStringArray[i] = formatTimeString(hour, minute)
                }
            }
            return timeStringArray
        }
    }

    private fun arrayToDbStringArray(): Array<String>{
        if (alarmArray == null) {
            return arrayOf()
        } else {
            val eachDay = AlarmFunction.getFirstEachDay(alarmArray!!)
            val timeStringArray = Array(5){""}

            for (i in 0..4) {
                if (eachDay[i] == -1) {
                    timeStringArray[i] = "no"
                } else {
                    val classTime = eachDay[i]
                    val hour = timeArray[classTime][0].toString()
                    val minute = if(timeArray[classTime][1] == 0){"00"}else{"30"}
                    timeStringArray[i] =  formatDbString(hour, minute)
                }
            }
            return timeStringArray
        }
    }

    private fun setDialog(timeStringArray: Array<String>, dbStringArray: Array<String>, isError: Boolean){
        if (isError) {
            val dialog = AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog)
            dialog.setMessage("시간표 사진이 아닌 것 같아요!")
                .setTitle("에러!")
                .setPositiveButton("다시 하기"){ _, _ ->
                    openGallery()
                    Toast.makeText(this, "다시 선택할게요~!", Toast.LENGTH_SHORT).show()
                }.setNegativeButton("취소") {_,_ ->
                    Toast.makeText(this, "다음에 할게요", Toast.LENGTH_SHORT).show()
                }
                .setCancelable(false)
                .show()
        } else {
            val dialog = AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog)

            val monTime = timeStringArray[0]
            val tueTime = timeStringArray[1]
            val wedTime = timeStringArray[2]
            val thuTime = timeStringArray[3]
            val friTime = timeStringArray[4]

            val dbMon = dbStringArray[0]
            val dbTue = dbStringArray[1]
            val dbWed = dbStringArray[2]
            val dbThu = dbStringArray[3]
            val dbFri = dbStringArray[4]

            dialog.setMessage("$monTime \n$tueTime \n$wedTime \n$thuTime \n$friTime")
                .setTitle("시간표 분석 완료!")
                .setNegativeButton("설정하기") { _, _ ->
                    Toast.makeText(this, "알람이 설정되었습니다!", Toast.LENGTH_SHORT).show()
                    AlarmFunction.setAlarms(dbMon, dbTue, dbWed, dbThu, dbFri, this, 90).toString() //성공여부에 따라 다이얼로그 띄우기
                    dbInsert(dbStringArray)
                    val isArray = arrayOf(dbMon != "no", dbTue != "no", dbWed != "no", dbThu != "no", dbFri != "no")
                    setTimeUI(timeStringArray, isArray)
                    setAlarmUI()
                }
                .setPositiveButton("다시하기") { _, _ ->
                    openGallery()
                    Toast.makeText(this, "다시 선택할게요~!", Toast.LENGTH_SHORT).show()
                }
                .setNeutralButton("취소") {_,_->
                    Toast.makeText(this, "다음에 할게요~!", Toast.LENGTH_SHORT).show()
                }
                .setCancelable(false)
                .show()
        }
    }

    private fun dbInsert(timeArray: Array<String>) {
        val isArray = arrayOf(true, true, true, true, true)

        database.delete()
        database.insertData(timeArray, isArray, 30)
    }

    private fun convertDBArrayToTimeStringArray(dbArray: Array<String>): Array<String> {
        val returnArray = Array(5){""}
        for (i in 0..4) {
            val dbString = dbArray[i]
            if (dbString == "no") {
                returnArray[i] = "오늘 공강!"
            } else {
                val tempSplit = dbString.split(",")
                val hour = tempSplit[0]
                val minute = tempSplit[1]
                val preTime = database.getPreTime()
                returnArray[i] = formatTimeString(hour, minute, preTime)
            }
        }
        return returnArray
    }

    private fun setTimeUI(timeStringArray: Array<String>, isArray: Array<Boolean>){
        for (i in 0..4) {
            val tempTimeTextView = timeTextViewArray[i]
            val tempDayTextView = dayTextViewArray[i]
            val tempSwitch = switchArray[i]
            val tempIs = isArray[i]
            val tempTimeString = timeStringArray[i]
            val content =
                SpannableText.
                convertToSpannable(tempTimeString, 3, tempTimeString.length, Color.BLACK, true, 20)

            if (tempTimeString == "오늘 공강!") {
                tempDayTextView.setTextColor(Color.RED)
                tempTimeTextView.text = content
                tempSwitch.isEnabled = false
                tempSwitch.isChecked = false
                tempSwitch.visibility = View.INVISIBLE
            } else {
                if (!tempIs) {
                    tempDayTextView.setTextColor(Color.BLUE)
                    tempTimeTextView.text = content
                    tempSwitch.isEnabled = true
                    tempSwitch.isChecked = false
                    tempSwitch.visibility = View.VISIBLE
                } else {
                    tempDayTextView.setTextColor(Color.BLACK)
                    tempTimeTextView.text = content
                    tempSwitch.isEnabled = true
                    tempSwitch.isChecked = true
                    tempSwitch.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun getTime(day: Int): String = database.getTime(day)
    private fun getDayOfWeek(flag: Int): Int {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
        var dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) + flag
        if (dayOfWeek == 8) dayOfWeek = 1
        if (dayOfWeek == 0) dayOfWeek = 7
        return dayOfWeek
    }

    private fun formatTimeString(hour: String, minute: String, preTime: Int): String {
        var intHour = hour.toInt()
        var intMinute = minute.toInt()
        intHour -= (preTime / 60)
        if (intMinute - preTime % 60 < 0) {
            intHour-=1
            intMinute = 60+(intMinute - (preTime % 60))
        } else {
            intMinute -= (preTime % 60)
        }

        return formatTimeString(intHour.toString(), intMinute.toString())
    }

    private fun formatDbString(hour: String, minute: String): String = "$hour,$minute"
    private fun formatTimeString(hour: String, minute: String): String {
        val returnMinute = if (minute.length == 1) {"0$minute"} else {minute}
        if (hour.toInt() > 12) {
            val newHour = hour.toInt() - 12
            return "오후 $newHour:$returnMinute"
        }
        return "오전 $hour:$returnMinute"
    }


    override fun onPause(){
        adView.pause()
        super.onPause()
    }

    override fun onDestroy() {
        adView.destroy()
        super.onDestroy()
    }

    companion object {
        init {
            System.loadLibrary("opencv_java4")
            System.loadLibrary("native-lib")
        }
    }
}