package android.kindergartens.com.core.ui;

import android.content.Context;
import android.kindergartens.com.R;
import android.util.AttributeSet;

import ch.halcyon.squareprogressbar.SquareProgressBar;

/**
 * Created by zhangruiyu on 2017/12/13.
 */

public class CustomSquareProgressBar extends SquareProgressBar {

    public CustomSquareProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setHoloColor(R.color.primary);
        setWidth(2);
    }

    public CustomSquareProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomSquareProgressBar(Context context) {
        super(context);
        init();
    }
}
