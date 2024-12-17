package com.transcode.smartcity101p2.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.contract.ForgotFragmentContract
import com.transcode.smartcity101p2.presenter.ForgotFragmentPresenter
import com.transcode.smartcity101p2.view.CustomAppBarLayout2
import kotlinx.android.synthetic.main.appbar_main2.view.*
import kotlinx.android.synthetic.main.fragment_forgot.*
import java.util.regex.Pattern

class ForgotFragment : CoreFragment(), ForgotFragmentContract.View {

    companion object {
        fun newInstance(): ForgotFragment {
            return ForgotFragment().apply {
                val bundle = Bundle()
                arguments = bundle
            }
        }
    }

    lateinit var presenter: ForgotFragmentPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_forgot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        val appBar = appbar as CustomAppBarLayout2
        appBar.setTitle(getString(R.string.forgot_forgotpassword_text))
        appBar.leftBt.setOnClickListener {
            fragmentManager?.popBackStack()
            FragmentHelper.remove(fragmentManager, this)
        }

        presenter = ForgotFragmentPresenter(this)

        submit.setOnClickListener {
            if(!isEmailValid(email.text.toString())){
                email.requestFocus()
                email.error = getString(R.string.forgot_err_text_fill_email)
                return@setOnClickListener
            }
            presenter.forgotPassword(email.text.toString())
        }
    }

    override fun updateView() {
        fill_form.visibility = View.GONE
        text_form.visibility = View.VISIBLE
    }

    override fun showError(message: String) {
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun isEmailValid(email: String): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

}