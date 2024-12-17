package com.transcode.smartcity101p2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.adapter.MenuFragmentAdapter
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.fragment.*
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import kotlinx.android.synthetic.main.activity_market.*
import kotlinx.android.synthetic.main.custom_tab_market.view.*

class MarketHomeActivity : CoreActivity() {

    lateinit var menuFragmentAdapter: MenuFragmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_market)

        contentViewpager.offscreenPageLimit = 3
        setUpViewPager(contentViewpager)
    }

    private fun setUpViewPager(contentViewpager: ViewPager) {
        menuFragmentAdapter = MenuFragmentAdapter(supportFragmentManager, this)
        menuFragmentAdapter.addFragment(MarketHomeFragment.newInstance(FragmentForPager.MARKET_HOME, "MARKET_HOME"))
//        menuFragmentAdapter.addFragment(MarketCartFragment.newInstance(FragmentForPager.MARKET_CART, "MARKET_CART"))
        menuFragmentAdapter.addFragment(MarketWishListFragment.newInstance(FragmentForPager.MARKET_MY, "MARKET_MY"))
//        menuFragmentAdapter.addFragment(MarketMyFragment.newInstance(FragmentForPager.MARKET_MY, "MARKET_MY"))
        menuFragmentAdapter.notifyDataSetChanged()

        contentViewpager.adapter = menuFragmentAdapter
        setUpTabLayout()
    }

    override fun onResume() {
        super.onResume()

        updateCartTab()
//        val fragment = menuFragmentAdapter.getItem(tableContent.selectedTabPosition)
//        if (fragment is TravelFavPlanFragment) {
//            fragment.updateView()
//        } else if (fragment is TravelFavPlaceFragment) {
//            fragment.updateView()
//        }
    }

    fun updateCartTab() {
//        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
//        val CitizenId = loginResponse.authority_info?.CitizenId ?: ""
//        tableContent?.getTabAt(1)?.let { tab ->
//            val cart_count = Hawk.get<Int>(Const.CART_COUNT + CitizenId) ?: 0
//            if (cart_count > 0) {
//                tab.customView?.tab_count?.visibility = View.VISIBLE
//                tab.customView?.tab_count?.text = cart_count.toString()
//            } else {
//                tab.customView?.tab_count?.visibility = View.GONE
//            }
//        }
    }

    private fun setUpTabLayout() {
        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val CitizenId = loginResponse.authority_info?.CitizenId ?: ""
        contentViewpager?.adapter?.let {
            tableContent.setupWithViewPager(contentViewpager)
            for (i in 0 until it.count step 1) {
                val tab = LayoutInflater.from(this).inflate(R.layout.custom_tab_market, null)
                tableContent.getTabAt(i)?.customView = tab
                tableContent.getTabAt(i)?.customView?.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
                val mode = (it as MenuFragmentAdapter).getItem(i).arguments?.getInt("MODE", 0) ?: 0
                tableContent.getTabAt(i)?.customView?.tab_text?.text = it.getItem(i).arguments?.getString("TITLE", "")
                        ?: ""
                tableContent.getTabAt(i)?.customView?.tab_text?.isSelected = true
                tableContent.getTabAt(i)?.customView?.tab_count?.visibility = View.GONE
                when (mode) {
                    FragmentForPager.MARKET_HOME -> {
                        tableContent.getTabAt(i)?.customView?.tab_image?.load("", R.drawable.selector_market_home)
                    }
                    FragmentForPager.MARKET_CART -> {
                        tableContent.getTabAt(i)?.customView?.tab_image?.load("", R.drawable.selector_market_cart)
                        val cart_count = Hawk.get<Int>(Const.CART_COUNT + CitizenId) ?: 0
                        if (cart_count > 0) {
                            tableContent.getTabAt(i)?.customView?.tab_count?.visibility = View.VISIBLE
                            tableContent.getTabAt(i)?.customView?.tab_count?.text = cart_count.toString()
                        } else {
                            tableContent.getTabAt(i)?.customView?.tab_count?.visibility = View.GONE
                        }
                    }
                    FragmentForPager.MARKET_MY -> {
                        tableContent.getTabAt(i)?.customView?.tab_image?.load("", R.drawable.selector_market_fav)
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
                        FragmentForPager.MARKET_CART -> {
//                            val fragment = menuFragmentAdapter.getItem(tab?.position ?: 0)
//                            if (fragment is MarketCartFragment) {
//                                fragment.updateView()
//                                updateCartTab()
//                            }
                        }
                        FragmentForPager.MARKET_MY -> {
                            val fragment = menuFragmentAdapter.getItem(tab?.position ?: 0)
//                            if (fragment is MarketMyFragment) {
//                                fragment.updateData()
//                            }

                            if (fragment is MarketWishListFragment) {
                                fragment.updateData()
                            }
                        }
                    }
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                3232 -> {
                    contentViewpager?.currentItem = 0
                    val fragment = menuFragmentAdapter.getItem(0)
                    if (fragment is MarketHomeFragment) {
                        fragment.showShop(data?.getStringExtra("shop_id") ?: "")
                    }
                }
//                3310 -> {
//                    contentViewpager?.currentItem = 2
//                    val fragment = menuFragmentAdapter.getItem(2)
//                    if (fragment is MarketMyFragment) {
//                        fragment.updateData()
//                    }
//                }
            }
        }
    }

    override fun onBackPressed() {
        if (contentViewpager?.currentItem == 0) {
            val fragment = menuFragmentAdapter.getItem(0)
            if (fragment is MarketHomeFragment) {
                fragment.checkBack()
            } else {
                super.onBackPressed()
            }
        } else {
            contentViewpager?.currentItem = 0
        }
    }

    fun moveTo(pos: Int) {
        if (pos in 0..2) {
            contentViewpager?.currentItem = pos
            val fragment = menuFragmentAdapter.getItem(pos)
//            if (fragment is MarketMyFragment) {
//                fragment.openOrderActivity()
//            }
        }
    }

    fun showShop(shop_id: String) {
        contentViewpager?.currentItem = 0
        val fragment = menuFragmentAdapter.getItem(0)
        if (fragment is MarketHomeFragment) {
            fragment.showShop(shop_id)
        }
    }
}