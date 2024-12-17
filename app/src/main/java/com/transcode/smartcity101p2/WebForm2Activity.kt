package com.transcode.smartcity101p2

import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.util.Base64
import android.webkit.*
import android.webkit.WebChromeClient.FileChooserParams.*
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import kotlinx.android.synthetic.main.activity_web_form2.*
import java.nio.charset.StandardCharsets
import android.widget.Toast


class WebForm2Activity : CoreActivity() {

    var wcc = object : WebChromeClient() {
        override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?,
                                       fileChooserParams: FileChooserParams?): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                uploadMessage?.onReceiveValue(null)
                uploadMessage = null

                uploadMessage = filePathCallback

                val intent = fileChooserParams?.createIntent()
                try {
                    startActivityForResult(intent, REQUEST_SELECT_FILE)
                } catch (e: ActivityNotFoundException) {
                    uploadMessage = null
                    Toast.makeText(this@WebForm2Activity, "Cannot open file chooser", Toast.LENGTH_LONG).show()
                    return false
                }

                return true
            }
            return true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_form2)

        val account = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)

        val market_url = BuildConfig.MARKETS_URL
        val url = "$market_url/home"
        var data = "citizen_id=" + account.authority_info?.CitizenId +
                "&FName=" + account.authority_info?.FName +
                "&LName=" + account.authority_info?.LName +
                "&Email=" + account.authority_info?.Email +
                "&Token=" + account.authority_info?.getAllToken() +
                "&token_type=" + account.server?.token_type
        data = Base64.encodeToString(data.toByteArray(StandardCharsets.UTF_8), Base64.DEFAULT)
        val token = account.server?.token ?: ""

        val urls = "$url?data=$data&token=$token&isstore=1"

        webView.loadUrl(urls)

        webView.apply {

            webChromeClient = wcc

            webViewClient = object : WebViewClient() {

                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    return super.shouldOverrideUrlLoading(view, request)
                }

                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    return super.shouldOverrideUrlLoading(view, url)
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {

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
            allowContentAccess = true
            allowFileAccess = true
        }
    }

    val REQUEST_SELECT_FILE = 100;
    var uploadMessage: ValueCallback<Array<Uri>>? = null

    @RequiresApi()
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_SELECT_FILE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                uploadMessage?.onReceiveValue(parseResult(resultCode, data))
                uploadMessage = null
            }
        }
    }
}