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
import com.transcode.smartcity101p2.adapter.MenuComplainFragmentAdapter
import com.transcode.smartcity101p2.fragment.ComplainFragment
import com.transcode.smartcity101p2.fragment.CreateComplainFragment
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.services.UpdateNotification
import kotlinx.android.synthetic.main.activity_complain.*
import kotlinx.android.synthetic.main.custom_tab_complain.view.*

class ComplainActivity : CoreActivity() {

    lateinit var fragment: ComplainFragment
    lateinit var menuFragmentAdapter: MenuComplainFragmentAdapter

    private val receiver = ComplainReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complain)

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
                "complain" -> Handler().postDelayed({
                    val complain_id = it.getString("complain_id")
                    moveToTab(1)

                    val fragment = menuFragmentAdapter.getItem(1)
                    if (fragment is ComplainFragment) {
                        fragment.openID(complain_id, false)
                    }

                }, 1000)
                "complain_chat" -> Handler().postDelayed({
                    val complain_id = it.getString("complain_id")
                    moveToTab(1)

                    val fragment = menuFragmentAdapter.getItem(1)
                    if (fragment is ComplainFragment) {
                        fragment.openID(complain_id, true)
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
        menuFragmentAdapter = MenuComplainFragmentAdapter(supportFragmentManager, baseContext)
        menuFragmentAdapter.addFragment(CreateComplainFragment.newInstance(0, getString(R.string.complain_complain_text)))
        menuFragmentAdapter.addFragment(ComplainFragment.newInstance(1, getString(R.string.complain_you_complain_text)))
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
                val tab = LayoutInflater.from(this).inflate(R.layout.custom_tab_complain, null)
                tableContent.getTabAt(i)?.customView = tab
                tableContent.getTabAt(i)?.customView?.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
                val mode = (it as MenuComplainFragmentAdapter).getItem(i).arguments?.getInt("MODE", 0)
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
                            if (fragment is ComplainFragment) {
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
        if (fragment is ComplainFragment) {
            if (fragment.isFragmentReady()) {
                fragment.fetch()
            }
        }
        updateComplainTab()

        val updateNotification = UpdateNotification()
        val login = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        updateNotification.updateComplain(login?.authority_info?.CitizenId ?: "", this)
        updateNotification.updateEmergency(login?.authority_info?.CitizenId ?: "", this)

        super.onResume()
    }

    fun moveToTab(position: Int) {
        contentViewpager?.currentItem = position
    }

    inner class ComplainReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val fragment = menuFragmentAdapter.getItem(1)
            if (fragment is ComplainFragment) {
                fragment.fetch()
            }
            updateComplainTab()
        }
    }

    fun updateComplainTab() {
        val complain_db = Hawk.get<ArrayList<HashMap<String, String>>>(Const.COMPLAIN_CHAT_DB)
                ?: arrayListOf()
        var count = 0

        for (data in complain_db) {
            if (!data["complain_id"].isNullOrEmpty()) {
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
        if (fragment is CreateComplainFragment) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}