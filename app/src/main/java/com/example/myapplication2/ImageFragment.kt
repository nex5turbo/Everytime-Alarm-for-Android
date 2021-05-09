package com.example.myapplication2

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
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
import org.opencv.android.Utils
import org.opencv.core.Mat
import java.io.InputStream

class ImageFragment(val database: SQLiteDatabase, val mcontext: Context): Fragment() {

    val GET_GALLERY_IMG = 200
    var resultPath = ""
    var alarmArray:ArrayList<ArrayList<Int>>? = null
    val timeArray = TimeData.timeArray
    lateinit var timeImageView: ImageView
    lateinit var textView: TextView
    lateinit var matResult: Mat
    lateinit var matInput: Mat

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_image, container, false) as ViewGroup
        timeImageView = rootView.findViewById(R.id.timeImage) as ImageView
        textView = rootView.findViewById(R.id.textView) as TextView
        timeImageView.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            startActivityForResult(intent, GET_GALLERY_IMG)
        }
        return rootView
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GET_GALLERY_IMG && resultCode == Activity.RESULT_OK && data != null && data.data != null){
            val isi: InputStream? =
                requireActivity().contentResolver.openInputStream(data.data!!)
            var realPath = RealPath()
            var uri = data?.data
            resultPath = realPath.getRealPath(mcontext, uri!!)!!
            timeImageView.setImageURI(uri)
            var myBit: Bitmap = BitmapFactory.decodeStream(isi)
            timeImageView.setImageBitmap(myBit)
            var k = cvtest(myBit)
            Utils.bitmapToMat(myBit, k)
            Utils.matToBitmap(k, myBit)
            timeImageView.setImageBitmap(myBit)

            if(alarmArray != null){
                var eachDay = AlarmFunction().getFirstEachDay(alarmArray!!)
                var monTime = ""
                var tueTime = ""
                var wedTime = ""
                var thuTime = ""
                var friTime = ""
                var dbMon = ""
                var dbTue = ""
                var dbWed = ""
                var dbThu = ""
                var dbFri = ""

                if (eachDay[0] == -1){
                    monTime = "월공강\n"
                    dbMon = "no"
                }else{
                    var hour = timeArray[eachDay[0]][0].toString()
                    var minute = if(timeArray[eachDay[0]][1] == 0){"00"}else{"30"}
                    monTime = "월요일 = "+(eachDay[0]+1).toString()+"교시 "+hour+":"+minute+"\n"
                    dbMon = hour+","+minute
                }
                if (eachDay[1] == -1){
                    tueTime = "화공강\n"
                    dbTue = "no"
                }else{
                    var hour = timeArray[eachDay[1]][0].toString()
                    var minute = if(timeArray[eachDay[1]][1] == 0){"00"}else{"30"}
                    tueTime = "화요일 = "+(eachDay[1]+1).toString()+"교시 "+hour+":"+minute+"\n"
                    dbTue = hour+","+minute
                }
                if (eachDay[2] == -1){
                    wedTime = "수공강\n"
                    dbWed = "no"
                }else{
                    var hour = timeArray[eachDay[2]][0].toString()
                    var minute = if(timeArray[eachDay[2]][1] == 0){"00"}else{"30"}
                    wedTime = "수요일 = "+(eachDay[2]+1).toString()+"교시 "+hour+":"+minute+"\n"
                    dbWed = hour+","+minute
                }
                if (eachDay[3] == -1){
                    thuTime = "목공강\n"
                    dbThu = "no"
                }else{
                    var hour = timeArray[eachDay[3]][0].toString()
                    var minute = if(timeArray[eachDay[3]][1] == 0){"00"}else{"30"}
                    thuTime = "목요일 = "+(eachDay[3]+1).toString()+"교시 "+hour+":"+minute+"\n"
                    dbThu = hour+","+minute
                }
                if (eachDay[4] == -1){
                    friTime = "금공강"
                    dbFri = "no"
                }else{
                    var hour = timeArray[eachDay[4]][0].toString()
                    var minute = if(timeArray[eachDay[4]][1] == 0){"00"}else{"30"}
                    friTime = "금요일 = "+eachDay[4].toString()+"교시 "+hour+":"+minute
                    dbFri = hour+","+minute
                }
                Log.d("###", monTime + tueTime + wedTime + thuTime + friTime)

                val dialog = AlertDialog.Builder(mcontext, android.R.style.Theme_DeviceDefault_Light_Dialog)
                dialog.setMessage(monTime + tueTime + wedTime + thuTime + friTime)
                    .setTitle("시간표 분석 완료!")
                    .setNegativeButton("설정하기", { dialog, which ->
                        Toast.makeText(mcontext, "알람이 설정되었습니다!", Toast.LENGTH_SHORT).show()
                        dbInsert(dbMon, dbTue, dbWed, dbThu, dbFri)
                        sendResult(monTime,tueTime,wedTime,thuTime,friTime)
                    })
                    .setPositiveButton("다시하기", { dialog, which ->
                        Toast.makeText(mcontext, "다시 선택할게요~!", Toast.LENGTH_SHORT).show()
                    }
                    )
                    .setCancelable(false)
                    .show()
            }else{
                val dialog = AlertDialog.Builder(mcontext, android.R.style.Theme_DeviceDefault_Light_Dialog)
                dialog.setMessage("시간표 사진이 아닌 것 같아요!")
                    .setTitle("에러!")
                    .setPositiveButton("다시 하기", DialogInterface.OnClickListener { dialog, which ->
                        Toast.makeText(mcontext, "다시 선택할게요~!", Toast.LENGTH_SHORT).show()
                    })
                    .setCancelable(false)
                    .show()
            }
        }
    }

    fun sendResult(mon: String, tue: String, wed: String, thu: String, fri: String){
        //send time string to setting fragment
        setFragmentResult("requestKey", bundleOf(
            "mon" to mon,
            "tue" to tue,
            "wed" to wed,
            "thu" to thu,
            "fri" to fri))
    }

    fun dbInsert(mon: String, tue: String, wed: String, thu: String, fri: String) {
        var sql = "delete from timeTable"
        database.execSQL(sql)
        sql = "insert into timeTable (mon, tue, wed, thu, fri) values ('"+mon+"','"+tue+"','"+wed+"','"+thu+"','"+fri+"')"
        database.execSQL(sql)
    }
    fun cvtest(myBit: Bitmap): Mat? {
        matInput = Mat()
        matResult = Mat()
        Utils.bitmapToMat(myBit, matInput)
        if (matResult != null) matResult.release()
        matResult = Mat(matInput.rows(), matInput.cols(), matInput.type())
        var resultArray = OpenCvModule().ConvertRGBtoGray(matInput.nativeObjAddr, matResult.nativeObjAddr)

        if (resultArray == null){
            textView.text = "시간표 사진이 아닌듯?"
            alarmArray = null
        }else {
            var rt = ""
            var count = 1
            for (temp: Int in resultArray) {
                rt += temp.toString()
                rt += " "
                if (count == resultArray.size / 5) {
                    rt += "\n"
                    count = 1
                    continue
                }
                count++
            }
            textView.text = rt
            alarmArray = AlarmFunction().splitArr(resultArray)
        }

        return matResult
    }
    companion object {
        init{
            System.loadLibrary("opencv_java4");
            System.loadLibrary("native-lib");
        }
    }
}