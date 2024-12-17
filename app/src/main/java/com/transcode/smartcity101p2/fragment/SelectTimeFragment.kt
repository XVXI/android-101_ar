package com.transcode.smartcity101p2.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.transcode.smartcity101p2.contract.SelectTimeFragmentContract
import com.transcode.smartcity101p2.presenter.SelectTimeFragmentPresenter
import com.transcode.smartcity101p2.utils.AppUtils
import com.transcode.smartcity101p2.view.CustomAppBarLayout
import kotlinx.android.synthetic.main.appbar_main.view.*
import kotlinx.android.synthetic.main.fragment_select_time.*
import android.app.TimePickerDialog
import android.widget.Toast
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.adapter.TimeListAdapter
import com.transcode.smartcity101p2.model.MyQueueResponse
import com.transcode.smartcity101p2.model.QueueSlotResponse
import java.util.*
import kotlin.collections.ArrayList


class SelectTimeFragment : CoreFragment(), SelectTimeFragmentContract.View, TimeListAdapter.ClickItem {
    override fun onclickItem(res: QueueSlotResponse.QueueSlotData) {
        selectTime = res.QueueFromTime ?: "12.00"
        queue_slot_id = res.QueueSlotId ?: "1"
    }

    companion object {
        fun newInstance(queue_id: String, dateTimeMills: Long, form_url: String, queue_type: String, base_lat: String, base_lng: String, queue_title: String): SelectTimeFragment {
            return SelectTimeFragment().apply {
                val bundle = Bundle()
                bundle.putString("queue_id", queue_id)
                bundle.putLong("dateTimeMills", dateTimeMills)
                bundle.putString("form_url", form_url)
                bundle.putString("queue_type", queue_type)
                bundle.putString("base_lat", base_lat)
                bundle.putString("base_lng", base_lng)
                bundle.putString("queue_title", queue_title)
                arguments = bundle
            }
        }
    }

    lateinit var presenter: SelectTimeFragmentPresenter
    lateinit var adapter: TimeListAdapter
    var selectTime = ""
    var queue_slot_id = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_select_time, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        selectTime = ""
        queue_slot_id = ""
        val appBar = appbar as CustomAppBarLayout
        appBar.setTitle("เลือกเวลา")
        appBar.leftBt.setOnClickListener {
            fragmentManager?.popBackStack()
            FragmentHelper.remove(fragmentManager, this)
        }

        presenter = SelectTimeFragmentPresenter(this)

        val textDate = "วันที่ " + AppUtils.getDateString(AppUtils.formateDate3, arguments?.getLong("dateTimeMills")
                ?: 0)

        dateSelect.text = textDate

        selecttime.setOnClickListener {
            context?.let {
                val mcurrentTime = Calendar.getInstance()
                val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
                val minute = mcurrentTime.get(Calendar.MINUTE)
                val mTimePicker: TimePickerDialog
                mTimePicker = TimePickerDialog(it, TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                    val min = if (selectedMinute < 10) {
                        "0$selectedMinute"
                    } else {
                        selectedMinute.toString()
                    }
                    selectTime = selectedHour.toString() + ":" + min
                    selecttime.text = selectTime
                }, hour, minute, true)//Yes 24 hour time
                mTimePicker.setTitle("Select Time")
                mTimePicker.show()
            }
        }

        submit.setOnClickListener {
            if (selectTime.isEmpty()) {
                showError("โปรดเลือกเวลารับบริการ")
            } else {
                context?.let {

                    val queue_id = arguments?.getString("queue_id") ?: "1"
                    val queue_type = arguments?.getString("queue_type") ?: "2"
                    val url = arguments?.getString("form_url") ?: ""
                    val date_string = AppUtils.getDateString(AppUtils.formateDate3, arguments?.getLong("dateTimeMills")
                            ?: 0) + " " + selectTime
                    val base_lat = arguments?.getString("base_lat") ?: "0"
                    val base_lng = arguments?.getString("base_lng") ?: "0"
                    val queue_title = arguments?.getString("queue_title") ?: ""


//                    fragmentManager?.let {
//                        if (arguments?.getString("form_url") ?: "" == "") {
//
//                            if (queue_type == "2") {
//                                FragmentHelper.replace(it, BookQueueFragment.newInstance(queue_type, queue_id, date_string, base_lat, base_lng, queue_slot_id, queue_title), R.id.content_home_frame)
//                            } else {
//                                FragmentHelper.replace(it, SelectLocationFragment.newInstance(queue_id, date_string, queue_slot_id, queue_title), R.id.content_home_frame)
//                            }
//                        } else {
//                            FragmentHelper.replace(it, WebFormViewFragment.newInstance("Form",
//                                    url,
//                                    queue_type,
//                                    queue_id,
//                                    date_string,
//                                    queue_slot_id,
//                                    base_lat,
//                                    base_lng,
//                                    queue_title), R.id.content_home_frame)
//                        }
//                    }

                }
            }
        }

        context?.let {
            adapter = TimeListAdapter(it)
            adapter.setClickListener(this)
            adapter.setRecyclerView(recyclerview)
        }

        presenter.getQueueSlot(arguments?.getString("queue_id")
                ?: "", AppUtils.getDateString(AppUtils.formateDate1, arguments?.getLong("dateTimeMills")
                ?: 0))
    }

    override fun showError(message: String) {
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun bookSuccess(myQueueResponse: MyQueueResponse) {
        fragmentManager?.let {
            FragmentHelper.replace(it, TicketDetailFragment.newInstance(myQueueResponse), R.id.content_home_frame)
        }
    }

    override fun getQueueSlotSuccess(list: ArrayList<QueueSlotResponse.QueueSlotData>) {
        adapter.setData(list)
        adapter.notifyDataSetChanged()
    }
}