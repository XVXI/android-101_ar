package com.transcode.smartcity101p2

import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.adapter.MarketOrderAdapter
import com.transcode.smartcity101p2.contract.MarketSuccessOrderActivityContract
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.model.buyapi.response.GetAllOrderResponse
import com.transcode.smartcity101p2.presenter.MarketSuccessOrderActivityPresenter
import com.transcode.smartcity101p2.view.CustomAppBarLayout2
import kotlinx.android.synthetic.main.activity_market_order.*
import kotlinx.android.synthetic.main.appbar_main2.view.*

class MarketSuccessOrderActivity : CoreActivity(), MarketSuccessOrderActivityContract.View {

    lateinit var presenter: MarketSuccessOrderActivityPresenter
    lateinit var adapter: MarketOrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_market_order)

        presenter = MarketSuccessOrderActivityPresenter(this)
        iniView()
    }

    private fun iniView() {
        val appBar = appbar as CustomAppBarLayout2
        appBar.setTitle("")
        appBar.textTitle2.text = "คำสั่งซื้อสำเร็จ"
        val img = ContextCompat.getDrawable(this, R.mipmap.market_icon_menu_order_header)
        img?.setBounds(0, 0, resources.getDimensionPixelSize(R.dimen.dp30), resources.getDimensionPixelSize(R.dimen.dp30))
        appBar.textTitle2.setCompoundDrawables(null, null, img, null)
        appBar.setHeaderColor(resources.getColor(R.color.purple))
        appBar.leftBt.setOnClickListener {
            finish()
        }

        adapter = MarketOrderAdapter(this)
        adapter.setRecyclerView(recycler_order)

        if (!isGuest()) {
            presenter.getSuccessOrders()
        }
    }

    override fun getOrderSuccess(list: ArrayList<GetAllOrderResponse.GetAllOrderResponseData>) {
        adapter.setData(list)
        adapter.notifyDataSetChanged()
    }

    private fun isGuest(): Boolean {
        val user = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        return user.authority_info?.IsFb == "2"
    }
}