package com.transcode.smartcity101p2.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.GsonBuilder
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.ComplainDetailActivity
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.adapter.ComplainListAdapter
import com.transcode.smartcity101p2.contract.ComplainFragmentContract
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.model.complain.ComplainListResponse
import com.transcode.smartcity101p2.presenter.ComplainFragmentPresenter
import kotlinx.android.synthetic.main.fragment_complain.*

class ComplainFragment : CoreFragment(), ComplainFragmentContract.View, ComplainListAdapter.ClickItem {
    override fun onclickItem(res: ComplainListResponse.ComplainListResponseData) {
        context?.apply {
            val intent = Intent(this, ComplainDetailActivity::class.java)
            val gson = GsonBuilder().create()
            val jsonData = gson.toJson(res)
            intent.putExtra("data", jsonData)
            intent.putExtra("open_chat", open_chat)
            startActivity(intent)
            open_chat = false
        }
    }

    override fun updateComplainList(list: ArrayList<ComplainListResponse.ComplainListResponseData>) {
        adapter.setData(list)
        adapter.notifyDataSetChanged()

        if (open_id.isNotEmpty()) {
            openID(open_id, open_chat)
        }
    }

    companion object {
        fun newInstance(mode: Int, title: String): ComplainFragment {
            return ComplainFragment().apply {
                val bundle = Bundle()
                bundle.putInt("MODE", mode)
                bundle.putString("TITLE", title)
                arguments = bundle
            }
        }
    }

    lateinit var presenter: ComplainFragmentPresenter
    lateinit var adapter: ComplainListAdapter
    var open_id = ""
    var open_chat = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_complain, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        presenter = ComplainFragmentPresenter(this)

        context?.let {
            adapter = ComplainListAdapter(it)
            adapter.setRecyclerView(recyclerview)
            adapter.setClickListener(this)
        }

        fetch()
    }

    fun backPress() {
        activity?.apply {
            finish()
        }
    }

    fun fetch() {
        val account = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val citizen_id = account.authority_info?.CitizenId ?: "0"
        presenter.getComplainList(citizen_id)
    }

    fun openID(complain_id: String, open_chat: Boolean) {
        this.open_chat = open_chat
        if (adapter.list.size > 0) {
            open_id = ""
            for (data in adapter.list) {
                if (data.complain_id == complain_id) {
                    onclickItem(data)
                    break
                }
            }
        } else {
            open_id = complain_id
        }
    }
}