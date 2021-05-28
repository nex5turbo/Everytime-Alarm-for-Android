package com.example.myapplication2.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.myapplication2.R
import com.example.myapplication2.utils.RealPath

const val RINGTONE_REQUEST_CODE = 100
class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        if (supportFragmentManager.findFragmentById(android.R.id.content) == null) {
            supportFragmentManager.beginTransaction().add(android.R.id.content, SettingsFragment() as Fragment).commit()
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        lateinit var filePicker: Preference
        lateinit var preTime: androidx.preference.EditTextPreference
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)
            filePicker = findPreference("musicPath")!!
            preTime = findPreference("preTimeText")!!
            var musicPath = filePicker.sharedPreferences.getString("musicPath", "")!!
            var preTimeText = preTime.sharedPreferences.getString("preTimeText", "30")

            preTimeText += "분 전"
            musicPath = if (musicPath == "") {"기본 알람음"} else {RealPath().getJustName(musicPath)}

            filePicker.summary = musicPath
            preTime.summary = preTimeText

            filePicker.setOnPreferenceClickListener {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "audio/*")
                startActivityForResult(intent, RINGTONE_REQUEST_CODE)
                true
            }

            preTime.setOnPreferenceChangeListener { preference, newValue ->
                val editor = preference.sharedPreferences.edit()
                editor.putInt("preTime", newValue.toString().toInt())
                editor.apply()
                preTime.summary = (newValue.toString() + "분 전")
                true
            }

            preTime.setOnBindEditTextListener {
                it.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
            }
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == RINGTONE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
                val realPath = RealPath()
                val uri = data.data
                val resultPath = realPath.getRealPath(requireContext(), uri!!)!!

                filePicker.summary = realPath.getJustName(resultPath)
                val editor = filePicker.sharedPreferences.edit()
                editor.putString("musicPath", resultPath)
                editor.apply()
            }
        }
    }

    override fun onBackPressed() {
        setResult(RESULT_OK)
        finish()
        super.onBackPressed()
    }
}
