package com.transcode.smartcity101p2.fragment

import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.webkit.*
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.BookQueueActivity
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.SelectLocationActivity
import com.transcode.smartcity101p2.TicketDetailActivity
import com.transcode.smartcity101p2.contract.WebFormViewFragmentContract
import com.transcode.smartcity101p2.dialog.CustomDialog
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.model.MyQueueResponse
import com.transcode.smartcity101p2.presenter.WebFormViewFragmentPresenter
import com.transcode.smartcity101p2.view.CustomAppBarLayout
import kotlinx.android.synthetic.main.appbar_main.view.*
import kotlinx.android.synthetic.main.fragment_webview.*

class WebFormViewFragment : CoreFragment(), WebFormViewFragmentContract.View {
    override fun showError(message: String) {
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun bookSuccess(myQueueResponse: MyQueueResponse) {
        activity?.apply {
            val intent = Intent(this, TicketDetailActivity::class.java)
            val gson = GsonBuilder().create()
            val jsonData = gson.toJson(myQueueResponse)
            intent.putExtra("data", jsonData)
            startActivityForResult(intent, Const.REQUEST_CODE_BOOK_QUEUE)
        }
    }

    companion object {
        fun newInstance(title: String, url: String, queue_type: String, queue_id: String, queue_title: String): WebFormViewFragment {
            return WebFormViewFragment().apply {
                val bundle = Bundle()
                bundle.putString("title", title)
                bundle.putString("url", url)
                bundle.putString("queue_type", queue_type)
                bundle.putString("queue_id", queue_id)
                bundle.putString("queue_title", queue_title)
                arguments = bundle
            }
        }

        fun newInstance(title: String, url: String, queue_type: String, queue_id: String, date_string: String, queue_slot_id: String, base_lat: String, base_lng: String, queue_title: String, endtime: String): WebFormViewFragment {
            return WebFormViewFragment().apply {
                val bundle = Bundle()
                bundle.putString("title", title)
                bundle.putString("url", url)
                bundle.putString("queue_type", queue_type)
                bundle.putString("queue_id", queue_id)
                bundle.putString("date_string", date_string)
                bundle.putString("queue_slot_id", queue_slot_id)
                bundle.putString("base_lat", base_lat)
                bundle.putString("base_lng", base_lng)
                bundle.putString("queue_title", queue_title)
                bundle.putString("endtime", endtime)
                arguments = bundle
            }
        }
    }

    lateinit var presenter: WebFormViewFragmentPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_webview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        val appBar = appbar as CustomAppBarLayout
        appBar.setTitle(arguments?.getString("title") ?: "title")
        appBar.leftBt.setOnClickListener {
            activity?.apply {
                finish()
            }
        }

        presenter = WebFormViewFragmentPresenter(this)

        val url = arguments?.getString("url") ?: "http://www.google.com"
        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val citizen_id = loginResponse.authority_info?.CitizenId ?: "0"
        val token = loginResponse.authority_info?.getAllToken() ?: "0"
        val urls = "$url?citizen_id=$citizen_id&token=$token"

        mWebview.loadUrl(urls)
//        mWebview.loadUrl("file:///android_asset/web.html")

        mWebview.apply {
            //             addJavascriptInterface(JavascriptInterface(), "Android")
            webChromeClient = WebChromeClient()

            webViewClient = object : WebViewClient() {

                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    return super.shouldOverrideUrlLoading(view, request)
                }

                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    //                    if(success){
//                        //check protocal

//                    if (arguments?.get("queue_type") == "1") {
//                        showBookNowPopup()
//                    }
//                        return true
//                    }

                    url?.let {
                        if (it.startsWith("https://sccitizen//") || it.startsWith("http://sccitizen//")) {
                            if (it.contains("success")) {
                                val queue_type = arguments?.getString("queue_type") ?: "1"
                                val queue_id = arguments?.getString("queue_id") ?: "1"
                                val date_string = arguments?.getString("date_string") ?: "1"
                                val queue_slot_id = arguments?.getString("queue_slot_id") ?: "1"
                                val base_lat = arguments?.getString("base_lat") ?: "0"
                                val base_lng = arguments?.getString("base_lng") ?: "0"
                                val queue_title = arguments?.getString("queue_title") ?: ""
                                val endtime = arguments?.getString("endtime") ?: "0"

                                var form_id = ""
                                if (it.contains("?")) {
                                    var s = it.split("?")[1]
                                    s = s.split("=")[1]
                                    if (s.contains("&")) {
                                        s = s.split("&")[0]
                                    }
                                    form_id = s
                                }

                                when (queue_type) {
                                    "1" -> showBookNowPopup(form_id)
                                    "2" -> activity?.apply {
                                        val intent = Intent(this, BookQueueActivity::class.java)
                                        intent.putExtra("queue_type", queue_type)
                                        intent.putExtra("queue_id", queue_id)
                                        intent.putExtra("choose_datatime", date_string)
                                        intent.putExtra("serve_lat", base_lat)
                                        intent.putExtra("serve_lng", base_lng)
                                        intent.putExtra("queue_slot_id", queue_slot_id)
                                        intent.putExtra("queue_title", queue_title)
                                        intent.putExtra("endtime", endtime)
                                        intent.putExtra("form_id", form_id)
                                        startActivityForResult(intent, Const.REQUEST_CODE_BOOK_QUEUE)
                                    }
                                    else -> activity?.apply {
                                        val intent = Intent(this, SelectLocationActivity::class.java)
                                        intent.putExtra("queue_id", queue_id)
                                        intent.putExtra("choose_datatime", date_string)
                                        intent.putExtra("queue_slot_id", queue_slot_id)
                                        intent.putExtra("queue_title", queue_title)
                                        intent.putExtra("endtime", endtime)
                                        intent.putExtra("form_id", form_id)
                                        startActivityForResult(intent, Const.REQUEST_CODE_BOOK_QUEUE)
                                    }
                                }
                            } else {
                                Toast.makeText(context, "fail", Toast.LENGTH_LONG).show()
                            }
                            return true
                        }
                    }

                    return super.shouldOverrideUrlLoading(view, url)
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
//                    url?.let { url ->
//                        if (url.contains(RESPONSE_SUCCESS, false)) {
//                            dismiss()
//                        }
//                    }
                }

                override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                    val builder = AlertDialog.Builder(context)
                    var message = Const.SSL_CERTIFICATE_ERROR
                    when (error?.primaryError) {
                        SslError.SSL_UNTRUSTED -> message = Const.AUTHORITY_NOT_TRUSTED
                        SslError.SSL_EXPIRED -> message = Const.CERTIFICATE_HAS_EXPIRED
                        SslError.SSL_IDMISMATCH -> message = Const.CERTIFICATE_HOSTNAME_MISMATCH
                        SslError.SSL_NOTYETVALID -> message = Const.CERTIFICATE_IS_NOT_YET_VALID
                    }
                    message += Const.POST_FIX_MESSAGE

                    builder.setTitle(Const.TITLE_SSL_ERROR)
                    builder.setMessage(message)
                    builder.setPositiveButton(Const.TITLE_BUTTON_CONTINUE) { dialog, which -> handler?.proceed() }
                    builder.setNegativeButton(Const.TITLE_BUTTON_CANCEL) { dialog, which -> handler?.cancel() }
                    val dialog = builder.create()
                    dialog.show()
                }
            }

        }.settings.apply {
            //Custom webView setting
            domStorageEnabled = true
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
            loadsImagesAutomatically = true
            pluginState = WebSettings.PluginState.ON
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            cacheMode = WebSettings.LOAD_DEFAULT
        }
    }

    var customDialog: CustomDialog? = null

    private fun showBookNowPopup(form_id: String) {
        val account = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        context?.let {
            customDialog = CustomDialog(it)
            customDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            customDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            customDialog?.show()
            customDialog?.setTitle(getString(R.string.confirm_queue_title_text))
            customDialog?.setMessage(getString(R.string.confirm_queue_message_text))
            customDialog?.setOnClickOKListener(View.OnClickListener {
                customDialog?.dismiss()
                presenter.bookNow(arguments?.getString("queue_id")
                        ?: "1", account.authority_info?.CitizenId.toString(), form_id)
            })
            customDialog?.setOnClickCancelListener(View.OnClickListener { customDialog?.dismiss() })
        }
    }

    internal inner class JavascriptInterface {

        @android.webkit.JavascriptInterface
        fun reloadWithActualSize(updatedHtml: String) {
            mWebview.loadDataWithBaseURL("file:///android_asset/", updatedHtml, "text/html", "UTF-8", "")
        }

    }
}