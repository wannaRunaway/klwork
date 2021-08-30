package com.kulun.energynet.login;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import androidx.core.content.ContextCompat;

import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.kulun.energynet.R;
import com.kulun.energynet.databinding.ActivityLoginPasswordBinding;
import com.kulun.energynet.main.BaseActivity;
import com.kulun.energynet.main.MainActivity;
import com.kulun.energynet.main.fragment.MainFragment;
import com.kulun.energynet.model.User;
import com.kulun.energynet.network.API;
import com.kulun.energynet.network.InternetWorkManager;
import com.kulun.energynet.network.MyObserver;
import com.kulun.energynet.network.RxHelper;
import com.kulun.energynet.requestbody.LoginPasswordRequest;
import com.kulun.energynet.utils.Mygson;
import com.kulun.energynet.utils.MD5;
import com.kulun.energynet.utils.SharePref;
import com.kulun.energynet.utils.Utils;

/**
 * created by xuedi on 2019/8/6
 */
public class PasswordLoginActivity extends BaseActivity implements View.OnClickListener {
    private ActivityLoginPasswordBinding binding;

    @Override
    public void initView(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login_password);
        binding.imgClose.setOnClickListener(this);
        binding.imgLogin.setOnClickListener(this);
        binding.imgRegister.setOnClickListener(this);
        binding.tvLoginCode.setOnClickListener(this);
        binding.tvForgetPassword.setOnClickListener(this);
        requestMapPermissions();
        initEdittext();
    }

    private void initEdittext() {//初始化输入框
        String phone = (String) SharePref.get(PasswordLoginActivity.this, API.phone, "");
        String password = (String) SharePref.get(PasswordLoginActivity.this, API.password, "");
        if (!phone.equals("")) {
            binding.etPhone.setText(phone);
        }
        if (!password.equals("")) {
            binding.etPassword.setText(password);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intents = new Intent(PasswordLoginActivity.this, MainActivity.class);
        intents.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intents);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_close:
                Intent intents = new Intent(PasswordLoginActivity.this, MainActivity.class);
                intents.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intents);
                finish();
                break;
            case R.id.img_login:
                String phone = binding.etPhone.getText().toString();
                String code = binding.etPassword.getText().toString();
                if (TextUtils.isEmpty(phone)) {
                    Utils.snackbar(PasswordLoginActivity.this, "手机号码不能为空");
                    return;
                }
                if (!Utils.isPhone(phone, PasswordLoginActivity.this)) {
                    return;
                }
                if (TextUtils.isEmpty(code)) {
                    Utils.snackbar(PasswordLoginActivity.this, "密码不能为空");
                    return;
                }
                login(phone, code);
                break;
            case R.id.img_register:
                Intent intent = new Intent(PasswordLoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_login_code:
                Intent intent1 = new Intent(PasswordLoginActivity.this, VeriCodeLoginActivity.class);
                startActivity(intent1);
                break;
            case R.id.tv_forget_password:
                String tel = binding.etPhone.getText().toString();
                Intent intent2 = new Intent(PasswordLoginActivity.this, ForgetPasswordActivity.class);
                intent2.putExtra("tel", tel);
                startActivity(intent2);
                break;
            default:
                break;
        }
    }

    private void login(String phone, String code) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LoginPasswordRequest loginBody = new LoginPasswordRequest();
        loginBody.setPhone(phone);
        loginBody.setPassword(MD5.encode(code));
        InternetWorkManager.getRequest().loginPassword(Utils.body(Mygson.getInstance().toJson(loginBody)))
                .compose(RxHelper.observableIOMain(this))
                .subscribe(new MyObserver<User>() {
                    @Override
                    public void onSuccess(User data) {
                        if (data != null) {
                            SharePref.put(PasswordLoginActivity.this, API.phone, phone);
                            SharePref.put(PasswordLoginActivity.this, API.password, code);
                        }
                        Utils.userParse(PasswordLoginActivity.this);
                    }

                    @Override
                    public void onFail(int code, String message) {

                    }

                    @Override
                    public void onError() {

                    }

                    @Override
                    public void onClearToken() {
                        Utils.toLogin(PasswordLoginActivity.this);
                    }
                });
//        HashMap<String,String> hashMap = new HashMap<>();
//        hashMap.put("phone", phone);
//        hashMap.put("password", MD5.encode(code));
//        new MyRequest().myRequest(API.LOGIN, true,hashMap,true, this, null, null, new Response() {
//            @Override
//            public void response(JsonObject json, JsonArray jsonArray,boolean isNull) {
//                User user = Mygson.getInstance().fromJson(json, User.class);
//                if (user != null) {
//                    SharePref.put(PasswordLoginActivity.this, API.phone, phone);
//                    SharePref.put(PasswordLoginActivity.this, API.password, code);
//                }
//                Utils.userParse(json, PasswordLoginActivity.this);
//            }
//        });
    }

    private void requestMapPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
        }
    }
}
