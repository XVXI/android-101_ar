package com.transcode.smartcity101p2.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.contract.TicketDetailFragmentContract
import com.transcode.smartcity101p2.dialog.CustomDialog
import com.transcode.smartcity101p2.dialog.LoadingDialog
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.model.MyQueueResponse
import com.transcode.smartcity101p2.model.QueueDetailResponse
import com.transcode.smartcity101p2.presenter.TicketDetailFragmentPresenter
import com.transcode.smartcity101p2.utils.AppUtils
import com.transcode.smartcity101p2.view.CustomAppBarLayout
import kotlinx.android.synthetic.main.appbar_main.view.*
import kotlinx.android.synthetic.main.fragment_ticket_detail.*
import android.app.Activity
import android.content.Intent
import com.transcode.smartcity101p2.LocationActivity
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.SelectDateActivity

class TicketDetailFragment : CoreFragment(), TicketDetailFragmentContract.View {
    override fun cancelSuccess() {
        loadingDialog?.dismiss()
        activity?.apply {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    override fun showError(message: String) {
        loadingDialog?.dismiss()
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        fun newInstance(data: Any): TicketDetailFragment {
            return TicketDetailFragment().apply {
                val bundle = Bundle()
                if (data is MyQueueResponse) {
                    val gson = GsonBuilder().create()
                    val myQueueResponseString = gson.toJson(data)
                    bundle.putString("data", myQueueResponseString)
                }
                arguments = bundle
            }
        }
    }

    lateinit var presenter: TicketDetailFragmentPresenter
    var myQueueResponse: MyQueueResponse? = null
    var customDialog: CustomDialog? = null
    var loadingDialog: LoadingDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_ticket_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val bundle = savedInstanceState ?: arguments

        bundle?.let {
            if (it.containsKey("data")) {
                val gson = Gson()
                myQueueResponse = gson.fromJson(it.getString("data"), MyQueueResponse::class.java)
            }
        }
        initView()
    }

    private fun initView() {
        val appBar = appbar as CustomAppBarLayout
        appBar.setTitle(myQueueResponse?.QueueName.toString())
        appBar.leftBt.setOnClickListener {
            activity?.apply {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }

        presenter = TicketDetailFragmentPresenter(this)

        val account = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val name = account.authority_info?.FName + "\r\r" + account.authority_info?.LName
        text_name.text = name
        text_number.text = account.authority_info?.Email

        title.text = myQueueResponse?.QueueName
        type.text = when {
            myQueueResponse?.QueueType == "1" -> "จองทันที"
            myQueueResponse?.QueueType == "2" -> "จองล่วงหน้า"
            else -> "จองนอกสถานที่"
        }
        booking_date.text = myQueueResponse?.CreateDatetime

        queue_number.text = myQueueResponse?.QueueNo

        val createDateString = myQueueResponse?.CreateDatetime ?: "0"
        val chooseDatetime = myQueueResponse?.ChooseDatetime ?: "0"
        when {
            myQueueResponse?.QueueType == "1" -> {

                image.load("", R.mipmap.now)

                val dateBook = "วันที่กดคิว " + "วันนี้"
                val before = "รออีก " + myQueueResponse?.BeforeQueue + " คน"
                booking_date.text = dateBook
                date.text = "วันนี้"
                time.text = before
            }
            myQueueResponse?.QueueType == "2" -> {
                image.load("", R.mipmap.book)
                val timeMillsBook = AppUtils.dateStringToMillis(createDateString, arrayOf(AppUtils.formateDate2))
                val dateBook = "วันที่กดคิว " + AppUtils.getDateString(AppUtils.formateDate3, timeMillsBook) + " " + AppUtils.getDateString(AppUtils.formateDate4, timeMillsBook)
                booking_date.text = dateBook

                val timeMills = AppUtils.dateStringToMillis(chooseDatetime, arrayOf(AppUtils.formateDate2))
                date.text = AppUtils.getDateString("EEEE " + AppUtils.formateDate3, timeMills)
                var fromtime = myQueueResponse?.QueueFromTime ?: "12:00"
                if (fromtime.length > 3) fromtime = fromtime.substring(0, fromtime.length - 3)
                var totime = myQueueResponse?.QueueToTime ?: "12:00"
                if (totime.length > 3) totime = totime.substring(0, totime.length - 3)
                val times = "$fromtime - $totime"
                time.text = times
            }
            else -> {
                image.load("", R.mipmap.trucking)
                val timeMillsBook = AppUtils.dateStringToMillis(createDateString, arrayOf(AppUtils.formateDate2))
                val dateBook = "วันที่กดคิว " + AppUtils.getDateString(AppUtils.formateDate3, timeMillsBook) + " " + AppUtils.getDateString(AppUtils.formateDate4, timeMillsBook)
                booking_date.text = dateBook

                val timeMills = AppUtils.dateStringToMillis(chooseDatetime, arrayOf(AppUtils.formateDate2))
                date.text = AppUtils.getDateString("EEEE " + AppUtils.formateDate3, timeMills)
                var fromtime = myQueueResponse?.QueueFromTime ?: "12:00"
                if (fromtime.length > 3) fromtime = fromtime.substring(0, fromtime.length - 3)
                var totime = myQueueResponse?.QueueToTime ?: "12:00"
                if (totime.length > 3) totime = totime.substring(0, totime.length - 3)
                val times = "$fromtime - $totime"
                time.text = times
            }
        }

        location.setOnClickListener {
            activity?.let {
                var lat: String
                var lng: String

                if (myQueueResponse?.QueueType == "3") {
                    lat = myQueueResponse?.ServeLat ?: "0"
                    lng = myQueueResponse?.ServeLng ?: "0"
                } else {
                    lat = myQueueResponse?.QueueLat ?: "0"
                    lng = myQueueResponse?.QueueLng ?: "0"
                }

                val intent = Intent(context, LocationActivity::class.java)
                intent.putExtra("lat", lat.toDouble())
                intent.putExtra("lng", lng.toDouble())
                startActivity(intent)
            }
        }

        context?.let {
            loadingDialog = LoadingDialog(it)
            loadingDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            loadingDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            loadingDialog?.setCancelable(false)
        }

        tomain.setOnClickListener {
            activity?.apply {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }

        cancel_ticket.setOnClickListener {
            showCancelPopup()
        }

        edit_ticket.setOnClickListener {
            activity?.apply {
                val intent = Intent(this, SelectDateActivity::class.java)
                intent.putExtra("queue_id", myQueueResponse?.QueueId ?: "")
                intent.putExtra("queue_checkin_id", myQueueResponse?.QueueCheckinId ?: "")
                intent.putExtra("queue_title", myQueueResponse?.QueueName ?: "")
                intent.putExtra("isEdit", true)
                startActivityForResult(intent, Const.REQUEST_CODE_BOOK_QUEUE)
            }
        }

        val timeMills = AppUtils.dateStringToMillis(myQueueResponse?.ChooseDatetime
                ?: "0", arrayOf(AppUtils.formateDate2))
        val timeString = AppUtils.getDateString(AppUtils.formateDate1, timeMills) + " 23:59:59"
        val timeMills_midnight = AppUtils.dateStringToMillis(timeString, arrayOf(AppUtils.formateDate2)) - AppUtils.day
        val currentTime = System.currentTimeMillis()
        if (currentTime > timeMills_midnight) {
            cancel_ticket.visibility = View.GONE
            edit_ticket.visibility = View.GONE
        }
    }

    override fun updateView(queueDetailResponse: QueueDetailResponse) {

    }

    private fun showCancelPopup() {
        context?.let {
            customDialog = CustomDialog(it)
            customDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            customDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            customDialog?.show()
            customDialog?.setTitle(getString(R.string.text_cancel_queue_alert))
            customDialog?.setMessage(getString(R.string.text_cancel_queue))
            customDialog?.setOnClickOKListener(View.OnClickListener {
                presenter.cancelQueueCheckin(myQueueResponse?.QueueCheckinId ?: "")
                customDialog?.dismiss()
                loadingDialog?.show()
            })
            customDialog?.setOnClickCancelListener(View.OnClickListener { customDialog?.dismiss() })
        }
    }
}