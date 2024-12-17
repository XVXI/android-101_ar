package com.transcode.smartcity101p2.adapter

import android.content.Context
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import java.util.ArrayList

class MenuComplainFragmentAdapter(fm: FragmentManager?, val context: Context) : FragmentStatePagerAdapter(fm) {
    private val mFragments = ArrayList<Fragment>()
    fun addFragment(fragment: Fragment) {
        mFragments.add(fragment)
    }

    override fun getItem(position: Int): Fragment {
        return mFragments[position]
    }

    override fun getCount(): Int {
        return mFragments.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return mFragments[position].arguments?.getString("TITLE", "") ?: ""
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun saveState(): Parcelable? {
        return null
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
    }
}