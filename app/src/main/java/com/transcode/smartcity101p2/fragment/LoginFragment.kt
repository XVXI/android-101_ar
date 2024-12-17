package com.transcode.smartcity101p2.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.CoreApplication
import com.transcode.smartcity101p2.HomeActivity
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.view.CustomAppBarLayout2
import kotlinx.android.synthetic.main.appbar_main2.view.*
import kotlinx.android.synthetic.main.fragment_login.*
import com.transcode.smartcity101p2.contract.LoginFragmentContract
import com.transcode.smartcity101p2.model.CityResponse
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.presenter.LoginFragmentPresenter
import java.util.regex.Pattern


class LoginFragment : CoreFragment(), LoginFragmentContract.View {

    companion object {
        fun newInstance(): LoginFragment {
            return LoginFragment().apply {
                val bundle = Bundle()
                arguments = bundle
            }
        }
    }

    lateinit var presenter: LoginFragmentPresenter

    override fun showProgressBar() {
        progress?.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progress?.visibility = View.GONE
    }

    override fun onLoginSuccess(res: LoginResponse) {
        context?.let {
            CoreApplication.saveLoginData(res)
            (it as Activity).finish()
            CoreApplication.openActivity(it, Intent(it, HomeActivity::class.java))
        }
    }

    override fun onLoginFail(message: String) {
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter = LoginFragmentPresenter(this)
        initView()
    }

    private fun initView() {
        val appBar = appbar as CustomAppBarLayout2
        appBar.setTitle(getString(R.string.login_login_text))
        appBar.leftBt.setOnClickListener {
            fragmentManager?.popBackStack()
            FragmentHelper.remove(fragmentManager, this)
        }

        forgot.setOnClickListener {
            replaceFragment(ForgotFragment.newInstance(), R.id.content_frame)
        }

        submit.setOnClickListener {
            if (!isEmailValid(username.text.toString()) || username.text.toString().isEmpty()) {
                username.requestFocus()
                username.error = getString(R.string.login_err_text_fill_email)
                return@setOnClickListener
            } else if (password.text.toString().isEmpty()) {
                password.requestFocus()
                password.error = getString(R.string.login_err_text_fill_password)
                return@setOnClickListener
            }
            val h = hashMapOf<String, String>()
            h["user"] = username.text.toString()
            h["pass"] = password.text.toString()
            Hawk.put("userpass", h)
            replaceFragment(SelectPlaceFragment.newInstance(), R.id.content_frame)
        }

        val res = Hawk.get<CityResponse>(Const.KEY_CITY)
        res?.let {
            val city_id = res.Cityid ?: "6"
            val h = Hawk.get<HashMap<String, String>>("userpass")
            presenter.requestLogin(h["user"].toString(), h["pass"].toString(), city_id)
            Hawk.delete(Const.KEY_CITY)
            Hawk.delete("userpass")
        }

    }

    private fun isEmailValid(email: String): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }
}