package com.kulun.energynet.mine.systemsetting;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.kulun.energynet.R;
import com.kulun.energynet.databinding.ActivitySystemSettingBinding;
import com.kulun.energynet.login.PasswordLoginActivity;
import com.kulun.energynet.main.BaseActivity;
import com.kulun.energynet.mine.ChangePasswordActivity;
import com.kulun.energynet.mine.PersonalActivity;
import com.kulun.energynet.model.User;
import com.kulun.energynet.network.InternetWorkManager;
import com.kulun.energynet.network.MyObserver;
import com.kulun.energynet.network.RxHelper;
import com.kulun.energynet.network.API;
import com.kulun.energynet.utils.SharePref;
import com.kulun.energynet.utils.Utils;

public class SystemSettingActivity extends BaseActivity implements ISystemSettingView{
    private ActivitySystemSettingBinding binding;
    private SystemSettingPresent systemSettingPresent;
    //private String apkpat
    @Override
    public void initView(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this ,R.layout.activity_system_setting);
        systemSettingPresent = new SystemSettingPresent(this);
        binding.title.setText("设置");
        //apkpath = getIntent().getStringExtra(API.apppath);
        binding.left.setOnClickListener(view -> finish());
        binding.rePersonl.setOnClickListener(view -> {
            systemSettingPresent.toPersonal();
        });
        binding.reChangePassword.setOnClickListener(view -> {
            systemSettingPresent.tochangepassword();
        });
        binding.tvLoginOut.setOnClickListener(view -> {
            systemSettingPresent.tologinout(SystemSettingActivity.this);
        });
    }

    @Override
    public void topersonalActivity() {
        Intent intent = new Intent(SystemSettingActivity.this, PersonalActivity.class);
        intent.putExtra(API.register, false);
        startActivity(intent);
    }

    @Override
    public void toChangePassword() {
        Intent intent = new Intent(SystemSettingActivity.this, ChangePasswordActivity.class);
        startActivity(intent);
    }

    @Override
    public void toLoginOut() {
        if (Utils.isFastClick()){
            Utils.snackbar( SystemSettingActivity.this, "点击过快");
            return;
        }
    }
}