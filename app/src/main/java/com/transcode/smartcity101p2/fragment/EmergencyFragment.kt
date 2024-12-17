package com.transcode.smartcity101p2.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.GsonBuilder
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.EmergencyDetailActivity
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.adapter.EmergencyListAdapter
import com.transcode.smartcity101p2.contract.EmergencyFragmentContract
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.model.emergency.EmergencyListResponse
import com.transcode.smartcity101p2.presenter.EmergencyFragmentPresenter
import kotlinx.android.synthetic.main.fragment_emergency.*

class EmergencyFragment : CoreFragment(), EmergencyFragmentContract.View, EmergencyListAdapter.ClickItem {
    override fun onclickItem(res: EmergencyListResponse.EmergencyListResponseData) {
        context?.apply {
            val intent = Intent(this, EmergencyDetailActivity::class.java)
            val gson = GsonBuilder().create()
            val jsonData = gson.toJson(res)
            intent.putExtra("data", jsonData)
            intent.putExtra("open_chat", open_chat)
            startActivity(intent)
            open_chat = false
        }
    }

    override fun updateEmergencyList(list: ArrayList<EmergencyListResponse.EmergencyListResponseData>) {
        adapter.setData(list)
        adapter.notifyDataSetChanged()

        if (open_id.isNotEmpty()) {
            openID(open_id, open_chat)
        }
    }

    companion object {
        fun newInstance(mode: Int, title: String): EmergencyFragment {
            return EmergencyFragment().apply {
                val bundle = Bundle()
                bundle.putInt("MODE", mode)
                bundle.putString("TITLE", title)
                arguments = bundle
            }
        }
    }

    lateinit var presenter: EmergencyFragmentPresenter
    lateinit var adapter: EmergencyListAdapter
    var open_id = ""
    var open_chat = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_emergency, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {

        presenter = EmergencyFragmentPresenter(this)

        context?.let {
            adapter = EmergencyListAdapter(it)
            adapter.setRecyclerView(recyclerview)
            adapter.setClickListener(this)
        }

        fetch()
    }

    fun fetch() {
        val account = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val citizen_id = account.authority_info?.CitizenId ?: "0"
        presenter.getEmergencyList(citizen_id)
    }

    fun openID(emer_id: String, open_chat: Boolean) {
        this.open_chat = open_chat
        if (adapter.list.size > 0) {
            open_id = ""
            for (data in adapter.list) {
                if (data.emer_id == emer_id) {
                    onclickItem(data)
                    break
                }
            }
        } else {
            open_id = emer_id
        }
    }
}