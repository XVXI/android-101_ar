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
import com.transcode.smartcity101p2.adapter.TravelFavPlanAdapter
import com.transcode.smartcity101p2.contract.TravelFavPlanFragmentContract
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.model.PlanListResponse
import com.transcode.smartcity101p2.model.travel.response.FavPlanListResponse
import com.transcode.smartcity101p2.presenter.TravelFavPlanFragmentPresenter
import com.transcode.smartcity101p2.view.CustomAppBarLayout2
import kotlinx.android.synthetic.main.appbar_main2.view.*
import kotlinx.android.synthetic.main.fragment_travel_fav_plan.*


class TravelFavPlanFragment : CoreFragment(), TravelFavPlanAdapter.ClickItem, TravelFavPlanFragmentContract.View {
    override fun onClickRemoveFavItem(plain_id: String) {
        val user = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        presenter.deleteFavPlan(user.authority_info?.CitizenId ?: "0", plain_id)
    }

    override fun updateFavPlan(data: ArrayList<FavPlanListResponse.FavPlanListResponseData>) {
        adapter.setData(data)
        adapter.notifyDataSetChanged()
    }

    override fun updateAddDelete() {
        updateView()
    }

    override fun onClickItem(res: PlanListResponse.PlanListResponseData) {
        context?.let {
            val intent = Intent(it, TravelPlanDetailActivity::class.java)
            val gson = GsonBuilder().create()
            val jsonData = gson.toJson(res)
            intent.putExtra("data", jsonData)
            startActivity(intent)
        }
    }

    companion object {
        fun newInstance(mode: Int, title: String): TravelFavPlanFragment {
            return TravelFavPlanFragment().apply {
                val bundle = Bundle()
                bundle.putInt("MODE", mode)
                bundle.putString("TITLE", title)
                arguments = bundle
            }
        }
    }

    lateinit var adapter: TravelFavPlanAdapter

    lateinit var presenter: TravelFavPlanFragmentPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_travel_fav_plan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        val appBar = appbar as CustomAppBarLayout2
        appBar.setTitle(arguments?.getString("TITLE") ?: "")
        appBar.setTitleColor(resources.getColor(R.color.dark_purple))
        appBar.leftBt.setOnClickListener {
            activity?.apply {
                finish()
            }
        }

        context?.let {
            adapter = TravelFavPlanAdapter(it)
            adapter.setClickListener(this)
            adapter.setRecyclerView(recycler_view)
        }

        presenter = TravelFavPlanFragmentPresenter(this)
    }

    fun updateView() {
        val user = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        presenter.getFavPlanList(user.authority_info?.CitizenId ?: "0")
    }
}