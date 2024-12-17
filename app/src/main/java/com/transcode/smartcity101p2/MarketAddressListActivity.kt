package com.transcode.smartcity101p2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.google.gson.GsonBuilder
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.contract.MarketAddressListActivityContract
import com.transcode.smartcity101p2.model.buyapi.response.AddressResponse
import com.transcode.smartcity101p2.presenter.MarketAddressListActivityPresenter
import com.transcode.smartcity101p2.view.CustomAppBarLayout2
import kotlinx.android.synthetic.main.appbar_main2.view.*
import kotlinx.android.synthetic.main.activity_addresslist.*
import kotlinx.android.synthetic.main.view_address2.view.*


class MarketAddressListActivity : CoreActivity(), MarketAddressListActivityContract.View {

    lateinit var presenter: MarketAddressListActivityPresenter
    var addressList = arrayListOf<AddressResponse.AddressResponseData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addresslist)

        presenter = MarketAddressListActivityPresenter(this)

        val appBar = appbar as CustomAppBarLayout2
        appBar.setTitle("ที่อยู่")
        appBar.setHeaderBackground(R.drawable.bg_gradient_purple)
        appBar.leftBt.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }

        add_address.setOnClickListener {
            startActivityForResult(Intent(this, MarketAddressDetailActivity::class.java), 3310)
        }

        presenter.getAllAddress()
    }

    override fun updateAddressView(list: ArrayList<AddressResponse.AddressResponseData>) {
        val defAddress = Hawk.get<AddressResponse.AddressResponseData>("DEF_ADDRESS")
        addressList = list
        if (list.isNotEmpty()) {
            if (defAddress == null) {
                Hawk.delete("DEF_ADDRESS")
                Hawk.put("DEF_ADDRESS", list[0])
            } else {
                var inList = false
                for (data in list) {
                    if (data.address_id == defAddress.address_id) {
                        inList = true
                    }
                }
                if (!inList) {
                    Hawk.delete("DEF_ADDRESS")
                    Hawk.put("DEF_ADDRESS", list[0])
                }
            }
        } else {
            Hawk.delete("DEF_ADDRESS")
        }
        updateAddressLayout()
    }

    override fun updateSuccess() {
        presenter.getAllAddress()
    }

    override fun updateError() {

    }

    private fun updateAddressLayout() {

        if (addressList.size >= 3) {
            add_address.visibility = View.GONE
        } else {
            add_address.visibility = View.VISIBLE
        }

        val defAddress = Hawk.get<AddressResponse.AddressResponseData>("DEF_ADDRESS")
        layout_address.removeAllViews()

        if (addressList.isNotEmpty()) {
            for (data in addressList) {
                val view = LayoutInflater.from(this).inflate(R.layout.view_address2, null)

                view.text_name.text = data.name
                view.text_tel.text = data.tel
                view.text_address.text = data.address

                if (data.address_id == defAddress.address_id) {
                    view.text_defaddress.visibility = View.VISIBLE
                } else {
                    view.text_defaddress.visibility = View.INVISIBLE
                }

                view.tag = data

                view.text_edit.setOnClickListener {
                    val gson = GsonBuilder().create()
                    val jsonData = gson.toJson(data)
                    startActivityForResult(Intent(this, MarketAddressDetailActivity::class.java).apply {
                        putExtra("data", jsonData)
                    }, 3310)
                }

                layout_address.addView(view)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                3310 -> {
                    presenter.getAllAddress()
                }
            }
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        super.onBackPressed()
    }
}