package com.transcode.smartcity101p2.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.adapter.CallPhoneAdapter
import com.transcode.smartcity101p2.contract.CallPhoneFragmentContract
import com.transcode.smartcity101p2.presenter.CallPhoneFragmentPresenter
import com.transcode.smartcity101p2.view.CustomAppBarLayout
import kotlinx.android.synthetic.main.appbar_main.view.*
import kotlinx.android.synthetic.main.fragment_callphone.*
import android.content.Intent
import android.net.Uri
import android.view.Window
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.adapter.PhonebookExpandableListAdapter
import com.transcode.smartcity101p2.dialog.CallPhoneDialog
import com.transcode.smartcity101p2.dialog.LoadingDialog
import com.transcode.smartcity101p2.model.CallPhoneResponse
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse

class CallPhoneFragment : CoreFragment(), CallPhoneFragmentContract.View, CallPhoneAdapter.ClickItem, PhonebookExpandableListAdapter.PhoneBookListAdapterListener {
    override fun onPhoneBookListClick(phonebookItem: CallPhoneResponse.CallPhoneData) {
        val phone = phonebookItem.Callnumber.toString()
        val title = phonebookItem.Callname.toString()
        showCallDialog(title, phone)
    }

    override fun onclickItem(res: CallPhoneResponse.CallPhoneData) {
        val phone = res.Callnumber.toString()
        val title = res.Callname.toString()
        showCallDialog(title, phone)
    }

    override fun updateList(list: ArrayList<CallPhoneResponse.CallPhoneData>) {
        loadingDialog?.dismiss()
        if (list.size > 0) {
            recyclerview.visibility = View.VISIBLE
            text_empty.visibility = View.GONE

//            adapter.setData(list)
//            adapter.notifyDataSetChanged()

            val header = arrayListOf<String>()
            val allHeader = arrayListOf<HashMap<String, String>>()
            val allChild = hashMapOf<String, ArrayList<CallPhoneResponse.CallPhoneData>>()

            for (data in list) {
                var inList = false
                for (charTitle in header) {
                    if ((data.Callname ?: "").startsWith(charTitle)) {
                        inList = true
                        break
                    }
                }
                if (!inList) {
                    val s = (data.Callname ?: "")[0].toString()
                    header.add(s)

                    val h = hashMapOf<String, String>()
                    h["title"] = s
                    allHeader.add(h)
                }
            }

            for (charTitle in header) {
                val childList = arrayListOf<CallPhoneResponse.CallPhoneData>()
                for (data in list) {
                    if ((data.Callname ?: "").startsWith(charTitle)) {
                        childList.add(data)
                    }
                }
                allChild.put(charTitle, childList)
            }

            adapter.setData(allHeader)
            adapter.setChildData(allChild)
            adapter.notifyDataSetChanged()

            for (i in 0 until adapter.groupCount) {
                recyclerview.expandGroup(i)
            }
        } else {
            recyclerview.visibility = View.GONE
            text_empty.visibility = View.VISIBLE
        }
    }

    companion object {
        fun newInstance(header_title: String): CallPhoneFragment {
            return CallPhoneFragment().apply {
                val bundle = Bundle()
                bundle.putString("header_title", header_title)
                arguments = bundle
            }
        }
    }

    lateinit var presenter: CallPhoneFragmentPresenter
    //    lateinit var adapter: CallPhoneAdapter
    lateinit var adapter: PhonebookExpandableListAdapter
    var loadingDialog: LoadingDialog? = null
    var callPhoneDialog: CallPhoneDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_callphone, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        val appBar = appbar as CustomAppBarLayout
        appBar.setTitle(arguments?.getString("header_title") ?: getString(R.string.home_hotline))
        appBar.leftBt.setOnClickListener {
            //            fragmentManager?.popBackStack()
            backPress()
        }

        context?.let {
            adapter = PhonebookExpandableListAdapter(it)
            adapter.setView(recyclerview)
            adapter.setChildListener(this)

            loadingDialog = LoadingDialog(it)
            loadingDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            loadingDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            loadingDialog?.setCancelable(false)
        }

        presenter = CallPhoneFragmentPresenter(this)

        val login = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val city_id = login.authority_info?.CityId ?: ""
        val token = login.authority_info?.getAllToken() ?: ""

        loadingDialog?.show()
        presenter.getCallPhoneNumber(city_id, token)
    }

    private fun showCallDialog(title: String, phone: String) {
        context?.let {
            val con = it
            callPhoneDialog = CallPhoneDialog(it)
            callPhoneDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            callPhoneDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            callPhoneDialog?.show()
            callPhoneDialog?.setTitle(getString(R.string.callphone_dialog_header))
            callPhoneDialog?.setMessage("$title $phone")
            callPhoneDialog?.setOnClickOKListener(View.OnClickListener {
                callPhoneDialog?.dismiss()
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                con.startActivity(intent)
            })
            callPhoneDialog?.setOnClickCancelListener(View.OnClickListener {
                callPhoneDialog?.dismiss()
            })
        }
    }

    fun backPress() {
        activity?.apply {
            finish()
        }
    }
}