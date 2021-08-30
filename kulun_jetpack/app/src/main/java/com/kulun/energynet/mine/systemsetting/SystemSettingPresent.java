package com.kulun.energynet.mine.systemsetting;

import android.app.Activity;
import android.content.Intent;

public class SystemSettingPresent implements ISystemSettingPresent{
    private ISystemSettingView iSystemSettingView;
    private ISystemSettingModel iSystemSettingModel;
    public SystemSettingPresent(ISystemSettingView iSystemSettingView){
        this.iSystemSettingView = iSystemSettingView;
        iSystemSettingModel = new SystemSettingModel();
    }
    @Override
    public void toPersonal() {
        iSystemSettingView.topersonalActivity();
    }

    @Override
    public void tochangepassword() {
        iSystemSettingView.toChangePassword();
    }

    @Override
    public void tologinout(Activity activity) {
        iSystemSettingView.toLoginOut();
        iSystemSettingModel.loginout(activity);
    }
}
