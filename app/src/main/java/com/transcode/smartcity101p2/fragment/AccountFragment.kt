package com.transcode.smartcity101p2.fragment

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.google.firebase.messaging.FirebaseMessaging
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.CoreApplication
import com.transcode.smartcity101p2.LoginActivity
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.adapter.AccountMenuAdapter
import com.transcode.smartcity101p2.contract.AccountFragmentContract
import com.transcode.smartcity101p2.dialog.SelectEditDialog
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.firebase.FirebaseMessagingManager
import com.transcode.smartcity101p2.model.CitizenInfoResponse
import com.transcode.smartcity101p2.model.CityFunctionResponse
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.presenter.AccountFragmentPresenter
import com.transcode.smartcity101p2.utils.AppUtils
import kotlinx.android.synthetic.main.fragment_account.*

class AccountFragment : CoreFragment(), AccountFragmentContract.View, AccountMenuAdapter.ClickItem {

    companion object {
        fun newInstance(): AccountFragment {
            return AccountFragment().apply {
                val bundle = Bundle()
                arguments = bundle
            }
        }
    }

    lateinit var presenter: AccountFragmentPresenter
    lateinit var adapter: AccountMenuAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        presenter = AccountFragmentPresenter(this)

        context?.let {
            adapter = AccountMenuAdapter(it)
            adapter.setRecyclerView(recyclerview)
            adapter.setClickListener(this)
        }

        presenter.getCityFunction()

        val user = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        when {
            user.authority_info?.IsFb == "2" -> {
                user_name.text = getString(R.string.guest_show_text)
                edit_account.visibility = View.INVISIBLE
            }
            user.authority_info?.IsFb == "1" -> {
                val name = user.authority_info?.FName.toString() + " " + user.authority_info?.LName.toString()
                user_name.text = name
                val accessToken = AccessToken.getCurrentAccessToken()
                val request = GraphRequest.newMeRequest(accessToken) { json, _ ->
                    // Application code
                    try {
                        val email = json.getString("email")
                        user_email.text = email.toString()
                    } catch (exception: Exception) {
                    }
                }
                val parameters = Bundle()
                parameters.putString("fields", "id,name,email,gender,birthday")
                request.parameters = parameters
                request.executeAsync()

                edit_account.setOnClickListener {
                    showEditDialog()
                }

                presenter.getCitizenInfo()
            }
            user.authority_info?.IsFb == "3" -> {
                val name = user.authority_info?.FName.toString() + " " + user.authority_info?.LName.toString()
                user_name.text = name
                user_email.text = user.authority_info?.Email

                edit_account.setOnClickListener {
                    showEditDialog()
                }

                presenter.getCitizenInfo()
            }
            else -> {
                val name = user.authority_info?.FName.toString() + " " + user.authority_info?.LName.toString()
                user_name.text = name
                user_email.text = user.authority_info?.Email

                edit_account.setOnClickListener {
                    showEditDialog()
                }

                presenter.getCitizenInfo()
            }
        }
    }

    private fun showEditDialog() {
        context?.let {
            val selectEditDialog = SelectEditDialog(it)
            selectEditDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            selectEditDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            selectEditDialog.show()
            selectEditDialog.setOnClickInfoListener(View.OnClickListener {
                fragmentManager?.let {
                    FragmentHelper.replace(it, EditAccountFragment.newInstance(), R.id.content_home_frame)
                }
                selectEditDialog.dismiss()
            })
            selectEditDialog.setOnClickPasswordListener(View.OnClickListener {
                fragmentManager?.let {
                    FragmentHelper.replace(it, ChangePasswordFragment.newInstance(), R.id.content_home_frame)
                }
                selectEditDialog.dismiss()
            })
        }
    }

    override fun updateList(dataList: ArrayList<CityFunctionResponse>) {
        adapter.setData(dataList)
        adapter.notifyDataSetChanged()

        val click_emergency = Hawk.get<String>("click_emergency") != null
        val click_complain = Hawk.get<String>("click_complain") != null

        if (click_complain) {
            Hawk.delete("click_complain")
            var canclick = false
            for (data in dataList) {
                if (data.FunctionId ?: "0" == "3") {
                    fragmentManager?.let {
//                        FragmentHelper.replace(it, ComplainFragment.newInstance(), R.id.content_home_frame)
                    }
                }
            }
        }

        if (click_emergency) {
            Hawk.delete("click_emergency")
            var canclick = false
            for (data in dataList) {
                if (data.FunctionId ?: "0" == "2") {
                    fragmentManager?.let {
//                        FragmentHelper.replace(it, EmergencyFragment.newInstance(), R.id.content_home_frame)
                    }
                }
            }
        }
    }

    override fun updateCitizenInfo(data: CitizenInfoResponse.CitizenInfoResponseData) {
        val urls = AppUtils.getImageUrl(data.CitizenImg)
        user_icon.load(urls, R.mipmap.man)
        val name = data.FName + " " + data.LName
        user_name.text = name
        if (!data.Email.isNullOrEmpty()) {
            user_email.text = data.Email
        }
    }

    override fun onclickItem(res: CityFunctionResponse, type: Int) {
        when (type) {
            AccountMenuAdapter.TYPE_LOGOUT -> {
                context?.let {
                    val mFirebaseMessaging = FirebaseMessaging.getInstance()
                    val login = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
                    val city_id_text = if (login.authority_info?.CityId.isNullOrEmpty()) {
                        ""
                    } else {
                        "_" + login.authority_info?.CityId
                    }
                    mFirebaseMessaging.unsubscribeFromTopic(FirebaseMessagingManager.KEY_FIREBASE_TOPIC + city_id_text)
                    mFirebaseMessaging.unsubscribeFromTopic(FirebaseMessagingManager.KEY_FIREBASE_TOPIC_NEWS + city_id_text)

                    val notificationIdList = if (Hawk.get<ArrayList<Int>>("noti_id_list") != null) {
                        Hawk.get<ArrayList<Int>>("noti_id_list")
                    } else {
                        arrayListOf()
                    }
                    Hawk.delete("noti_id_list")
                    val notificationManager = it.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    for (noti_id in notificationIdList) {
                        notificationManager.cancel(noti_id)
                    }

                    LoginManager.getInstance().logOut()
                    CoreApplication.deleteLoginData()
                    (it as Activity).finish()
                    CoreApplication.openActivity(it, Intent(it, LoginActivity::class.java))
                }
            }
            AccountMenuAdapter.TYPE_EDIT -> {

            }
            AccountMenuAdapter.TYPE_LINE -> {

            }
            AccountMenuAdapter.TYPE_NORMAL -> {
                val currentFragment = FragmentHelper.getCurrentFragment(fragmentManager, R.id.content_home_frame)
                when (res.FunctionId) {
                    "1" -> {
                        if (currentFragment is HomeFragment) {
                            currentFragment.moveToMenu(0)
                        }
                    }
                    "2" -> {
                        //แจ้งเหตุ
                        fragmentManager?.let {
//                            FragmentHelper.replace(it, EmergencyFragment.newInstance(), R.id.content_home_frame)
                        }
                    }
                    "3" -> {
                        //ร้องเรียน
                        fragmentManager?.let {
//                            FragmentHelper.replace(it, ComplainFragment.newInstance(), R.id.content_home_frame)
                        }
                    }
                    "4" -> {
                        if (currentFragment is HomeFragment) {
                            currentFragment.moveToMenu(1)
                        }
                    }
                    "13" -> {
                        fragmentManager?.let {
                            val login = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
                            val news_cat_id = login.authority_info?.ArticleNewsCatId.toString()
                            FragmentHelper.replace(it, NewsFragment.newInstance(res.FunctionName.toString(), news_cat_id), R.id.content_home_frame)
                        }
                    }
                    "14" -> {
                        fragmentManager?.let {
                            val login = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
                            val news_cat_id = login.authority_info?.AwardNewsCatId.toString()
                            FragmentHelper.replace(it, NewsFragment.newInstance(res.FunctionName.toString(), news_cat_id), R.id.content_home_frame)
                        }
                    }
                    "15" -> {
                        fragmentManager?.let {
                            FragmentHelper.replace(it, CallPhoneFragment.newInstance(res.FunctionName.toString()), R.id.content_home_frame)
                        }
                    }
                    "16" -> {
                        fragmentManager?.let {
                            FragmentHelper.replace(it, CctvListFragment.newInstance(res.FunctionName.toString()), R.id.content_home_frame)
                        }
                    }
                    "17" -> {
                        fragmentManager?.let {
                            val login = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
                            val youtube_list_id = login.authority_info?.YoutubeList.toString()
                            FragmentHelper.replace(it, YoutubePlayListFragment.newInstance(res.FunctionName.toString(), youtube_list_id), R.id.content_home_frame)
                        }
                    }
                }
            }
        }
    }

}