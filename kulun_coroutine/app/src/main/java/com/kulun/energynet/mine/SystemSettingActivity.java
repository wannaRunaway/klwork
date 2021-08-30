package com.kulun.energynet.mine;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kulun.energynet.R;
import com.kulun.energynet.databinding.ActivitySystemSettingBinding;
import com.kulun.energynet.login.PasswordLoginActivity;
import com.kulun.energynet.main.BaseActivity;
import com.kulun.energynet.model.ResponseModel;
import com.kulun.energynet.model.User;
import com.kulun.energynet.network.InternetWorkManager;
import com.kulun.energynet.network.MyObserver;
import com.kulun.energynet.network.RxHelper;
import com.kulun.energynet.requestparams.Response;
import com.kulun.energynet.network.API;
import com.kulun.energynet.requestparams.MyRequest;
import com.kulun.energynet.utils.SharePref;
import com.kulun.energynet.utils.Utils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SystemSettingActivity extends BaseActivity {
    private ActivitySystemSettingBinding binding;
    private long time;
    //private String apkpat
    @Override
    public void initView(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this ,R.layout.activity_system_setting);
        binding.title.setText("设置");
        //apkpath = getIntent().getStringExtra(API.apppath);
        binding.left.setOnClickListener(view -> finish());
        binding.rePersonl.setOnClickListener(view -> {
            Intent intent = new Intent(SystemSettingActivity.this, PersonalActivity.class);
            intent.putExtra(API.register, false);
            startActivity(intent);
        });
        binding.reChangePassword.setOnClickListener(view -> {
            Intent intent = new Intent(SystemSettingActivity.this,ChangePasswordActivity.class);
            startActivity(intent);
        });
        binding.tvLoginOut.setOnClickListener(view -> {
            if (Utils.isFastClick()){
                Utils.snackbar( SystemSettingActivity.this, "点击过快");
                return;
            }
            loginOut();
        });
    }


    private void loginOut() {
        InternetWorkManager.getRequest().logout()
                .compose(RxHelper.observableIOMain(SystemSettingActivity.this))
                .subscribe(new MyObserver<User>() {
                    @Override
                    public void onSuccess(User data) {
                        Intent intent = new Intent(SystemSettingActivity.this, PasswordLoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        User.getInstance().setToken("");
                        SharePref.put(SystemSettingActivity.this, API.token, "");
                        SharePref.clear(SystemSettingActivity.this);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFail(int code, String message) {

                    }

                    @Override
                    public void onError() {

                    }

                    @Override
                    public void onClearToken() {
                        Utils.toLogin(SystemSettingActivity.this);
                    }
                });
//        new MyRequest().myRequest(API.LOGINOUT, true,null,true,this, null, null, new Response() {
//            @Override
//            public void response(JsonObject json, JsonArray jsonArray,boolean isNull) {
//                Intent intent = new Intent(SystemSettingActivity.this, PasswordLoginActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                User.getInstance().setToken("");
//                SharePref.put(SystemSettingActivity.this, API.token, "");
//                SharePref.clear(SystemSettingActivity.this);
//                startActivity(intent);
//                finish();
//            }
//        });
    }
}