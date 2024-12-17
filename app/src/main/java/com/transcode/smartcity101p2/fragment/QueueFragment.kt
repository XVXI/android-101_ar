package com.transcode.smartcity101p2.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.QueueDetailActivity
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.TicketDetailActivity
import com.transcode.smartcity101p2.adapter.AllQueueListAdapter
import com.transcode.smartcity101p2.contract.QueueFragmentContract
import com.transcode.smartcity101p2.dialog.LoadingDialog
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.model.MyQueueResponse
import com.transcode.smartcity101p2.model.QueueListResponse
import com.transcode.smartcity101p2.presenter.QueueFragmentPresenter
import com.transcode.smartcity101p2.services.ReminderManager
import com.transcode.smartcity101p2.view.CustomAppBarLayout
import kotlinx.android.synthetic.main.appbar_main.view.*
import kotlinx.android.synthetic.main.fragment_queue.*

class QueueFragment : CoreFragment(), QueueFragmentContract.View, AllQueueListAdapter.ClickItem {
    override fun setAlert(queue: MyQueueResponse) {
        context?.let {
            ReminderManager.INSTANCE.setAlert(queue, it)
        }
    }

    override fun onclickItem(res: Any) {
        when (res) {
            is QueueListResponse -> {
                activity?.apply {
                    val intent = Intent(this, QueueDetailActivity::class.java)
                    val gson = GsonBuilder().create()
                    val jsonData = gson.toJson(res)
                    intent.putExtra("data", jsonData)
                    startActivity(intent)
                }
            }
            is MyQueueResponse -> {
                activity?.apply {
                    val intent = Intent(this, TicketDetailActivity::class.java)
                    val gson = GsonBuilder().create()
                    val jsonData = gson.toJson(res)
                    intent.putExtra("data", jsonData)
                    startActivity(intent)
                }
            }
        }
    }

    companion object {
        fun newInstance(): QueueFragment {
            return QueueFragment().apply {
                val bundle = Bundle()
                arguments = bundle
            }
        }
    }

    lateinit var presenter: QueueFragmentPresenter
    lateinit var adapter: AllQueueListAdapter
    var loadingDialog: LoadingDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_queue, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        context?.let {
            adapter = AllQueueListAdapter(it)
            adapter.setRecyclerView(recyclerview)
            adapter.setClickListener(this)

            loadingDialog = LoadingDialog(it)
            loadingDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            loadingDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            loadingDialog?.setCancelable(false)
        }

        presenter = QueueFragmentPresenter(this)

        loadingDialog?.show()
        presenter.getQueueList()

        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        loginResponse?.let {
            val appBar = appbar as CustomAppBarLayout
//            appBar.setTitle(it.authority_info?.CityName.toString())
            appBar.setTitle("จองคิวบริการ")
            appBar.leftBt.visibility = View.VISIBLE
            appBar.iconLeft.visibility = View.INVISIBLE
            appBar.iconLeft.load(it.authority_info?.LogoUrl, R.mipmap.ic_launcher)
            appBar.leftBt.setOnClickListener {
                activity?.apply {
                    finish()
                }
            }
        }
    }

    override fun updateList(res: ArrayList<Any>) {
        loadingDialog?.dismiss()
        adapter.setData(res)
        adapter.notifyDataSetChanged()
    }

    override fun showError(message: String) {
        loadingDialog?.dismiss()
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_LONG).show()
        }
    }

    fun click() {
        loadingDialog?.show()
        presenter.getQueueList()
    }

    override fun onResume() {
        click()
        super.onResume()
    }
}