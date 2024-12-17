package com.transcode.smartcity101p2.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ExpandableListView
import android.widget.Toast
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.WebFormActivity
import com.transcode.smartcity101p2.adapter.CustomExpandableListAdapter
import com.transcode.smartcity101p2.adapter.DateListAdapter
import com.transcode.smartcity101p2.contract.SelectDateFragmentContract
import com.transcode.smartcity101p2.dialog.CustomDialog
import com.transcode.smartcity101p2.dialog.LoadingDialog
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.QueueSlotResponse
import com.transcode.smartcity101p2.presenter.SelectDateFragmentPresenter
import com.transcode.smartcity101p2.utils.AppUtils
import com.transcode.smartcity101p2.view.CustomAppBarLayout
import kotlinx.android.synthetic.main.appbar_main.view.*
import kotlinx.android.synthetic.main.fragment_select_date.*

class SelectDateFragment : CoreFragment(), SelectDateFragmentContract.View, DateListAdapter.ClickItem, CustomExpandableListAdapter.ChildClickListener {
    override fun editChooseDatetimeSuccess() {
        showError("เปลี่ยนเวลาที่จองคิวสำเร็จ")
        activity?.apply {
            setResult(RESULT_OK)
            finish()
        }
    }

    override fun onChildClick(groupPosition: Int, data: QueueSlotResponse.QueueSlotData) {
        context?.let {
            val d = adapter.getGroup(groupPosition) as HashMap<String, Any>
            val day = AppUtils.getDateString("EEE " + AppUtils.formateDate3, d["date"] as Long)

            var stime = data.QueueFromTime ?: "00:00:00"

            var etime = data.QueueToTime ?: "00:00:00"

            date_string = AppUtils.getDateString(AppUtils.formateDate3, d["date"] as Long) + " " + stime
            date_string2 = AppUtils.getDateString(AppUtils.formateDate1, d["date"] as Long) + " " + stime
            queue_slot_id = data.QueueSlotId ?: ""
            end_time = etime
        }
    }

    override fun getQueueSlotSuccess(list: ArrayList<QueueSlotResponse.QueueSlotData>, groupPosition: Int) {
        loadingDialog?.dismiss()
        if (list.size > 0) {
            adapter.updateChild(groupPosition, list)
        } else {
            showError(Const.MESSAGE_NO_QUEUE_SLOT)
        }
    }

    override fun onclickItem(res: HashMap<String, Any>) {
        selectDate = res["date"] as Long
    }

    companion object {
        fun newInstance(queue_id: String, queue_type: String, form_url: String, base_lat: String, base_lng: String, queue_title: String): SelectDateFragment {
            return SelectDateFragment().apply {
                val bundle = Bundle()
                bundle.putString("queue_id", queue_id)
                bundle.putString("queue_type", queue_type)
                bundle.putString("form_url", form_url)
                bundle.putString("base_lat", base_lat)
                bundle.putString("base_lng", base_lng)
                bundle.putString("queue_title", queue_title)
                bundle.putBoolean("isEdit", false)
                arguments = bundle
            }
        }

        fun newInstance(queue_id: String, queue_checkin_id: String, queue_title: String): SelectDateFragment {
            return SelectDateFragment().apply {
                val bundle = Bundle()
                bundle.putString("queue_id", queue_id)
                bundle.putString("queue_checkin_id", queue_checkin_id)
                bundle.putString("queue_title", queue_title)
                bundle.putBoolean("isEdit", true)
                arguments = bundle
            }
        }
    }

    lateinit var presenter: SelectDateFragmentPresenter
    var loadingDialog: LoadingDialog? = null
    var customDialog: CustomDialog? = null
    lateinit var adapter: CustomExpandableListAdapter
    private var selectDate = 0L
    private var date_string = ""
    private var date_string2 = ""
    private var queue_slot_id = ""
    private var end_time = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_select_date, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        date_string = ""
        queue_slot_id = ""
        end_time = ""

        val appBar = appbar as CustomAppBarLayout
        appBar.setTitle("เลือกวัน")
        appBar.leftBt.setOnClickListener {
            activity?.apply {
                finish()
            }
        }
        selectDate = 0
        presenter = SelectDateFragmentPresenter(this)
        context?.let {
            loadingDialog = LoadingDialog(it)
            loadingDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            loadingDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            loadingDialog?.setCancelable(false)

            adapter = CustomExpandableListAdapter(it)
            adapter.setView(expandableListView)
            adapter.setChildListener(this)
            expandableListView.setOnGroupExpandListener(groupExpandListener)
        }

        presenter.calculateDayList(arguments?.getString("queue_type") ?: "2")
        val isEdit = arguments?.getBoolean("isEdit") ?: false
        submit.text = getString(R.string.text_edit_queue)

        submit.setOnClickListener {
            if (date_string == "" || queue_slot_id == "") {
                showError("โปรดเลือกวัน")
                return@setOnClickListener
            }

            if (isEdit) {
                showEditPopup()
            } else {
                goBookQueue()
            }
        }
    }

    private fun goBookQueue() {
        val queue_id = arguments?.getString("queue_id") ?: "0"
        val form_url = arguments?.getString("form_url") ?: ""
        val queue_type = arguments?.getString("queue_type") ?: "2"
        val base_lat = arguments?.getString("base_lat") ?: "0"
        val base_lng = arguments?.getString("base_lng") ?: "0"
        val queue_title = arguments?.getString("queue_title") ?: ""

        fragmentManager?.let {

            if (arguments?.getString("form_url") ?: "" == "") {

                if (queue_type == "2") {
                    FragmentHelper.replace(it, BookQueueFragment.newInstance(queue_type, queue_id, date_string, base_lat, base_lng, queue_slot_id, queue_title, end_time, ""), R.id.content_home_frame)
                } else {
                    FragmentHelper.replace(it, SelectLocationFragment.newInstance(queue_id, date_string, queue_slot_id, queue_title, end_time, ""), R.id.content_home_frame)
                }
            } else {
                activity?.apply {
                    val intent = Intent(this, WebFormActivity::class.java)
                    intent.putExtra("title", "Form")
                    intent.putExtra("url", form_url)
                    intent.putExtra("queue_type", queue_type)
                    intent.putExtra("queue_id", queue_id)
                    intent.putExtra("date_string", date_string)
                    intent.putExtra("queue_slot_id", queue_slot_id)
                    intent.putExtra("base_lat", base_lat)
                    intent.putExtra("base_lng", base_lng)
                    intent.putExtra("queue_title", queue_title)
                    intent.putExtra("endtime", end_time)
                    startActivityForResult(intent, Const.REQUEST_CODE_BOOK_QUEUE)
                }
            }

            for (i in 0 until adapter.groupCount) {
                expandableListView.collapseGroup(i)
            }
        }
    }

    private val groupExpandListener = ExpandableListView.OnGroupExpandListener {
        for (i in 0 until adapter.groupCount) {
            if (i != it) {
                expandableListView.collapseGroup(i)
            }
        }
        val queue_id = arguments?.getString("queue_id") ?: "0"
        val data = adapter.getGroup(it) as HashMap<String, Any>
        if (adapter.getAllChild(it).size <= 0) {
            loadingDialog?.show()
            presenter.getQueueSlot(it, queue_id, AppUtils.getDateString(AppUtils.formateDate1, data["date"] as Long))
        }
    }

    override fun updateView(list: ArrayList<HashMap<String, Any>>) {
        adapter.setData(list)
        adapter.notifyDataSetChanged()

        for (i in 0 until adapter.groupCount) {
            expandableListView.collapseGroup(i)
        }
    }

    override fun showError(message: String) {
        loadingDialog?.dismiss()
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun showEditPopup() {
        context?.let {
            customDialog = CustomDialog(it)
            customDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            customDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            customDialog?.show()
            customDialog?.setTitle(getString(R.string.text_cancel_queue_alert))
            val message = getString(R.string.text_edit_queue_text) + "\n" + date_string.substring(0, date_string.length - 3) + " - " + end_time.substring(0, end_time.length - 3) + " น."
            customDialog?.setMessage(message)
            customDialog?.setOnClickOKListener(View.OnClickListener {
                val queue_checkin_id = arguments?.getString("queue_checkin_id") ?: ""
                presenter.editChooseDatetime(queue_checkin_id, date_string2, queue_slot_id)
                customDialog?.dismiss()
                loadingDialog?.show()
            })
            customDialog?.setOnClickCancelListener(View.OnClickListener { customDialog?.dismiss() })
        }
    }
}