package com.kulun.energynet.mine;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kulun.energynet.R;
import com.kulun.energynet.databinding.ActivityChangePasswordBinding;
import com.kulun.energynet.main.BaseActivity;
import com.kulun.energynet.model.ResponseModel;
import com.kulun.energynet.model.UseBind;
import com.kulun.energynet.model.User;
import com.kulun.energynet.network.InternetWorkManager;
import com.kulun.energynet.network.MyObserver;
import com.kulun.energynet.network.RxHelper;
import com.kulun.energynet.requestbody.ChangepasswordRequest;
import com.kulun.energynet.network.API;
import com.kulun.energynet.utils.MD5;
import com.kulun.energynet.utils.Mygson;
import com.kulun.energynet.utils.Utils;

import java.util.HashMap;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * created by xuedi on 2019/8/28
 */
public class ChangePasswordActivity extends BaseActivity {
    private ActivityChangePasswordBinding binding;

    @Override
    public void initView(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password);
        binding.imgClose.setOnClickListener(view -> finish());
        binding.tvConfrim.setOnClickListener(view -> {
            String oldpassword = binding.etPasswordOld.getText().toString();
            String newpassword = binding.etPasswordNew.getText().toString();
            String confirmpassword = binding.etPasswordConfirm.getText().toString();
            if (oldpassword.isEmpty()) {
                Utils.snackbar( ChangePasswordActivity.this, "请输入原密码");
                return;
            }
            if (newpassword.isEmpty()) {
                Utils.snackbar(ChangePasswordActivity.this, "请输入新密码");
                return;
            }
            if (confirmpassword.isEmpty()) {
                Utils.snackbar( ChangePasswordActivity.this, "请再次输入新密码");
                return;
            }
            if (newpassword.length() < 6 || confirmpassword.length() < 6) {
                Utils.snackbar(ChangePasswordActivity.this, "密码不能少于6位");
                return;
            }
            if (!newpassword.equals(confirmpassword)) {
                Utils.snackbar( ChangePasswordActivity.this, "两次密码输入不一致");
                return;
            }
            if (Utils.teshu(newpassword)) {
                Utils.snackbar( ChangePasswordActivity.this, "密码含有特殊字符");
                return;
            }
            changePassword(newpassword, oldpassword);
        });
    }

  //{
  //    "password": "123456",
  //    "new_password": "000000"
  //}
    private void changePassword(String newpassword, String oldpassword) {
        ChangepasswordRequest changepasswordRequest = new ChangepasswordRequest();
        changepasswordRequest.setPassword(MD5.encode(oldpassword));
        changepasswordRequest.setNew_password(MD5.encode(newpassword));
        InternetWorkManager.getRequest().changepassword(Utils.body(Mygson.getInstance().toJson(changepasswordRequest)))
                .compose(RxHelper.observableIOMain(this))
                .subscribe(new MyObserver<User>() {
                    @Override
                    public void onSuccess(User data) {
                        Utils.snackbar(ChangePasswordActivity.this, "密码修改成功");
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
                        Utils.toLogin(ChangePasswordActivity.this);
                    }
                });
//        HashMap<String,String> map = new HashMap<>();
//        map.put("password", MD5.encode(oldpassword));
//        map.put("new_password", MD5.encode(newpassword));
//        new MyRequest().myRequest(API.changePassword,true, map,true,  this, null, null, new Response() {
//            @Override
//            public void response(JsonObject json, JsonArray jsonArray,boolean isNull) {
//                Utils.snackbar(ChangePasswordActivity.this, "密码修改成功");
//                finish();
//            }
//        });
    }
}
