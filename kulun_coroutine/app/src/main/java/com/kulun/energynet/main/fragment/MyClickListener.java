package com.kulun.energynet.main.fragment;

import android.app.Activity;
import android.view.View;

import com.kulun.energynet.utils.Utils;

public class MyClickListener implements View.OnClickListener{
    private Activity activity;
    private View.OnClickListener listener;
    public MyClickListener(Activity activity,View.OnClickListener listener){
        this.activity = activity;
        this.listener = listener;
    }
    @Override
    public void onClick(View v) {
        if (Utils.getToken(activity) == null || Utils.getToken(activity).equals("")) {
            Utils.toLogin(activity);
            Utils.snackbar(activity, "请先登陆");
            return;
        }
        v.setOnClickListener(listener);
    }
}
