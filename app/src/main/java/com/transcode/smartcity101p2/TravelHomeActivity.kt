package com.transcode.smartcity101p2

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.transcode.smartcity101p2.adapter.MenuFragmentAdapter
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.fragment.*
import kotlinx.android.synthetic.main.activity_travel.*
import kotlinx.android.synthetic.main.custom_tab_travel.view.*

class TravelHomeActivity : CoreActivity() {

    lateinit var menuFragmentAdapter: MenuFragmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_travel)

        contentViewpager.offscreenPageLimit = 4
        setUpViewPager(contentViewpager)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION) &&
//                    shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
//                showLocationPermissionReasonable()
//            } else {
//                ActivityCompat.requestPermissions(this,
//                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), 100)
//            }
//        }else{
        ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), 100)
//        }
    }

    private fun showLocationPermissionReasonable() {
        val alertBuilder = AlertDialog.Builder(this, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen)
        alertBuilder.setCancelable(false)
        alertBuilder.setTitle("Use your location")
        alertBuilder.setMessage("This app collects location data to see your current location in map [" + getString(R.string.home_emergency) + "] , " +
                "[" + getString(R.string.home_complain) + "] , " +
                "[" + getString(R.string.home_cctv) + "] & " +
                "[" + getString(R.string.home_travel) + "] even when the app is closed or not in use.")
        alertBuilder.setPositiveButton("Turn on", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                ActivityCompat.requestPermissions(this@TravelHomeActivity,
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), 100)
            }
        })

        alertBuilder.setNegativeButton("No thanks", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {

            }
        })

        val alert = alertBuilder.create()
        alert.show()
    }

    private fun setUpViewPager(contentViewpager: ViewPager) {
        menuFragmentAdapter = MenuFragmentAdapter(supportFragmentManager, this)
        menuFragmentAdapter.addFragment(TravelPlanFragment.newInstance(FragmentForPager.PLAN, getString(R.string.travel_string_plan)))
        menuFragmentAdapter.addFragment(TravelPlaceFragment.newInstance(FragmentForPager.PLACE, getString(R.string.travel_string_place)))
        menuFragmentAdapter.addFragment(TravelFavPlanFragment.newInstance(FragmentForPager.FAV_PLAN, getString(R.string.travel_string_fplan)))
        menuFragmentAdapter.addFragment(TravelFavPlaceFragment.newInstance(FragmentForPager.FAV_PLACE, getString(R.string.travel_string_fplace)))
//        menuFragmentAdapter.addFragment(TravelStampFragment.newInstance(FragmentForPager.TRAVEL, getString(R.string.home_travel)))
        menuFragmentAdapter.notifyDataSetChanged()

        contentViewpager.adapter = menuFragmentAdapter
        setUpTabLayout()
    }

    override fun onResume() {
        super.onResume()
        val fragment = menuFragmentAdapter.getItem(tableContent.selectedTabPosition)
        if (fragment is TravelFavPlanFragment) {
            fragment.updateView()
        } else if (fragment is TravelFavPlaceFragment) {
            fragment.updateView()
        }
    }

    private fun setUpTabLayout() {
        contentViewpager?.adapter?.let {
            tableContent.setupWithViewPager(contentViewpager)
            for (i in 0 until it.count step 1) {
                val tab = LayoutInflater.from(this).inflate(R.layout.custom_tab_travel, null)
                tableContent.getTabAt(i)?.customView = tab
                tableContent.getTabAt(i)?.customView?.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
                val mode = (it as MenuFragmentAdapter).getItem(i).arguments?.getInt("MODE", 0) ?: 0
                tableContent.getTabAt(i)?.customView?.tab_text?.text = it.getItem(i).arguments?.getString("TITLE", "")
                        ?: ""
                tableContent.getTabAt(i)?.customView?.tab_text?.isSelected = true
                when (mode) {
                    FragmentForPager.PLAN -> {
                        tableContent.getTabAt(i)?.customView?.tab_image?.load("", R.mipmap.icon_plan)
                    }
                    FragmentForPager.PLACE -> {
                        tableContent.getTabAt(i)?.customView?.tab_image?.load("", R.mipmap.icon_travel_place)
                    }
                    FragmentForPager.FAV_PLAN -> {
                        tableContent.getTabAt(i)?.customView?.tab_image?.load("", R.mipmap.icon_fav_plan)
                    }
                    FragmentForPager.FAV_PLACE -> {
                        tableContent.getTabAt(i)?.customView?.tab_image?.load("", R.mipmap.icon_fav_place)
                    }
                    FragmentForPager.TRAVEL -> {
                        tableContent.getTabAt(i)?.customView?.tab_image?.load("", R.mipmap.icon_t_travel)
                    }
                }
            }

            tableContent.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {

                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    hideSoftKeyboard()

                    when (menuFragmentAdapter.getItem(tab?.position
                            ?: 0).arguments?.getInt("MODE", 0) ?: 0) {
                        FragmentForPager.FAV_PLAN -> {
                            val fragment = menuFragmentAdapter.getItem(tab?.position ?: 0)
                            if (fragment is TravelFavPlanFragment) {
                                fragment.updateView()
                            }
                        }
                        FragmentForPager.FAV_PLACE -> {
                            val fragment = menuFragmentAdapter.getItem(tab?.position ?: 0)
                            if (fragment is TravelFavPlaceFragment) {
                                fragment.updateView()
                            }
                        }
                        FragmentForPager.TRAVEL -> {
//                            val fragment = menuFragmentAdapter.getItem(tab?.position ?: 0)
//                            if (fragment is TravelStampFragment) {
//                                fragment.showStampPlaceByID("")
//                            }
                        }
                    }
                }
            })
        }
    }
}