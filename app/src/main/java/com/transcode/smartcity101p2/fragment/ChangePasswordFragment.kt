package com.transcode.smartcity101p2.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.contract.ChangePasswordFragmentContract
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.presenter.ChangePasswordFragmentPresenter
import com.transcode.smartcity101p2.view.CustomAppBarLayout
import kotlinx.android.synthetic.main.appbar_main.view.*
import kotlinx.android.synthetic.main.fragment_change_password.*

class ChangePasswordFragment : CoreFragment(), ChangePasswordFragmentContract.View {
    override fun changePasswordSuccess() {
        showError("เปลี่ยนรหัสผ่านสำเร็จ")
        fragmentManager?.popBackStack()
        FragmentHelper.remove(fragmentManager, this)
        activity?.apply {
            finish()
        }
    }

    override fun showError(message: String) {
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_LONG).show()
        }
        hideSoftKeyboard()
    }

    companion object {
        fun newInstance(): ChangePasswordFragment {
            return ChangePasswordFragment().apply {
                val bundle = Bundle()
                arguments = bundle
            }
        }
    }

    lateinit var presenter: ChangePasswordFragmentPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_change_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        val appBar = appbar as CustomAppBarLayout
        appBar.setTitle(getString(R.string.change_password_change))
        appBar.leftBt.setOnClickListener {
            fragmentManager?.popBackStack()
            FragmentHelper.remove(fragmentManager, this)
        }

        presenter = ChangePasswordFragmentPresenter(this)

        val account = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val emailtext = account.authority_info?.Email ?: "email"

        email_text.text = emailtext

        submit.setOnClickListener {
            checkPassword()
        }
    }

    private fun checkPassword() {
        if (new_password_text.text.toString().length <= 3) {
            showError("รหัสผ่านต้องมีขนาด 4 ตัวอักษรขึ้นไป")
            return
        }

        if (old_password_text.text.isEmpty()) {
            showError("โปรดกรอกรหัสผ่าน")
            return
        }

        if (new_password_text.text.isEmpty()) {
            showError("โปรดใส่รหัสผ่านที่ต้องการแก้ไข")
            return
        }
        if (new_password_text.text.toString() != c_password_text.text.toString()) {
            showError("รหัสผ่านไม่ตรงกัน")
            return
        }

        val account = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val token = account.authority_info?.getAllToken() ?: ""
        presenter.changePassword(email_text.text.toString(), old_password_text.text.toString(), new_password_text.text.toString(), token)
    }
}