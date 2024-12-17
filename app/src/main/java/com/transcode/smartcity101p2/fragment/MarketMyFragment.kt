package com.transcode.smartcity101p2.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.*
import com.transcode.smartcity101p2.contract.MarketMyFragmentContract
import com.transcode.smartcity101p2.extension.loadCircle
import com.transcode.smartcity101p2.model.CitizenInfoResponse
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.GAccount
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.model.buyapi.response.AddressResponse
import com.transcode.smartcity101p2.presenter.MarketMyFragmentPresenter
import com.transcode.smartcity101p2.utils.AppUtils
import kotlinx.android.synthetic.main.fragment_market_my.*
import kotlinx.android.synthetic.main.view_address.view.*

class MarketMyFragment : CoreFragment(), MarketMyFragmentContract.View {
    companion object {
        fun newInstance(mode: Int, title: String): MarketMyFragment {
            return MarketMyFragment().apply {
                val bundle = Bundle()
                bundle.putInt("MODE", mode)
                bundle.putString("TITLE", title)
                arguments = bundle
            }
        }
    }

    lateinit var presenter: MarketMyFragmentPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_market_my, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {

        presenter = MarketMyFragmentPresenter(this)

        checkUserType()

        my_wishlist.setOnClickListener {
            activity?.apply {
                startActivityForResult(Intent(this, MarketWishListActivity::class.java), 3232)
            }
        }

        my_all_order.setOnClickListener {
            openOrderActivity()
        }

        my_success_order.setOnClickListener {
            activity?.apply {
                startActivityForResult(Intent(this, MarketSuccessOrderActivity::class.java), 3232)
            }
        }

        updateData()
    }

    fun openOrderActivity() {
        activity?.apply {
            startActivityForResult(Intent(this, MarketOrderActivity::class.java), 3232)
        }
    }

    fun updateData() {
        presenter.getAllAddress()
    }

    override fun updateAddressView(list: ArrayList<AddressResponse.AddressResponseData>) {
        val defAddress = Hawk.get<AddressResponse.AddressResponseData>("DEF_ADDRESS")
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

    private fun updateAddressLayout() {
        add_address.visibility = View.GONE

        val defAddress = Hawk.get<AddressResponse.AddressResponseData>("DEF_ADDRESS")
        layout_address.removeAllViews()

        val view = LayoutInflater.from(context).inflate(R.layout.view_address, null)
        if (defAddress != null) {
            view.text_name.text = defAddress.name
            view.text_tel.text = defAddress.tel
            view.text_address.text = defAddress.address

            view.tag = defAddress
            layout_address.addView(view)
        } else {
            view.text_address.text = "ไม่มีที่อยู่จัดส่ง"

            layout_address.addView(view)
        }

        view.setOnClickListener {
            if (isGuest()) {
                Toast.makeText(it.context, "โปรดเข้าสู่ระบบก่อนทำรายการ", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            activity?.apply {
                startActivityForResult(Intent(this, MarketAddressListActivity::class.java), 3310)
            }
        }
    }

    override fun updateSuccess() {
        updateData()
    }

    override fun updateError() {

    }

    private fun checkUserType() {
        val user = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        when {
            user.authority_info?.IsFb == "2" -> {
                //Guest
                val name = "Guest"
                text_user.text = name
            }
            user.authority_info?.IsFb == "1" -> {
                val accessToken = AccessToken.getCurrentAccessToken()
                val request = GraphRequest.newMeRequest(accessToken) { json, _ ->
                    // Application code
                    try {
                        val picture = json.getJSONObject("picture").getJSONObject("data").getString("url")
                        val name = json.getString("name")
                        text_user.text = name
                        img_user.loadCircle(picture, R.mipmap.market_icon_my)
                    } catch (exception: Exception) {
                        val name = user.authority_info?.FName + " " + user.authority_info?.LName
                        text_user.text = name
                    }
                }
                val parameters = Bundle()
                parameters.putString("fields", "id,name,email,gender,birthday,picture.type(large)")
                request.parameters = parameters
                request.executeAsync()
            }
            user.authority_info?.IsFb == "3" -> {
                val name = user.authority_info?.FName + " " + user.authority_info?.LName
                text_user.text = name
                context?.let {
                    val acct = Hawk.get<GAccount>("G_ACCOUNT")
                    img_user.loadCircle(acct?.photoUrl, R.mipmap.market_icon_my)
                }
            }
            else -> {
                val name = user.authority_info?.FName + " " + user.authority_info?.LName
                text_user.text = name
                presenter.getCitizenInfo()
            }
        }
    }

    override fun updateCitizenInfo(data: CitizenInfoResponse.CitizenInfoResponseData) {
        val picture = AppUtils.getImageUrl(data.CitizenImg)
        img_user.loadCircle(picture, R.mipmap.market_icon_my)

        val name = data.FName + " " + data.LName
        text_user.text = name
    }

    private fun isGuest(): Boolean {
        val user = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        return user.authority_info?.IsFb == "2"
    }
}