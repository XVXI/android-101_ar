package com.transcode.smartcity101p2.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.GsonBuilder
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.TravelPlanDetailActivity
import com.transcode.smartcity101p2.adapter.AllProvinceAdapter
import com.transcode.smartcity101p2.adapter.PlanListAdapter
import com.transcode.smartcity101p2.contract.TravelPlanFragmentContract
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.model.PlanListResponse
import com.transcode.smartcity101p2.model.ProvinceResponse
import com.transcode.smartcity101p2.presenter.TravelPlanFragmentPresenter
import com.transcode.smartcity101p2.view.CustomAppBarLayout2
import kotlinx.android.synthetic.main.appbar_main2.view.*
import kotlinx.android.synthetic.main.fragment_travel_plan.*


class TravelPlanFragment : CoreFragment(), TravelPlanFragmentContract.View, PlanListAdapter.ClickItem, AllProvinceAdapter.AllProvinceAdapterClickItem {
    override fun onAllProvinceAdapterClickItem(res: ProvinceResponse.ProvinceResponseData) {
        city_id = res.province_id ?: "1"
        city_name = res.province_name ?: ""
        text_city_name.text = getString(R.string.travel_string_t1)
        text_city_name.isSelected = true
        layout_day_1.performClick()
        button_close_province.performClick()
    }

    override fun onclickItem(res: PlanListResponse.PlanListResponseData) {
        context?.let {
            val intent = Intent(it, TravelPlanDetailActivity::class.java)
            val gson = GsonBuilder().create()
            val jsonData = gson.toJson(res)
            intent.putExtra("data", jsonData)
            startActivity(intent)
        }
    }

    companion object {
        fun newInstance(mode: Int, title: String): TravelPlanFragment {
            return TravelPlanFragment().apply {
                val bundle = Bundle()
                bundle.putInt("MODE", mode)
                bundle.putString("TITLE", title)
                arguments = bundle
            }
        }
    }

    var city_name = ""
    var city_id = ""
    lateinit var presenter: TravelPlanFragmentPresenter
    lateinit var adapter: PlanListAdapter
    lateinit var allProvinceAdapter: AllProvinceAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_travel_plan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        presenter = TravelPlanFragmentPresenter(this)
        context?.let {
            adapter = PlanListAdapter(it)
            adapter.setRecyclerView(recycler_view)
            adapter.setClickListener(this)

            allProvinceAdapter = AllProvinceAdapter(it)
            allProvinceAdapter.setRecyclerView(all_province)
            allProvinceAdapter.setClickListener(this)
        }

        val appBar = appbar as CustomAppBarLayout2
        appBar.setTitle(arguments?.getString("TITLE") ?: "")
        appBar.setTitleColor(resources.getColor(R.color.dark_purple))
        appBar.leftBt.setOnClickListener {
            activity?.apply {
                finish()
            }
        }
        appBar.rightBt.visibility = View.GONE
//        appBar.rightBt.load("", R.mipmap.icon_th)

        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        loginResponse?.let {
            city_name = it.authority_info?.CityName.toString()
            city_id = it.authority_info?.CityId.toString()
            text_city_name.text = getString(R.string.travel_string_t1)
            text_city_name.isSelected = true
        }

        layout_day_1.setOnClickListener {
            clear()

            layout_day_1.setBackgroundResource(R.drawable.circle_dark_purple_white)
            text_day_1_int.setTextColor(resources.getColor(R.color.white))
            text_day_1_day.setTextColor(resources.getColor(R.color.white))

            progress.visibility = View.VISIBLE
            presenter.getPlanList("1", city_id)
        }

        layout_day_2.setOnClickListener {
            clear()

            layout_day_2.setBackgroundResource(R.drawable.circle_dark_purple_white)
            text_day_2_int.setTextColor(resources.getColor(R.color.white))
            text_day_2_day.setTextColor(resources.getColor(R.color.white))

            progress.visibility = View.VISIBLE
            presenter.getPlanList("2", city_id)
        }

        layout_day_3.setOnClickListener {
            clear()

            layout_day_3.setBackgroundResource(R.drawable.circle_dark_purple_white)
            text_day_3_int.setTextColor(resources.getColor(R.color.white))
            text_day_3_day.setTextColor(resources.getColor(R.color.white))

            progress.visibility = View.VISIBLE
            presenter.getPlanList("3", city_id)
        }

        layout_day_1.performClick()

        appBar.rightBt.setOnClickListener {
            layout_province.visibility = View.VISIBLE

            val list = Hawk.get<ArrayList<ProvinceResponse.ProvinceResponseData>>("PROVINCE_LIST")
                    ?: arrayListOf()

            if (list.isNotEmpty()) {
                updateProvince(list)
            } else {
                presenter.getProvince()
            }
        }

        button_close_province.setOnClickListener {
            layout_province.visibility = View.GONE
        }
    }

    private fun clear() {
        layout_day_1.setBackgroundResource(R.drawable.circle_white_purple)
        text_day_1_int.setTextColor(resources.getColor(R.color.dark_purple))
        text_day_1_day.setTextColor(resources.getColor(R.color.dark_purple))

        layout_day_2.setBackgroundResource(R.drawable.circle_white_purple)
        text_day_2_int.setTextColor(resources.getColor(R.color.dark_purple))
        text_day_2_day.setTextColor(resources.getColor(R.color.dark_purple))

        layout_day_3.setBackgroundResource(R.drawable.circle_white_purple)
        text_day_3_int.setTextColor(resources.getColor(R.color.dark_purple))
        text_day_3_day.setTextColor(resources.getColor(R.color.dark_purple))
    }

    override fun updatePlanList(list: ArrayList<PlanListResponse.PlanListResponseData>) {
        progress?.visibility = View.GONE
        adapter.setData(list)
        adapter.notifyDataSetChanged()
    }

    override fun updateProvince(data: ArrayList<ProvinceResponse.ProvinceResponseData>) {
        Hawk.delete("PROVINCE_LIST")
        Hawk.put("PROVINCE_LIST", data)

        allProvinceAdapter.setData(data)
        allProvinceAdapter.notifyDataSetChanged()
    }
}