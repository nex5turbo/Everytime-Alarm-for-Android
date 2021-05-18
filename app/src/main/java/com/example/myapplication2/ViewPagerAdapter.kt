package com.example.myapplication2

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myapplication2.fragments.ImageFragment
import com.example.myapplication2.fragments.MainFragment
import com.example.myapplication2.fragments.SettingFragment
import com.example.myapplication2.dbutils.DBFunction

class ViewPagerAdapter(private val fa: FragmentActivity, private val database: DBFunction): FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        var frag: Fragment? = null
        val mainFragment = MainFragment(fa, database)
        val imageFragment = ImageFragment(fa, database)
        val settingFragment = SettingFragment(fa, database)

        when (position) {
            0->frag = mainFragment
            1->frag = imageFragment
            2->frag = settingFragment
        }
        return frag!!
    }
}