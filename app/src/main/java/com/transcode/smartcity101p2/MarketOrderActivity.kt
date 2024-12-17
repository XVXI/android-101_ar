package com.transcode.smartcity101p2

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.Window
import android.widget.Toast
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.adapter.MarketOrderAdapter
import com.transcode.smartcity101p2.contract.MarketOrderActivityContract
import com.transcode.smartcity101p2.dialog.ConfirmGoodsDialog
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.model.buyapi.response.GetAllOrderResponse
import com.transcode.smartcity101p2.presenter.MarketOrderActivityPresenter
import com.transcode.smartcity101p2.view.CustomAppBarLayout2
import kotlinx.android.synthetic.main.activity_market_order.*
import kotlinx.android.synthetic.main.appbar_main2.view.*
import kotlinx.android.synthetic.main.dialog_confirm_goods.view.*

class MarketOrderActivity : CoreActivity(), MarketOrderActivityContract.View, MarketOrderAdapter.ClickConfirmItem {

    lateinit var presenter: MarketOrderActivityPresenter
    lateinit var adapter: MarketOrderAdapter
    var confirmGoodsDialog: ConfirmGoodsDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_market_order)

        presenter = MarketOrderActivityPresenter(this)
        iniView()
    }

    private fun iniView() {
        val appBar = appbar as CustomAppBarLayout2
        appBar.setTitle("")
        appBar.textTitle2.text = "รายการสั่งซื้อ"
        val img = ContextCompat.getDrawable(this, R.mipmap.market_icon_order_header)
        img?.setBounds(0, 0, resources.getDimensionPixelSize(R.dimen.dp30), resources.getDimensionPixelSize(R.dimen.dp30))
        appBar.textTitle2.setCompoundDrawables(null, null, img, null)
        appBar.setHeaderBackground(R.drawable.bg_gradient_purple)
        appBar.leftBt.setOnClickListener {
            finish()
        }

        adapter = MarketOrderAdapter(this)
        adapter.setRecyclerView(recycler_order)
        adapter.setConfirmClickListener(this)

        if (!isGuest()) {
            presenter.getAllOrders()
        }
    }

    override fun getOrderSuccess(list: ArrayList<GetAllOrderResponse.GetAllOrderResponseData>) {
        adapter.setData(list)
        adapter.notifyDataSetChanged()
    }

    override fun onClickConfirmItem(item: GetAllOrderResponse.GetAllOrderResponseData) {
        confirmGoodsDialog = ConfirmGoodsDialog(this, item)
        confirmGoodsDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        confirmGoodsDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        confirmGoodsDialog?.show()

        confirmGoodsDialog?.getRootView()?.let { view ->
            view.dl_cancel.setOnClickListener {
                confirmGoodsDialog?.dismiss()
            }

            view.dl_ok.setOnClickListener {
                if (view.dl_rating.rating > 0) {
                    showLoading()
                    presenter.confirmReceiveOrder(item.order_id
                            ?: "", view.dl_rating.rating.toString(), view.dl_comment.text.toString())
                } else {
                    Toast.makeText(this, "โปรดให้ดาว", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun confirmReceiveOrderSuccess() {
        hideLoading()
        confirmGoodsDialog?.dismiss()
//        presenter.getAllOrders()
        startActivity(Intent(this, MarketSuccessOrderActivity::class.java))
        finish()
    }

    override fun confirmReceiveOrderFail() {
        hideLoading()
        confirmGoodsDialog?.dismiss()
        Toast.makeText(this, "Confirm error", Toast.LENGTH_LONG).show()
    }

    private fun isGuest(): Boolean {
        val user = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        return user.authority_info?.IsFb == "2"
    }
}