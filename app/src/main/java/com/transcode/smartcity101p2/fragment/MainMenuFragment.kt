package com.transcode.smartcity101p2.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.Profile
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.*
import com.transcode.smartcity101p2.contract.MainMenuFragmentContract
import com.transcode.smartcity101p2.presenter.MainMenuFragmentPresenter
import kotlinx.android.synthetic.main.fragment_main_menu.*
import java.lang.Exception
import java.util.*
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.transcode.smartcity101p2.dialog.LanguageDialog
import com.transcode.smartcity101p2.extension.loadCircle
import com.transcode.smartcity101p2.model.*
import kotlinx.android.synthetic.main.dialog_lang.view.*


class MainMenuFragment : CoreFragment(), MainMenuFragmentContract.View {
    override fun showError(message: String) {
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        fun newInstance(): MainMenuFragment {
            return MainMenuFragment().apply {
                val bundle = Bundle()
                arguments = bundle
            }
        }
    }

    lateinit var presenter: MainMenuFragmentPresenter
    val LOGIN_MODE = "login_mode"
    var languageDialog: LanguageDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        presenter = MainMenuFragmentPresenter(this)

        menu_register.setOnClickListener {
            replaceFragment(RegisterFragment.newInstance(isFBUser = false), R.id.content_frame)
        }

        menu_login.setOnClickListener {
            replaceFragment(LoginFragment.newInstance(), R.id.content_frame)
        }
        menu_setting_lang.setOnClickListener {
            setAppLanguage()
        }

        login_button.setReadPermissions(Arrays.asList("public_profile", "email"))

        menu_guest.setOnClickListener {
            Hawk.put(LOGIN_MODE, "2")
            context?.let {
                presenter.requestLoginGuest(CoreApplication.getUDID(it))
            }
        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        val mGoogleSignInClient = activity?.let {
            GoogleSignIn.getClient(it, gso)
        }

        mGoogleSignInClient?.signOut()?.addOnCompleteListener { }

        sign_in_button.setOnClickListener {
            Hawk.put(LOGIN_MODE, "3")
            signInG()
        }

        val res = Hawk.get<CityResponse>(Const.KEY_CITY)
        res?.let {
            Hawk.delete(Const.KEY_CITY)

            if (Hawk.get<String>(LOGIN_MODE) == "1") {
                val accessToken = AccessToken.getCurrentAccessToken()
                accessToken?.apply {
                    val request = GraphRequest.newMeRequest(accessToken) { json, _ ->
                        // Application code
                        val fb_id = json.getString("id")
                        val fname = try {
                            json.getString("first_name")
                        } catch (e: Exception) {
                            ""
                        }
                        val lname = try {
                            json.getString("last_name")
                        } catch (e: Exception) {
                            ""
                        }
                        val email = try {
                            json.getString("email")
                        } catch (e: Exception) {
                            ""
                        }
                        presenter.updateCityAccount(email, fname, lname, fb_id, it.Cityid ?: "")
                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "id,name,first_name,last_name,email,gender,birthday")
                    request.parameters = parameters
                    request.executeAsync()
                }
            } else if (Hawk.get<String>(LOGIN_MODE) == "3") {
                val city_id = it.Cityid ?: ""
                gaccount?.let { ga ->
                    presenter.updateCityAccount(ga.email ?: "", ga.givenName ?: "", ga.familyName
                            ?: "", ga.id ?: "", city_id)
                }
            } else {
                val city_id = it.Cityid ?: ""
                context?.let {
                    presenter.updateCityAccount("", "", "", CoreApplication.getUDID(it), city_id)
                }
            }
        }

        val login = Hawk.get<Boolean>(Const.KEY_NEED_LOGIN)
        login?.let {
            Hawk.delete(Const.KEY_NEED_LOGIN)
            menu_login.performClick()
        }

        term_and_con_text.setOnClickListener {
            //            replaceFragment(PolicyFragment.newInstance(), R.id.content_frame)
            context?.let {
                val prefix = when {
                    CoreApplication.getLanguage(it).equals("th", true) -> "/policy/th"
                    CoreApplication.getLanguage(it).equals("en", true) -> "/policy/eng"
                    else -> "/policy/chi"
                }

                val market_url = BuildConfig.MARKETS_URL
                val url = "$market_url/$prefix"
                val webIntent = Intent(Intent.ACTION_VIEW)
                webIntent.data = Uri.parse(url)
                it.startActivity(webIntent)
            }
        }

        context?.let {
            when {
                CoreApplication.getLanguage(it).equals("th", true) -> button_change_lang.loadCircle(BitmapFactory.decodeResource(it.resources, R.mipmap.icon_l_th))
                CoreApplication.getLanguage(it).equals("en", true) -> button_change_lang.loadCircle(BitmapFactory.decodeResource(it.resources, R.mipmap.icon_l_en))
                else -> button_change_lang.loadCircle(BitmapFactory.decodeResource(it.resources, R.mipmap.icon_l_ch))
            }
        }

        button_change_lang.setOnClickListener {
            showSelectLangDialog()
        }

        val tv = sign_in_button.getChildAt(0)
        if (tv is TextView) {
            tv.text = getString(R.string.login_login_text)
        }

    }

    var gaccount: GoogleSignInAccount? = null
    fun handleSignInGResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            gaccount = completedTask.getResult(ApiException::class.java)
            val ga = GAccount()
            ga.email = gaccount?.email ?: ""
            ga.photoUrl = gaccount?.photoUrl.toString()
            Hawk.put("G_ACCOUNT", ga)
            presenter.requestLoginGoogle(gaccount?.id ?: "",
                    gaccount?.email ?: "",
                    gaccount?.givenName ?: "",
                    gaccount?.familyName ?: "")
        } catch (e: ApiException) {
        }
    }

    private fun signInG() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        val mGoogleSignInClient = activity?.let {
            GoogleSignIn.getClient(it, gso)
        }
        val signInIntent = mGoogleSignInClient?.signInIntent
        activity?.startActivityForResult(signInIntent, Const.RC_SIGN_IN)
    }

    fun loginWithFacebook() {
        val accessToken = AccessToken.getCurrentAccessToken()
        val request = GraphRequest.newMeRequest(accessToken) { json, _ ->
            // Application code
            val first_name = try {
                json.getString("first_name")
            } catch (e: Exception) {
                ""
            }
            val last_name = try {
                json.getString("last_name")
            } catch (e: Exception) {
                ""
            }
            val email = try {
                json.getString("email")
            } catch (e: Exception) {
                ""
            }
            presenter.requestLoginFacebook(email, first_name, last_name)
        }
        val parameters = Bundle()
        parameters.putString("fields", "id,name,first_name,last_name,email,gender,birthday")
        request.parameters = parameters
        request.executeAsync()
    }

    private fun setAppLanguage() {
        context?.let {
            val lang = if (CoreApplication.getLanguage(it) == PREF_LANG_TH) {
                PREF_LANG_EN
            } else {
                PREF_LANG_TH
            }
            CoreApplication.setLanguage(it, lang)
            val refresh = Intent(activity, SplashActivity::class.java)
            refresh.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            it.startActivity(refresh)
            activity?.finishAffinity()
        }
    }

    override fun onLoginSuccess(loginResponse: LoginResponse) {
        if (!loginResponse.authority_info?.CityId.isNullOrEmpty()) {
            context?.let {
                CoreApplication.saveLoginData(loginResponse)
                if (loginResponse.authority_info?.IsFb == "2") {
                    startApp()
                } else {
                    if (!loginResponse.authority_info?.Email.isNullOrEmpty() && loginResponse.authority_info?.Email != "undefined") {
                        presenter.getCitizenInfo()
                    } else {
                        replaceFragment(SelectPlaceFragment.newInstance(), R.id.content_frame)
                    }
                }
            }
        } else {
            replaceFragment(SelectPlaceFragment.newInstance(), R.id.content_frame)
        }
    }

    override fun onLoginFail(message: String) {
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onUpdateDataComplete() {
        if (Hawk.get<String>(LOGIN_MODE) == "1") {
            loginWithFacebook()
        } else if (Hawk.get<String>(LOGIN_MODE) == "3") {
            presenter.requestLoginGoogle(gaccount?.id ?: "",
                    gaccount?.email ?: "",
                    gaccount?.givenName ?: "",
                    gaccount?.familyName ?: "")
        } else {
            context?.let {
                presenter.requestLoginGuest(CoreApplication.getUDID(it))
            }
        }
    }

    override fun onGetCitizenInfoSuccess(data: CitizenInfoResponse.CitizenInfoResponseData) {
        data.FName?.let {
            startApp()
        } ?: kotlin.run {
            if (Hawk.get<String>(LOGIN_MODE) == "1") {
                val accessToken = AccessToken.getCurrentAccessToken()
                val request = GraphRequest.newMeRequest(accessToken) { json, _ ->
                    // Application code
                    val emailString = json.getString("email")
                    val profile = Profile.getCurrentProfile()
                    val name = profile.firstName + " " + profile.lastName

                    val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
                    var citizen_id = loginResponse.authority_info?.CitizenId ?: ""
                    val token = loginResponse.authority_info?.getAllToken() ?: ""

                    presenter.editAccountInfo("1234567890123", citizen_id, profile.firstName, profile.lastName,
                            "", "0", "0", "0", "", "", token)
                }
                val parameters = Bundle()
                parameters.putString("fields", "id,name,email,gender,birthday")
                request.parameters = parameters
                request.executeAsync()
            } else {
                gaccount?.let {
                    val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
                    var citizen_id = loginResponse.authority_info?.CitizenId ?: ""
                    val token = loginResponse.authority_info?.getAllToken() ?: ""
                    presenter.editAccountInfo("1234567890123", citizen_id, it.givenName
                            ?: "", it.familyName ?: "",
                            "", "0", "0", "0", "", "", token)
                }
            }
        }
    }

    private fun startApp() {
        context?.let {
            (it as Activity).finish()
            CoreApplication.openActivity(it, Intent(it, HomeActivity::class.java))
        }
    }

    private fun showSelectLangDialog() {
        context?.let {
            languageDialog?.dismiss()
            languageDialog = LanguageDialog(it)
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
                    CoreApplication.getLanguage(it).contains("th", true) -> view.bt_th_check.visibility = View.VISIBLE
                    CoreApplication.getLanguage(it).contains("en", true) -> view.bt_en_check.visibility = View.VISIBLE
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

                view.bt_done.setOnClickListener { button ->
                    languageDialog?.dismiss()
                    if (CoreApplication.getLanguage(button.context) != tmpLang) {
                        CoreApplication.setLanguage(button.context, tmpLang)
                        val intent = button.context.packageManager.getLaunchIntentForPackage(button.context.packageName)
                        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent?.apply {
                            startActivity(this)
                        }
                    }
                }
            }
        }
    }
}