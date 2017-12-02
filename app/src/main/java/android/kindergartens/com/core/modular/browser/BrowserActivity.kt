package android.kindergartens.com.core.modular.browser

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.kindergartens.com.Constants
import android.kindergartens.com.R
import android.kindergartens.com.base.BaseToolbarActivity
import android.kindergartens.com.core.tools.ClipboardUtils
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.*
import kotlinx.android.synthetic.main.activity_web.*
import org.jetbrains.anko.toast


/**
 * Created by hcc on 16/8/7 14:12
 * 100332338@qq.com
 *
 *
 * 浏览器界面
 */
class BrowserActivity : BaseToolbarActivity() {


    private val mHandler = Handler()

    private var url: String? = null
    private var mTitle: String? = null

    private val webViewClient = WebViewClientBase()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        toolbar.title = if (TextUtils.isEmpty(mTitle)) "详情" else mTitle
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.menu_browser, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> onBackPressed()

            R.id.menu_share -> share()

            R.id.menu_open -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                startActivity(intent)
            }

            R.id.menu_copy -> {
                ClipboardUtils.setText(this@BrowserActivity, url)
                toast("已复制")
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun share() {

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享")
        intent.putExtra(Intent.EXTRA_TEXT, "来自「幼儿园小助手」的分享:" + url!!)
        startActivity(Intent.createChooser(intent, mTitle))
    }


    override fun onBackPressed() {

        if (mWebView!!.canGoBack() && mWebView!!.copyBackForwardList().size > 0
                && mWebView!!.url != mWebView!!.copyBackForwardList()
                .getItemAtIndex(0).originalUrl) {
            mWebView!!.goBack()
        } else {
            finish()
        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {

        circle_progress!!.visibility = View.VISIBLE

        val webSettings = mWebView!!.settings
        webSettings.javaScriptEnabled = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webSettings.domStorageEnabled = true
        webSettings.setGeolocationEnabled(true)
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        mWebView!!.settings.blockNetworkImage = true
        mWebView!!.webViewClient = webViewClient
        mWebView!!.requestFocus(View.FOCUS_DOWN)
        mWebView!!.settings.defaultTextEncodingName = "UTF-8"
        mWebView!!.webChromeClient = object : WebChromeClient() {

            override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {

                val b2 = AlertDialog.Builder(this@BrowserActivity)
                        .setTitle(R.string.app_name)
                        .setMessage(message)
                        .setPositiveButton("确定") { dialog, which -> result.confirm() }

                b2.setCancelable(false)
                b2.create()
                b2.show()
                return true
            }

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                circle_progress.progress = newProgress
            }
        }
        mWebView!!.loadUrl(url)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        val intent = intent
        if (intent != null) {
            url = intent.getStringExtra(Constants.EXTRA_URL)
            mTitle = intent.getStringExtra(Constants.EXTRA_TITLE)
        }


        setupWebView()
    }

    override fun onPause() {

        mWebView!!.reload()
        super.onPause()
    }

    override fun onDestroy() {

        mWebView!!.destroy()
        mHandler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    inner class WebViewClientBase : WebViewClient() {

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {

            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView, url: String) {

            super.onPageFinished(view, url)
            circle_progress!!.visibility = View.GONE
            mWebView!!.settings.blockNetworkImage = false
        }

        override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {

            super.onReceivedError(view, request, error)
            val errorHtml = "<html><body><h2>找不到网页</h2></body></html>"
            view.loadDataWithBaseURL(null, errorHtml, "text/html", "UTF-8", null)
        }

    }

    companion object {

        fun launch(activity: Context, url: String, title: String) {
            if (url.isNotEmpty()) {
                val intent = Intent(activity, BrowserActivity::class.java)
                intent.putExtra(Constants.EXTRA_URL, url)
                intent.putExtra(Constants.EXTRA_TITLE, title)
                activity.startActivity(intent)
            }
        }
    }

}
