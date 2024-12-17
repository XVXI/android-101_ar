package com.transcode.smartcity101p2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.contract.SettingActivityContract
import com.transcode.smartcity101p2.dialog.LanguageDialog
import com.transcode.smartcity101p2.dialog.LoadingDialog
import com.transcode.smartcity101p2.model.CitizenInfoResponse
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.GAccount
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.presenter.SettingActivityPresenter
import com.transcode.smartcity101p2.view.CustomAppBarLayout2
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.appbar_main2.view.*
import kotlinx.android.synthetic.main.dialog_lang.view.*

class SettingActivity : AppCompatActivity(), SettingActivityContract.View {
    override fun updateCitizenInfo(data: CitizenInfoResponse.CitizenInfoResponseData) {
        loadingDialog?.dismiss()

        val tel = data.Tel ?: "-"
        val address = data.Address ?: "-"
        val province = data.ProvinceName ?: ""
        val zip = data.ZipCode ?: ""
        pf_name.text = data.FName + " " + data.LName
        pf_id.text = getString(R.string.account_text_c_code) + data.IdCard
        pf_tel.text = getString(R.string.account_text_tel) + tel
        pf_address1.text = getString(R.string.account_text_address) + address
        pf_address2.text = "$province $zip"

        Hawk.put("user_image", data.CitizenImg.toString())
    }

    override fun onFail() {
        loadingDialog?.dismiss()
    }

    lateinit var presenter: SettingActivityPresenter
    var languageDialog: LanguageDialog? = null
    var loadingDialog: LoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val appBar = appbar as CustomAppBarLayout2
        appBar.setTitle(getString(R.string.account_text_setting))
        appBar.setHeaderBackground(R.drawable.bg_gradient_purple)
        appBar.leftBt.setOnClickListener {
            finish()
        }

        presenter = SettingActivityPresenter(this)

        account_info.setOnClickListener {
            startActivity(Intent(this, EditAccountActivity::class.java))
        }

        password_button.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }

        lang_button.setOnClickListener {
            showSelectLangDialog()
        }

        lang_name.text = if (CoreApplication.getLanguage(this).equals("th", true)) {
            "ไทย"
        } else if (CoreApplication.getLanguage(this).equals("en", true)) {
            "English"
        } else {
            "中文语言"
        }

        button_stores.setOnClickListener {
            val intent = Intent(this, WebForm2Activity::class.java)
            startActivity(intent)
        }

        button_privacy.setOnClickListener {
            val prefix = when {
                CoreApplication.getLanguage(this).equals("th", true) -> "/policy/th"
                CoreApplication.getLanguage(this).equals("en", true) -> "/policy/eng"
                else -> "/policy/chi"
            }

            val market_url = BuildConfig.MARKETS_URL
            val url = "$market_url/$prefix"
            val webIntent = Intent(Intent.ACTION_VIEW)
            webIntent.data = Uri.parse(url)
            startActivity(webIntent)
        }

        loadingDialog = LoadingDialog(this)
        loadingDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        loadingDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        loadingDialog?.setCancelable(false)
    }

    override fun onResume() {
        checkUserType()
        super.onResume()
    }

    var sync = false

    private fun checkUserType() {
        val user = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        when {
            user.authority_info?.IsFb == "2" -> {
                //Guest
                pf_name.text = "Guest"
                pf_id.text = getString(R.string.account_text_c_code) + "-"
                pf_tel.text = getString(R.string.account_text_tel) + "-"
                pf_email.text = getString(R.string.account_text_email) + "-"
                pf_address1.text = getString(R.string.account_text_address) + "-"
                pf_address2.text = ""
                password_layout.visibility = View.GONE
            }
            user.authority_info?.IsFb == "1" -> {
                password_layout.visibility = View.GONE

//                pf_email.text = "Facebook"

                val fname = user.authority_info?.FName ?: ""
                val lname = user.authority_info?.LName ?: ""
                val name = "$fname $lname"
                pf_name.text = name
                val accessToken = AccessToken.getCurrentAccessToken()
                val request = GraphRequest.newMeRequest(accessToken) { json, _ ->
                    // Application code
                    try {
                        val picture = json.getJSONObject("picture").getJSONObject("data").getString("url")
                    } catch (exception: Exception) {
                    }
                    try {
                        val email = json.getString("email")
                        pf_email.text = email.toString()
                    } catch (exception: Exception) {
                    }
                }
                val parameters = Bundle()
                parameters.putString("fields", "id,name,email,gender,birthday,picture.type(large)")
                request.parameters = parameters
                if (!sync) {
                    sync = !sync
                    request.executeAsync()
                }

                presenter.getCitizenInfo()
            }
            user.authority_info?.IsFb == "3" -> {
                password_layout.visibility = View.GONE
                val acct = Hawk.get<GAccount>("G_ACCOUNT")

                val fname = user.authority_info?.FName ?: ""
                val lname = user.authority_info?.LName ?: ""
                val name = "$fname $lname"
                pf_name.text = name
                pf_email.text = acct?.email

                presenter.getCitizenInfo()
            }
            else -> {
                loadingDialog?.show()

                val fname = user.authority_info?.FName ?: ""
                val lname = user.authority_info?.LName ?: ""
                val email = user.authority_info?.Email ?: "-"
                pf_name.text = "$fname $lname"
                pf_id.text = getString(R.string.account_text_c_code) + "-"
                pf_email.text = getString(R.string.account_text_email) + email
                password_layout.visibility = View.VISIBLE

                presenter.getCitizenInfo()
            }
        }
    }

    private fun showSelectLangDialog() {
        languageDialog?.dismiss()
        languageDialog = LanguageDialog(this)
        languageDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        languageDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val window = languageDialog?.window
        val wlp = window?.attributes

        wlp?.gravity = Gravity.BOTTOM
        wlp?.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        window?.attributes = wlp

        languageDialog?.show()

        languageDialog?.getView()?.let { view ->
            var tmpLang = "en"

            view.bt_th_check.visibility = View.INVISIBLE
            view.bt_en_check.visibility = View.INVISIBLE
            view.bt_zh_check.visibility = View.INVISIBLE
            when {
                CoreApplication.getLanguage(this).contains("th", true) -> view.bt_th_check.visibility = View.VISIBLE
                CoreApplication.getLanguage(this).contains("en", true) -> view.bt_en_check.visibility = View.VISIBLE
                else -> view.bt_zh_check.visibility = View.VISIBLE
            }

            view.bt_th.setOnClickListener {
                tmpLang = "th"
                view.bt_en_check.visibility = View.INVISIBLE
                view.bt_zh_check.visibility = View.INVISIBLE

                view.bt_th_check.visibility = View.VISIBLE
            }

            view.bt_en.setOnClickListener {
                tmpLang = "en"
                view.bt_th_check.visibility = View.INVISIBLE
                view.bt_zh_check.visibility = View.INVISIBLE

                view.bt_en_check.visibility = View.VISIBLE
            }

            view.bt_zh.setOnClickListener {
                tmpLang = "zh"
                view.bt_th_check.visibility = View.INVISIBLE
                view.bt_en_check.visibility = View.INVISIBLE

                view.bt_zh_check.visibility = View.VISIBLE
            }

            view.bt_done.setOnClickListener {
                languageDialog?.dismiss()
                if (CoreApplication.getLanguage(this) != tmpLang) {
                    CoreApplication.setLanguage(this, tmpLang)
                    val intent = baseContext.packageManager.getLaunchIntentForPackage(baseContext.packageName)
                    intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent?.let {
                        startActivity(it)
                    }
                }
            }
        }
    }
}