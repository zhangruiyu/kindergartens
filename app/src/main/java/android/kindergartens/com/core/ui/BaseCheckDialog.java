package android.kindergartens.com.core.ui;

import android.app.Dialog;
import android.content.Context;

/**
 * Created by zhangruiyu on 2017/11/20.
 */

public class BaseCheckDialog extends Dialog {
    private int res;

    public BaseCheckDialog(Context context, int theme, int res) {
        super(context, theme);
        setContentView(res);
        this.res = res;

        setCanceledOnTouchOutside(false);
    }

}