package com.transcode.smartcity101p2.fragment

import android.app.AlertDialog
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.presenter.ForgotFragmentPresenter
import com.transcode.smartcity101p2.view.CustomAppBarLayout
import kotlinx.android.synthetic.main.appbar_main.view.*
import kotlinx.android.synthetic.main.fragment_webview.*

class WebViewFragment : CoreFragment() {

    companion object {
        fun newInstance(title: String, url: String): WebViewFragment {
            return WebViewFragment().apply {
                val bundle = Bundle()
                bundle.putString("title", title)
                bundle.putString("url", url)
                arguments = bundle
            }
        }
    }

    lateinit var presenter: ForgotFragmentPresenter

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
            fragmentManager?.popBackStack()
            FragmentHelper.remove(fragmentManager, this)
        }

        mWebview.loadUrl(arguments?.getString("url") ?: "http://www.google.com")

        mWebview.apply {
//            addJavascriptInterface(JavascriptInterface(), "Android")
            webChromeClient = WebChromeClient()

            webViewClient = object : WebViewClient() {

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
//                    url?.let { url ->
//                        if (url.contains(RESPONSE_SUCCESS, false)) {
//                            dismiss()
//                        }
//                    }
                }

                override fun onPageFinished(webView: WebView, url: String) {
                    super.onPageFinished(webView, url)

                }

                override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                    val builder = AlertDialog.Builder(context)
                    var message = Const.SSL_CERTIFICATE_ERROR
                    when (error?.getPrimaryError()) {
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
        }
    }

    internal inner class JavascriptInterface {

        @android.webkit.JavascriptInterface
        fun reloadWithActualSize(updatedHtml: String) {
            mWebview.loadDataWithBaseURL("file:///android_asset/", updatedHtml, "text/html", "UTF-8", "")
        }

    }
}