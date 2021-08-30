package com.kulun.energynet.main;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import com.trello.rxlifecycle4.components.RxActivity;
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity;

/**
 * created by xuedi on 2019/7/31
 */
public abstract class BaseActivity extends RxAppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        changeToLightStatusBar(this);
        initView(savedInstanceState);
    }
//    public static void changeToLightStatusBar(@NonNull Activity activity) {
//        try {
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//                return;
//            }
//            if (activity == null) {
//                return;
//            }
//            Window window = activity.getWindow();
//            if (window == null) {
//                return;
//            }
//
//            View decorView = window.getDecorView();
//            if (decorView == null) {
//                return;
//            }
//            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
    public abstract void initView(Bundle savedInstanceState);
}
