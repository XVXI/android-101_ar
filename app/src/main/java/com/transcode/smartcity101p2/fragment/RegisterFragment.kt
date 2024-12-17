package com.transcode.smartcity101p2.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import com.transcode.smartcity101p2.view.CustomAppBarLayout2
import kotlinx.android.synthetic.main.appbar_main2.view.*
import com.facebook.login.LoginManager
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.BuildConfig
import com.transcode.smartcity101p2.CoreApplication
import com.transcode.smartcity101p2.HomeActivity
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.contract.RegisterFragmentContract
import com.transcode.smartcity101p2.dialog.CustomDialog
import com.transcode.smartcity101p2.dialog.LoadingDialog
import com.transcode.smartcity101p2.model.CityResponse
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.presenter.RegisterFragmentPresenter
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.custom_dialog.view.*
import java.util.regex.Pattern


class RegisterFragment : CoreFragment(), RegisterFragmentContract.View {
    override fun onLoginFail(message: String) {
        hideLoading()
        register_button.isEnabled = true
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        fun newInstance(isFBUser: Boolean): RegisterFragment {
            return RegisterFragment().apply {
                val bundle = Bundle()
                bundle.putBoolean("isFBUser", isFBUser)
                arguments = bundle
            }
        }
    }

    lateinit var presenter: RegisterFragmentPresenter
    lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        Hawk.put(Const.KEY_CITY, CityResponse().apply {
            Cityname = "เทศบาลเมืองร้อยเอ็ด"
            Cityid = "6"
        })

        context?.let {
            loadingDialog = LoadingDialog(it)
            loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            loadingDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            loadingDialog.setCancelable(false)
        }

        presenter = RegisterFragmentPresenter(this)
        val appBar = appbar as CustomAppBarLayout2
        appBar.setTitle(getString(R.string.register_register_text))
        appBar.leftBt.setOnClickListener {
            LoginManager.getInstance().logOut()
            fragmentManager?.popBackStack()
            FragmentHelper.remove(fragmentManager, this)
        }

        val isFBUser = arguments?.getBoolean("isFBUser") ?: false

        if (isFBUser) {
            presenter.getFacebookDetail()

            userDetailcontainer.visibility = View.GONE
            name.isEnabled = false
            lastname.isEnabled = false
            email.isEnabled = false
        }

        select_place.setOnClickListener {
            replaceFragment(SelectPlaceFragment.newInstance(), R.id.content_frame)
        }

        val res = Hawk.get<CityResponse>(Const.KEY_CITY)
        res?.let {
            Hawk.delete(Const.KEY_CITY)
            select_place.text = it.Cityname
            select_place.tag = res
        }

        register_button.setOnClickListener {
            showLoadingDialog()
            register_button.isEnabled = false
            if (checkFillForm(name.text.toString(), lastname.text.toString(), email.text.toString(),
                            select_place.tag, password.text.toString(), repassword.text.toString(),
                            term_and_con_checkbox.isChecked)) {
                return@setOnClickListener
            }
            presenter.checkRegisterState(isFBUser, name.text.toString(), lastname.text.toString(), email.text.toString(),
                    select_place.tag, password.text.toString(), repassword.text.toString(),
                    term_and_con_checkbox.isChecked)
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

        val filter = InputFilter { source, start, end, dest, dstart, dend ->
            for (i in start until end) {
                if (!isTextFilter(source[i])) {
                    return@InputFilter ""
                }
            }
            null
        }
        password.filters = arrayOf(filter)
        repassword.filters = arrayOf(filter)
    }

    private fun isTextFilter(c: Char): Boolean {
        val l = "abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ 1234567890"
        for (ch in l) {
            if (c == ch) {
                return true
            }
        }
        return false
    }

    override fun updateFBRegister(firstName: String, lastName: String, emailString: String) {
        name?.setText(firstName)
        lastname?.setText(lastName)
        email?.setText(emailString)
    }

    override fun showError(message: String) {
        hideLoading()
        register_button.isEnabled = true
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onLoginSuccess(res: LoginResponse) {
        hideLoading()
        register_button.isEnabled = true
        context?.let {
            CoreApplication.saveLoginData(res)
            (it as Activity).finish()
            CoreApplication.openActivity(it, Intent(it, HomeActivity::class.java))
        }
    }

    private fun checkFillForm(nametext: String, lastnametext: String, emailtext: String,
                              select_place: Any?, passwordtext: String, repasswordtext: String,
                              term_and_con_checkbox: Boolean): Boolean {
        val notfill = nametext.isEmpty() || lastnametext.isEmpty() || emailtext.isEmpty() || (select_place == null) || passwordtext.isEmpty() || repasswordtext.isEmpty() || !term_and_con_checkbox || (passwordtext.length <= 3)
        val checkpassword = (!passwordtext.isEmpty() && !repasswordtext.isEmpty()) && (passwordtext == repasswordtext)

        if (select_place == null) {
            showError(getString(R.string.register_err_city_text))
        }

        if (passwordtext.length <= 3) {
            showError(getString(R.string.register_err_pass_l_text))
        }

        if (!checkpassword) {
            showError(getString(R.string.register_err_pass_m_text))
        }

        if (nametext.isEmpty()) {
            name.error = getString(R.string.register_err_name_text)
        }

        if (lastnametext.isEmpty()) {
            lastname.error = getString(R.string.register_err_lname_text)
        }

        if (!isEmailValid(emailtext) || emailtext.isEmpty()) {
            email.error = getString(R.string.register_err_email_text)
        }

        if (passwordtext.isEmpty()) {
            password.error = getString(R.string.register_err_pass_text)
        }

        if (repasswordtext.isEmpty()) {
            repassword.error = getString(R.string.register_err_pass_text)
        }

        if (!term_and_con_checkbox) {
            showError(getString(R.string.register_err_accept_text))
        }

        return notfill
    }

    private fun isEmailValid(email: String): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    private fun showLoadingDialog() {
        loadingDialog.show()
    }

    private fun hideLoading() {
        loadingDialog.dismiss()
    }

    override fun registerSuccess() {
        hideLoading()
        showPopup()
    }

    var customDialog: CustomDialog? = null

    private fun showPopup() {
        context?.let {
            customDialog = CustomDialog(it)
            customDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            customDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            customDialog?.show()
            customDialog?.setTitle(getString(R.string.register_register_text))
            customDialog?.setMessage(getString(R.string.register_success))
            customDialog?.setOnClickOKListener(View.OnClickListener {
                val appBar = appbar as CustomAppBarLayout2
                appBar.leftBt.performClick()
                customDialog?.dismiss()
            })

            customDialog?.b?.let { view ->
                view.dl_cancel.visibility = View.GONE
                view.dl_center.visibility = View.GONE
            }

            customDialog?.setOnClickCancelListener(View.OnClickListener { customDialog?.dismiss() })
        }
    }
}