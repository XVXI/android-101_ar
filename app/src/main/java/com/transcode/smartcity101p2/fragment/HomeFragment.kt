package com.transcode.smartcity101p2.fragment

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.adapter.MenuFragmentAdapter
import com.transcode.smartcity101p2.adapter.QueueAdapter
import com.transcode.smartcity101p2.contract.HomeFragmentContract
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.presenter.HomeFragmentPresenter
import kotlinx.android.synthetic.main.custom_tab.view.*
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : CoreFragment(), HomeFragmentContract.View {
    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment().apply {
                val bundle = Bundle()
                arguments = bundle
            }
        }
    }

    lateinit var presenter: HomeFragmentPresenter
    lateinit var menuFragmentAdapter: MenuFragmentAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        presenter = HomeFragmentPresenter(this)

        setUpViewPager(contentViewpager)
    }

    private fun setUpViewPager(contentViewpager: ViewPager) {
        context?.let {
            menuFragmentAdapter = MenuFragmentAdapter(fragmentManager, it)
            menuFragmentAdapter.addFragment(FragmentForPager.newInstance(FragmentForPager.NEWS, "News"))
            menuFragmentAdapter.addFragment(FragmentForPager.newInstance(FragmentForPager.QUEUE, "Queue"))
            menuFragmentAdapter.addFragment(FragmentForPager.newInstance(FragmentForPager.ACCOUNT, "Account"))
            menuFragmentAdapter.notifyDataSetChanged()

            contentViewpager.adapter = menuFragmentAdapter
            setUpTabLayout()
        }
    }

    fun moveToMenu(position: Int) {
        contentViewpager.setCurrentItem(position, true)
    }

    private fun setUpTabLayout() {
        contentViewpager?.adapter?.let {
            tableContent.setupWithViewPager(contentViewpager)
            for (i in 0 until it.count step 1) {
                val tab = LayoutInflater.from(context).inflate(R.layout.custom_tab, null)
                tableContent.getTabAt(i)?.customView = tab
                tableContent.getTabAt(i)?.customView?.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
                val mode = (it as MenuFragmentAdapter).getItem(i).arguments?.getInt("MODE", 0) ?: 0
                when (mode) {
                    FragmentForPager.NEWS -> {
                        tableContent.getTabAt(i)?.customView?.tab_image?.load("", R.drawable.selector_news)
                    }
                    FragmentForPager.QUEUE -> {
                        tableContent.getTabAt(i)?.customView?.tab_image?.load("", R.drawable.selector_queue)
                    }
                    FragmentForPager.ACCOUNT -> {
                        tableContent.getTabAt(i)?.customView?.tab_image?.load("", R.drawable.selector_user)
                    }
                }
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
                        FragmentForPager.NEWS -> {

                        }
                        FragmentForPager.QUEUE -> {
                            val adapter = (menuFragmentAdapter.getItem(tab?.position
                                    ?: 0) as FragmentForPager).getBaseAdapter()
                            adapter?.let {
                                (it as QueueAdapter).refresh()
                            }
                        }
                        FragmentForPager.ACCOUNT -> {

                        }
                    }
                }

            })
        }
    }

    fun getCurrentTab(): Int = tableContent?.selectedTabPosition ?: 0

}