package com.transcode.smartcity101p2.adapter

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.TypePlaceAdapterContract
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.model.*
import com.transcode.smartcity101p2.model.travel.response.FavPlaceListResponse
import com.transcode.smartcity101p2.presenter.SuggestPlaceAdapterPresenter
import kotlinx.android.synthetic.main.item_sugest_place.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SuggestPlaceAdapter(var context: Context) : AppBaseAdapter(), TypePlaceAdapterContract.View {

    override fun updateFavPlace(data: ArrayList<FavPlaceListResponse.FavPlaceListResponseData>) {
        favPlaceList = data
        notifyDataSetChanged()
    }

    override fun updateAddDelete() {
        val user = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        presenter.getFavPlaceList(user.authority_info?.CitizenId ?: "0")
    }

    var list = arrayListOf<PlaceSuggestionResponse.PlaceSuggestionResponseData>()
    private var listener: AllSearchAdapter.AllSearchAdapterClickItem? = null

    var presenter = SuggestPlaceAdapterPresenter(this)
    var favPlaceList = arrayListOf<FavPlaceListResponse.FavPlaceListResponseData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_sugest_place, parent, false)
        val viewHolder = ViewHolder(view)
        view.layoutParams = lp
        return viewHolder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.itemView.item_plan_text_remark.text = data.place_name

        holder.itemView.item_plan_image.load("" ?: "", R.mipmap.icon_travel_header)

        holder.itemView.setOnClickListener {
            listener?.onAllSearchAdapterClickItem(data.place_name ?: "", data.place_id ?: "")
        }

        var isFav = false

        for (fav in favPlaceList) {
            if (data.place_id == fav.place_id) {
                isFav = true
                break
            }
        }

        if (isFav) {
            holder.itemView.iconfav.load("", R.mipmap.icon_fav_active)
        } else {
            holder.itemView.iconfav.load("", R.mipmap.icon_fav_inactive)
        }

        holder.itemView.iconfav.setOnClickListener {
            if (isFav) {
                val user = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
                presenter.deleteFavPlace(user.authority_info?.CitizenId
                        ?: "0", data.place_id ?: "0")
            } else {
                val user = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
                presenter.addFavPlace(user.authority_info?.CitizenId
                        ?: "0", data.place_id ?: "0")
            }
        }
    }

    fun setRecyclerView(rv: RecyclerView) {
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(rv.context, LinearLayoutManager.HORIZONTAL, false)
        rv.adapter = this
    }

    fun setData(list: ArrayList<PlaceSuggestionResponse.PlaceSuggestionResponseData>) {
        this.list = list
    }

    fun setClickListener(listener: AllSearchAdapter.AllSearchAdapterClickItem?) {
        this.listener = listener
    }

    private fun favIt(place_id: String) {
        val callbacks = object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>?, response: Response<CommonResponse>?) {
                Log.e("success", "success")
            }

            override fun onFailure(call: Call<CommonResponse>?, t: Throwable?) {
                Log.e("error", "error")
            }
        }

        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val citizen_id = loginResponse.authority_info?.CitizenId ?: ""
        ApiRequest.INSTANCE.requestPlaceFav(callbacks, PlaceFavRequest(place_id, citizen_id))
    }
}