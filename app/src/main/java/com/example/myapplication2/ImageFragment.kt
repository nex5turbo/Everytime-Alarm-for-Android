package com.example.myapplication2

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult

class ImageFragment(val database: SQLiteDatabase): Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_image, container, false) as ViewGroup
        val timeImage = rootView.findViewById(R.id.timeImage) as ImageView
        timeImage.setOnClickListener{
            Log.d("###", "Image View Clicked in Image")
            val result = "result"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))

        }
        return rootView
    }
}