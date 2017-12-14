package android.kindergartens.com.core.tools;


import com.github.yoojia.inputs.AndroidMessageDisplay;
import com.github.yoojia.inputs.AndroidNextInputs;

/**
 * Created by zhangruiyu on 2017/12/14.
 */

public class CustomNextInputs extends AndroidNextInputs {
    public CustomNextInputs() {
        setMessageDisplay(new AndroidMessageDisplay());
    }
}
