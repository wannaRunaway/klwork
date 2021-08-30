package com.kulun.energynet.mine.systemsetting;

import android.app.Activity;
import android.content.Intent;

import com.kulun.energynet.login.PasswordLoginActivity;
import com.kulun.energynet.model.User;
import com.kulun.energynet.network.API;
import com.kulun.energynet.network.InternetWorkManager;
import com.kulun.energynet.network.MyObserver;
import com.kulun.energynet.network.RxHelper;
import com.kulun.energynet.utils.SharePref;
import com.kulun.energynet.utils.Utils;

public class SystemSettingModel implements ISystemSettingModel{
//    private ISystemSettingPresent iSystemSettingPresent;
//    public SystemSettingModel(ISystemSettingPresent iSystemSettingPresent){
//        this.iSystemSettingPresent = iSystemSettingPresent;
//    }
    @Override
    public void loginout(Activity activity) {
        InternetWorkManager.getRequest().logout()
                .compose(RxHelper.observableIOMain(activity))
                .subscribe(new MyObserver<User>() {
                    @Override
                    public void onSuccess(User data) {
                        Intent intent = new Intent(activity, PasswordLoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        User.getInstance().setToken("");
                        SharePref.put(activity, API.token, "");
                        SharePref.clear(activity);
                        activity.startActivity(intent);
                        activity.finish();
                    }

                    @Override
                    public void onFail(int code, String message) {

                    }

                    @Override
                    public void onError() {

                    }

                    @Override
                    public void onClearToken() {
                        Utils.toLogin(activity);
                    }
                });
    }
}
