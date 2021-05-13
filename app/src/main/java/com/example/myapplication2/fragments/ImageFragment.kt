package com.example.myapplication2.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.example.myapplication2.*
import com.example.myapplication2.utils.AlarmFunction
import com.example.myapplication2.OpenCvModule
import com.example.myapplication2.utils.DBFunction
import com.example.myapplication2.utils.RealPath
import com.example.myapplication2.utils.TimeData
import org.opencv.android.Utils
import org.opencv.core.Mat
import java.io.InputStream

private const val GET_GALLERY_IMG = 200

class ImageFragment(private val database: SQLiteDatabase, private val mContext: Context, private val db: DBFunction): Fragment() {
    private var resultPath = ""
    private var alarmArray:ArrayList<ArrayList<Int>>? = null
    private val timeArray = TimeData.timeArray
    private lateinit var timeImageView: ImageView
    private lateinit var textView: TextView
    private lateinit var matResult: Mat
    private lateinit var matInput: Mat

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_image, container, false) as ViewGroup
        timeImageView = rootView.findViewById(R.id.timeImage) as ImageView

        timeImageView.setOnClickListener{
            openGallery()
        }

        return rootView
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GET_GALLERY_IMG && resultCode == Activity.RESULT_OK && data != null && data.data != null){
            val isi: InputStream? =
                requireActivity().contentResolver.openInputStream(data.data!!)
            val realPath = RealPath()
            val uri = data.data
            resultPath = realPath.getRealPath(mContext, uri!!)!!
            val myBit: Bitmap = BitmapFactory.decodeStream(isi)
            val k = cvTest(myBit)
            Utils.bitmapToMat(myBit, k)
            Utils.matToBitmap(k, myBit)
            timeImageView.setImageBitmap(myBit)

            if(alarmArray != null){
                val timeStringArray = arrayToTimeStringArray()
                val dbStringArray = arrayToDbStringArray()
                setDialog(timeStringArray, dbStringArray, false)
            } else {
                val emptyArray: Array<String> = arrayOf()
                setDialog(emptyArray, emptyArray,true)
            }
        }
    }

    private fun sendResult(mon: String, tue: String, wed: String, thu: String, fri: String, pre: Int){
        //send time string to setting fragment
        setFragmentResult("requestKey", bundleOf(
            "mon" to mon,
            "tue" to tue,
            "wed" to wed,
            "thu" to thu,
            "fri" to fri,
            "pre" to pre))
    }

    private fun openGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(intent, GET_GALLERY_IMG)
    }

    private fun dbInsert(mon: String, tue: String, wed: String, thu: String, fri: String) {
        var sql = "delete from timeTable"
        database.execSQL(sql)
        sql = "insert into timeTable (mon, tue, wed, thu, fri, ismon, istue, iswed, isthu, isfri, pretime) "+
                "values ('$mon','$tue','$wed','$thu','$fri', 1, 1, 1, 1, 1, 30)"
        database.execSQL(sql)
    }

    private fun cvTest(myBit: Bitmap): Mat {
        matInput = Mat()
        matResult = Mat()
        Utils.bitmapToMat(myBit, matInput)

        matResult.release()
        matResult = Mat(matInput.rows(), matInput.cols(), matInput.type())
        val resultArray = OpenCvModule().ConvertRGBtoGray(matInput.nativeObjAddr, matResult.nativeObjAddr)

        alarmArray = AlarmFunction.splitArr(resultArray)

        return matResult
    }

    private fun arrayToTimeStringArray(): Array<String>{
        if (alarmArray == null) {
            return arrayOf()
        } else {
            val eachDay = AlarmFunction.getFirstEachDay(alarmArray!!)
            val dayStringArray = TimeData.dayStringArray
            val timeStringArray = Array<String>(5){""}

            for (i in 0..4) {
                if (eachDay[i] == -1) {
                    timeStringArray[i] = dayStringArray[i][0].toString()+"공강"
                } else {
                    val classTime = eachDay[i]
                    val hour = timeArray[classTime][0].toString()
                    val minute = if(timeArray[classTime][1] == 0){"00"}else{"30"}
                    timeStringArray[i] =  formatTimeString(dayStringArray[i], classTime.toString(), hour, minute)
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
            val timeStringArray = Array<String>(5){""}

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
            val dialog = AlertDialog.Builder(mContext, android.R.style.Theme_DeviceDefault_Light_Dialog)
            dialog.setMessage("시간표 사진이 아닌 것 같아요!")
                    .setTitle("에러!")
                    .setPositiveButton("다시 하기"){ _, _ ->
                        openGallery()
                        Toast.makeText(mContext, "다시 선택할게요~!", Toast.LENGTH_SHORT).show()
                    }.setNegativeButton("취소") {_,_ ->
                        Toast.makeText(mContext, "취소", Toast.LENGTH_SHORT).show()
                    }
                    .setCancelable(false)
                    .show()
        } else {
            val dialog = AlertDialog.Builder(mContext, android.R.style.Theme_DeviceDefault_Light_Dialog)

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
                        Toast.makeText(mContext, "알람이 설정되었습니다!", Toast.LENGTH_SHORT).show()
                        dbInsert(dbMon, dbTue, dbWed, dbThu, dbFri)
                        sendResult(dbMon, dbTue, dbWed, dbThu, dbFri, 30)
                    }
                    .setPositiveButton("다시하기") { _, _ ->
                        openGallery()
                        Toast.makeText(mContext, "다시 선택할게요~!", Toast.LENGTH_SHORT).show()
                    }
                    .setCancelable(false)
                    .show()
        }
    }

    private fun formatTimeString(day: String, classTime: String, hour: String, minute: String): String =
            "${day} = ${classTime}교시 ${hour}:${minute}"

    private fun formatDbString(hour: String, minute: String): String =
            "${hour},${minute}"

    companion object {
        init {
            System.loadLibrary("opencv_java4");
            System.loadLibrary("native-lib");
        }
    }
}
