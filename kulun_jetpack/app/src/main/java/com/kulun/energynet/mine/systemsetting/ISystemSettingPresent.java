package com.kulun.energynet.mine.systemsetting;

import android.app.Activity;
import android.content.Intent;

public interface ISystemSettingPresent {
    /*
    * 去个人信息
    * */
    void toPersonal();
    /*
    * 去修改密码
    * */
    void tochangepassword();
    /*
    * 退出登录
    * */
    void tologinout(Activity activity);
}
