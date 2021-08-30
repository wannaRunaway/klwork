package com.kulun.energynet.customizeView;


import android.content.Context;
import android.widget.Toast;

/**
 * ToastUtils 利用单例模式，解决Toast重复弹出的问题
 */
public class ToastUtils {

    private static Toast toast;
    public static void showShort(CharSequence sequence, Context context) {
        if (toast == null) {
            toast = Toast.makeText(context.getApplicationContext(), sequence, Toast.LENGTH_SHORT);
        } else {
            toast.setText(sequence);
        }
        toast.show();
    }
}