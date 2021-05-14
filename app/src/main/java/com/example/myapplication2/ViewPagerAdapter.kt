package com.example.myapplication2

import android.database.sqlite.SQLiteDatabase
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myapplication2.fragments.ImageFragment
import com.example.myapplication2.fragments.MainFragment
import com.example.myapplication2.fragments.SettingFragment
import com.example.myapplication2.utils.DBFunction

class ViewPagerAdapter(private val fa: FragmentActivity, private val database: SQLiteDatabase, private val db: DBFunction): FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        var frag: Fragment? = null
        when (position) {
            0->frag = MainFragment(fa, db)
            1->frag = ImageFragment(database, fa, db)
            2->frag = SettingFragment(fa, db)
            3->frag = SettingFragment(fa, db)
            4->frag = SettingFragment(fa, db)
        }
        return frag!!
    }
}