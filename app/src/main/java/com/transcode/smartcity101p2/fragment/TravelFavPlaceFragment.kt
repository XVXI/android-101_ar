package com.transcode.smartcity101p2.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.TravelPlaceDetailActivity
import com.transcode.smartcity101p2.adapter.TravelFavPlaceAdapter
import com.transcode.smartcity101p2.contract.TravelFavPlaceFragmentContract
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.model.travel.response.FavPlaceListResponse
import com.transcode.smartcity101p2.presenter.TravelFavPlaceFragmentPresenter
import com.transcode.smartcity101p2.view.CustomAppBarLayout2
import kotlinx.android.synthetic.main.appbar_main2.view.*
import kotlinx.android.synthetic.main.fragment_travel_fav_place.*


class TravelFavPlaceFragment : CoreFragment(), TravelFavPlaceAdapter.ClickItem, TravelFavPlaceFragmentContract.View {
    override fun onClickRemoveFavItem(place_id: String) {
        val user = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        presenter.deleteFavPlace(user.authority_info?.CitizenId ?: "0", place_id)
    }

    override fun updateFavPlace(data: ArrayList<FavPlaceListResponse.FavPlaceListResponseData>) {
        adapter.setData(data)
        adapter.notifyDataSetChanged()
    }

    override fun updateAddDelete() {
        updateView()
    }

    override fun onClickItem(title: String, place_id: String) {
        context?.let {
            val intent = Intent(it, TravelPlaceDetailActivity::class.java)
            intent.putExtra("title", title)
            intent.putExtra("place_id", place_id)
            startActivity(intent)
        }
    }

    companion object {
        fun newInstance(mode: Int, title: String): TravelFavPlaceFragment {
            return TravelFavPlaceFragment().apply {
                val bundle = Bundle()
                bundle.putInt("MODE", mode)
                bundle.putString("TITLE", title)
                arguments = bundle
            }
        }
    }

    lateinit var adapter: TravelFavPlaceAdapter

    lateinit var presenter: TravelFavPlaceFragmentPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_travel_fav_place, container, false)
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
            adapter = TravelFavPlaceAdapter(it)
            adapter.setClickListener(this)
            adapter.setRecyclerView(recycler_view)
        }

        presenter = TravelFavPlaceFragmentPresenter(this)
    }

    fun updateView() {
        val user = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        presenter.getFavPlaceList(user.authority_info?.CitizenId ?: "0")
    }
}