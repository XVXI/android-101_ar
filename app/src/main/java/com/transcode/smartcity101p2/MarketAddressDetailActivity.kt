package com.transcode.smartcity101p2

import android.app.Activity
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.contract.MarketAddressDetailActivityContract
import com.transcode.smartcity101p2.model.buyapi.response.AddressResponse
import com.transcode.smartcity101p2.presenter.MarketAddressDetailActivityPresenter
import com.transcode.smartcity101p2.view.CustomAppBarLayout2
import kotlinx.android.synthetic.main.appbar_main2.view.*
import kotlinx.android.synthetic.main.activity_addressdetail.*


class MarketAddressDetailActivity : CoreActivity(), MarketAddressDetailActivityContract.View {

    var data: AddressResponse.AddressResponseData? = null
    lateinit var presenter: MarketAddressDetailActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addressdetail)

        presenter = MarketAddressDetailActivityPresenter(this)

        val appBar = appbar as CustomAppBarLayout2
        appBar.setTitle("ที่อยู่")
        appBar.setHeaderBackground(R.drawable.bg_gradient_purple)
        appBar.leftBt.setOnClickListener {
            finish()
        }

        val filter = InputFilter { source, start, end, dest, dstart, dend ->
            for (i in start until end) {
                if (!isNumberFilter(source[i])) {
                    return@InputFilter ""
                }
            }
            null
        }
        text_tel.filters = arrayOf(filter)

        intent?.extras?.getString("data")?.let {
            val gson = Gson()
            data = gson.fromJson(it, AddressResponse.AddressResponseData::class.java)

            text_name.setText(data?.name)
            text_detail.setText(data?.address)
            text_tel.setText(data?.tel)

            val defAddress = Hawk.get<AddressResponse.AddressResponseData>("DEF_ADDRESS")

            def.isChecked = data?.address_id == defAddress.address_id

            def.setOnClickListener {
                Hawk.delete("DEF_ADDRESS")
                Hawk.put("DEF_ADDRESS", data)

                setResult(Activity.RESULT_OK)
                finish()
            }

            del.setOnClickListener {
                presenter.deleteAddress(data?.address_id ?: "")
            }

            def.visibility = View.VISIBLE
            del.visibility = View.VISIBLE
            ok.visibility = View.VISIBLE

            ok.text = "เปลี่ยนแปลงข้อมูล"

            ok.setOnClickListener {
                if (text_name.text.isNullOrEmpty()) {
                    Toast.makeText(this, "โปรดกรอกชื่อ", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                if (text_detail.text.isNullOrEmpty()) {
                    Toast.makeText(this, "โปรดกรอกที่อยู่", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                if (text_tel.text.isNullOrEmpty()) {
                    Toast.makeText(this, "โปรดกรอกหมายเลขโทรศัพท์", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                presenter.updateAddress(data?.address_id
                        ?: "", text_name.text.toString(), text_detail.text.toString(), text_tel.text.toString())
            }
        } ?: kotlin.run {
            def.visibility = View.GONE
            del.visibility = View.GONE
            ok.visibility = View.VISIBLE

            ok.setOnClickListener {
                if (text_name.text.isNullOrEmpty()) {
                    Toast.makeText(this, "โปรดกรอกชื่อ", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                if (text_detail.text.isNullOrEmpty()) {
                    Toast.makeText(this, "โปรดกรอกที่อยู่", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                if (text_tel.text.isNullOrEmpty()) {
                    Toast.makeText(this, "โปรดกรอกหมายเลขโทรศัพท์", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                presenter.addAddress(text_name.text.toString(), text_detail.text.toString(), text_tel.text.toString())
            }
        }
    }


    override fun updateSuccess() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun updateError() {
        Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
    }

    private fun isNumberFilter(c: Char): Boolean {
        val l = "1234567890"
        for (ch in l) {
            if (c == ch) {
                return true
            }
        }
        return false
    }
}