package com.won.naz

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.won.naz.fragments.ImageFragment
import com.won.naz.fragments.MainFragment
import com.won.naz.fragments.SettingFragment
import com.won.naz.dbutils.DBFunction

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