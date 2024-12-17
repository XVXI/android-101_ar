package com.transcode.smartcity101p2.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.adapter.*
import kotlinx.android.synthetic.main.fragmentforpager_layout.view.*

class FragmentForPager : CoreFragment() {

    companion object {
        const val NEWS = 0
        const val QUEUE = 1
        const val ACCOUNT = 2

        const val PLAN = 3
        const val PLACE = 4
        const val FAV_PLAN = 5
        const val FAV_PLACE = 6
        const val TRAVEL = 7

        const val MARKET_HOME = 8
        const val MARKET_CART = 9
        const val MARKET_MY = 10

        fun newInstance(mode: Int, title: String): FragmentForPager {
            return FragmentForPager().apply {
                val bundle = Bundle()
                bundle.putInt("MODE", mode)
                bundle.putString("TITLE", title)
                arguments = bundle
            }
        }
    }

    private var mode: Int = 0
    var adapter: AppBaseAdapter? = null
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var queueAdapter: QueueAdapter
    private lateinit var accountAdapter: AccountAdapter
    private lateinit var travelPlanAdapter: TravelPlanAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mode = arguments?.getInt("MODE", 0) ?: 0
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val b = inflater.inflate(R.layout.fragmentforpager_layout, container, false)
        val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        b.layoutParams = lp

        when (mode) {
            NEWS -> {
                context?.let {
                    newsAdapter = NewsAdapter(it, fragmentManager)
                    newsAdapter.setRecyclerView(b.recyclerview)
                    adapter = newsAdapter
                }
            }
            QUEUE -> {
                context?.let {
                    queueAdapter = QueueAdapter(it, fragmentManager)
                    queueAdapter.setRecyclerView(b.recyclerview)
                    adapter = queueAdapter
                }
            }
            ACCOUNT -> {
                context?.let {
                    accountAdapter = AccountAdapter(it, fragmentManager)
                    accountAdapter.setRecyclerView(b.recyclerview)
                    adapter = accountAdapter
                }
            }
            PLAN -> {
                context?.let {
                    travelPlanAdapter = TravelPlanAdapter(it, fragmentManager)
                    travelPlanAdapter.setRecyclerView(b.recyclerview)
                    adapter = travelPlanAdapter
                }
            }
        }
        return b
    }

    fun notifyDataSetChanged() {
        adapter?.notifyDataSetChanged()
    }

    fun getBaseAdapter(): AppBaseAdapter? = adapter
}