package android.kindergartens.com.core.modular.checkupdate

import android.content.Context
import android.content.DialogInterface
import android.kindergartens.com.R
import android.kindergartens.com.base.BaseActivity
import android.kindergartens.com.core.ui.BaseCheckDialog
import android.kindergartens.com.net.ServerApi
import android.kindergartens.com.service.CheckUpdateService
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView

import com.allenliu.versionchecklib.callback.APKDownloadListener
import com.allenliu.versionchecklib.callback.CommitClickListener
import com.allenliu.versionchecklib.callback.DialogDismissListener
import com.allenliu.versionchecklib.core.AllenChecker
import com.allenliu.versionchecklib.core.VersionDialogActivity
import com.allenliu.versionchecklib.core.VersionParams
import com.allenliu.versionchecklib.core.http.HttpParams
import com.allenliu.versionchecklib.core.http.HttpRequestMethod
import com.mazouri.tools.Tools

import java.io.File

/**
 * Created by zhangruiyu on 2017/11/21.
 */

class CustomVersionDialogActivity : VersionDialogActivity(), CommitClickListener, DialogDismissListener, APKDownloadListener {


    var loadingView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //这里是几个回调
        setApkDownloadListener(this)
        setCommitClickListener(this)
        setDialogDimissListener(this)
    }

    /**
     * 下载文件成功也关闭app
     * 也判断是否强制更新
     *
     * @param file
     */
    override fun onDownloadSuccess(file: File) {
        forceCloseApp()
        Log.e("CustomVersionDialogActi", "文件下载成功回调")
    }

    override fun onDownloadFail() {

    }

    override fun onDownloading(progress: Int) {

        //        Log.e("CustomVersionDialogActi", "正在下载中回调...");
    }

    override fun onCommitClick() {
        Log.e("CustomVersionDialogActi", "确认按钮点击回调")
    }


    /**
     * 自定义更新展示界面 直接重写此方法就好
     */
    public override fun showVersionDialog() {
        //使用默认的提示框直接调用父类的方法,如果需要自定义的对话框，那么直接重写此方法
        // super.showVersionDialog();
        customVersionDialogTwo()
        //        Toast.makeText(this, "重写此方法显示自定义对话框", Toast.LENGTH_SHORT).show();
    }

    /**
     * 自定义dialog two
     */
    private fun customVersionDialogTwo() {
        versionDialog = BaseCheckDialog(this, R.style.BaseDialog, R.layout.custom_dialog_two_layout)
        val tvTitle = versionDialog.findViewById<TextView>(R.id.tv_title)
        val tvMsg = versionDialog.findViewById<TextView>(R.id.tv_msg)
        val btnUpdate = versionDialog.findViewById<Button>(R.id.btn_update)

        versionDialog.show()
        //设置dismiss listener 用于强制更新,dimiss会回调dialogDismiss方法
        versionDialog.setOnDismissListener(this)
        //可以使用之前从service传过来的一些参数比如：title。msg，downloadurl，parambundle
        tvTitle.text = versionTitle
        tvMsg.text = versionUpdateMsg
        //可以使用之前service传过来的值
        val bundle = versionParamBundle
        btnUpdate.setOnClickListener {
            versionDialog.dismiss()
            super@CustomVersionDialogActivity.dealAPK()
        }
        versionDialog.show()
    }

    /**
     * 自定义下载失败重试对话框
     * 使用父类的failDialog
     */
    override fun showFailDialog() {
        super.showFailDialog()
        //        Toast.makeText(this, "重写此方法使用自定义失败加载框", Toast.LENGTH_SHORT).show();
    }

    /**
     * 要更改下载中界面 只需要重写此方法即可
     * 因为下载的时候会不断回调此方法
     * dialog使用全局 只初始化一次
     * 使用父类的loadingDialog保证下载成功会dimiss掉dialog
     *
     * @param currentProgress
     */
    override fun showLoadingDialog(currentProgress: Int) {
        if (!isCustomDownloading) {
            super.showLoadingDialog(currentProgress)
        } else {
            //使用父类的loadingDialog保证下载成功会dimiss掉dialog
            if (loadingDialog == null) {
                loadingView = LayoutInflater.from(this).inflate(R.layout.custom_download_layout, null)
                loadingDialog = AlertDialog.Builder(this).setTitle("").setView(loadingView).create()
                loadingDialog.setCancelable(false)
                loadingDialog.setCanceledOnTouchOutside(false)
                loadingDialog.setOnCancelListener { finish() }
            }
            val pb = loadingView?.findViewById<View>(R.id.pb) as ProgressBar
            val tvProgress = loadingView?.findViewById<View>(R.id.tv_progress) as TextView
            tvProgress.text = String.format(getString(R.string.versionchecklib_progress), currentProgress)
            pb.progress = currentProgress
            loadingDialog.show()
        }
        //        Toast.makeText(this, "显示自定义的下载加载框", Toast.LENGTH_SHORT).show();
    }


    /**
     * versiondialog dismiss 的时候会回调此方法
     * 这里面可以进行强制更新操作
     *
     *
     * 建议用一个ActivityManger记录每个Activity出入堆栈
     * 最后全部关闭activity 实现app exit
     * ActivityTaskManger.finishAllActivity();
     *
     * @param dialog
     */
    override fun dialogDismiss(dialog: DialogInterface) {
        Log.e("CustomVersionDialogActi", "dialog dismiss 回调")
        //        finish();
        forceCloseApp()
    }

    /**
     * 在dialogDismiss和onDownloadSuccess里面强制更新
     * 分别表示两种情况：
     * 一种用户取消下载  关闭app
     * 一种下载成功安装的时候 应该也关闭app
     */
    private fun forceCloseApp() {
        if (isForceUpdate) {
            //我这里为了简便直接finish 就行了
            BaseActivity.exitApp()
        }
    }

    override fun onResume() {
        super.onResume()
        BaseActivity.runActivity = this
        BaseActivity.activists.add(this)
    }

    override fun onPause() {
        super.onPause()
        BaseActivity.activists.remove(this)
    }

    companion object {
        var isForceUpdate = false
        var isCustomDownloading = false
        fun chechUpdate(ctx: Context) {
            val httpParams = HttpParams()
            httpParams.put("version", Tools.appTool().getAppVersionCode(ctx))
            httpParams.put("os", "Android")
            //服务不需要关闭,检查更新完后自动会关闭
            val builder = VersionParams.Builder().setRequestMethod(HttpRequestMethod.POST).setRequestUrl(ServerApi.baseUrl + "/checkUpdate").setRequestParams(httpParams)
                    .setService(CheckUpdateService::class.java).setCustomDownloadActivityClass(
                    CustomVersionDialogActivity::class.java
            )
            AllenChecker.startVersionCheck(ctx, builder.build())
        }
    }

}
