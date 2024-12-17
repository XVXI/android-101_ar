package com.transcode.smartcity101p2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.adapter.MenuEmergencyFragmentAdapter
import com.transcode.smartcity101p2.fragment.CreateEmergencyFragment
import com.transcode.smartcity101p2.fragment.EmergencyFragment
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.services.UpdateNotification
import kotlinx.android.synthetic.main.activity_emergency.*
import kotlinx.android.synthetic.main.custom_tab_emergency.view.*

class EmergencyActivity : CoreActivity() {

    lateinit var fragment: EmergencyFragment
    lateinit var menuFragmentAdapter: MenuEmergencyFragmentAdapter

    private val receiver = EmergencyReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergency)

        val filter = IntentFilter(Const.BROADCAST_TOGGLE_NOTIFICATION)
        this.registerReceiver(receiver, filter)

        button_back.setOnClickListener {
            finish()
        }

        setUpViewPager(contentViewpager)

        if (intent?.extras?.getBoolean("list") == true) {
            Handler().postDelayed({
                moveToTab(1)
            }, 1000)
        }

        intent.extras?.let {
            val type = it.getString("type")
            when (type) {
                "emergency" -> Handler().postDelayed({
                    val emer_id = it.getString("emer_id") ?: ""
                    moveToTab(1)

                    val fragment = menuFragmentAdapter.getItem(1)
                    if (fragment is EmergencyFragment) {
                        fragment.openID(emer_id, false)
                    }

                }, 1000)
                "emer_chat" -> Handler().postDelayed({
                    val emer_id = it.getString("emer_id") ?: ""
                    moveToTab(1)

                    val fragment = menuFragmentAdapter.getItem(1)
                    if (fragment is EmergencyFragment) {
                        fragment.openID(emer_id, true)
                    }

                }, 1000)
                else -> {

                }
            }
        }
    }

    override fun onDestroy() {
        this.unregisterReceiver(receiver)
        super.onDestroy()
    }

    private fun setUpViewPager(contentViewpager: ViewPager) {
        menuFragmentAdapter = MenuEmergencyFragmentAdapter(supportFragmentManager, baseContext)
        menuFragmentAdapter.addFragment(CreateEmergencyFragment.newInstance(0, getString(R.string.emergency_emergency_text)))
        menuFragmentAdapter.addFragment(EmergencyFragment.newInstance(1, getString(R.string.emergency_you_emergency_text)))
        menuFragmentAdapter.notifyDataSetChanged()

        contentViewpager.adapter = menuFragmentAdapter
        ViewCompat.setNestedScrollingEnabled(contentViewpager, true)
        setUpTabLayout()
        moveToTab(0)
    }

    private fun setUpTabLayout() {
        contentViewpager?.adapter?.let {
            tableContent.setupWithViewPager(contentViewpager)
            for (i in 0 until it.count step 1) {
                val tab = LayoutInflater.from(this).inflate(R.layout.custom_tab_emergency, null)
                tableContent.getTabAt(i)?.customView = tab
                tableContent.getTabAt(i)?.customView?.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
                val mode = (it as MenuEmergencyFragmentAdapter).getItem(i).arguments?.getInt("MODE", 0)
                        ?: 0
                val title = it.getItem(i).arguments?.getString("TITLE", "") ?: ""
                tableContent.getTabAt(i)?.customView?.tab_text?.text = title
                tableContent.getTabAt(i)?.customView?.setBackgroundResource(R.drawable.selector_emergency)
            }

            tableContent.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {

                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    val mode = menuFragmentAdapter.getItem(tab?.position
                            ?: 0).arguments?.getInt("MODE", 0) ?: 0
                    when (mode) {
                        1 -> {
                            val fragment = menuFragmentAdapter.getItem(tab?.position ?: 0)
                            if (fragment is EmergencyFragment) {
                                fragment.fetch()
                            }
                        }
                    }
                }

            })
        }
    }

    override fun onResume() {
        val fragment = menuFragmentAdapter.getItem(1)
        if (fragment is EmergencyFragment) {
            if (fragment.isFragmentReady()) {
                fragment.fetch()
            }
        }
        updateEmergencyTab()

        val updateNotification = UpdateNotification()
        val login = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        updateNotification.updateComplain(login?.authority_info?.CitizenId ?: "", this)
        updateNotification.updateEmergency(login?.authority_info?.CitizenId ?: "", this)

        super.onResume()
    }

    fun moveToTab(position: Int) {
        contentViewpager?.currentItem = position
    }

    inner class EmergencyReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val fragment = menuFragmentAdapter.getItem(1)
            if (fragment is EmergencyFragment) {
                fragment.fetch()
            }
            updateEmergencyTab()
        }
    }

    fun updateEmergencyTab() {
        val emer_db = Hawk.get<ArrayList<HashMap<String, String>>>(Const.EMER_CHAT_DB)
                ?: arrayListOf()
        var count = 0

        for (data in emer_db) {
            if (!data["emer_id"].isNullOrEmpty()) {
                count += (data["count"] ?: "0").toInt()
            }
        }
        tableContent?.getTabAt(1)?.let { tab ->
            if (count > 0) {
                tab.customView?.tab_count?.visibility = View.VISIBLE
                tab.customView?.tab_count?.text = count.toString()
            } else {
                tab.customView?.tab_count?.visibility = View.GONE
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        val fragment = menuFragmentAdapter.getItem(0)
        if (fragment is CreateEmergencyFragment) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}