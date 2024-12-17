package com.transcode.smartcity101p2.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.CoreApplication
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.adapter.PlaceTypeListAdapter
import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.model.PlaceTypeResponse
import kotlinx.android.synthetic.main.dialog_select_place_type.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DialogSelectPlaceType(context: Context) : CoreDialog(context), PlaceTypeListAdapter.ClickTypeItem {
    override fun onClickTypeItem(data: PlaceTypeResponse.PlaceTypeResponseData, url: String) {
        dismiss()
        listener?.onSelectType(data.type_id ?: "0", url)
    }

    var b: View? = null
    var listener: SelectTypeListener? = null
    var adapter: PlaceTypeListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLayout()
    }

    private fun initLayout() {
        var inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        b = inflater.inflate(R.layout.dialog_select_place_type, null, false)
        val lp = FrameLayout.LayoutParams(CoreApplication.getScreenWidth() - (CoreApplication.getScreenWidth() / 4), FrameLayout.LayoutParams.WRAP_CONTENT)
        b?.layouts?.layoutParams = lp
        setContentView(b)

        b?.dl_close?.setOnClickListener { dismiss() }

        adapter = PlaceTypeListAdapter(context)
        b?.recyclerView?.let {
            adapter?.setRecyclerView(it)
            adapter?.setClickTypeListener(this)
        }

        getPlaceType()
    }

    private fun getPlaceType() {
        val callbacks = object : Callback<PlaceTypeResponse> {
            override fun onResponse(call: Call<PlaceTypeResponse>?, response: Response<PlaceTypeResponse>?) {
                response?.body()?.data?.let {
                    if (it.size > 0) {
                        val all = PlaceTypeResponse.PlaceTypeResponseData()
                        all.type_name = "All"
                        it.add(0, all)
                    }
                    adapter?.setData(it)
                    adapter?.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<PlaceTypeResponse>?, t: Throwable?) {

            }
        }
        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        var city_id = loginResponse.authority_info?.CityId ?: ""
        ApiRequest.INSTANCE.requestPlaceType(callbacks, city_id)
    }

    private val onClickListener = View.OnClickListener {
        dismiss()
    }

    fun setTypeListener(listener: SelectTypeListener) {
        this.listener = listener
    }

    interface SelectTypeListener {
        fun onSelectType(type: String, url: String)
    }
}