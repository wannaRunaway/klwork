package com.kulun.energynet.model;

import android.app.Activity;

import com.kulun.energynet.network.API;
import com.kulun.energynet.utils.SharePref;

import java.io.Serializable;

public class User implements Serializable {
    /*
		"account": "202101191051280",
		"name": "范严",
		"phone": "15990068247",
		"balance": 8170,
		"canRefund": true,
		"identity": "340******521",
		"unread": 0,
		"token": "",
		"ali_oss": {
			"OssEndPoint": "oss-cn-shanghai.aliyuncs.com",
			"StsEndPoint": "sts.cn-shanghai.aliyuncs.com",
			"BucketName": "app-back-98039b9ced38"
		}
}
    * */
    private volatile static User user;

    public static User getInstance() {
        if (null == user) {
            synchronized (User.class) {
                if (null == user) {
                    user = new User();
                }
            }
        }
        return user;
    }

    public String phone, token = "", name, account;
    public double balance;
    public boolean islogin = false;
    private double mylatitude, mylongtitude, longtitude, latitude;
    public String cityName;
    public StationInfo stationInfo;
    public int id, usebind, unread, cityid;
    public UseBind use_bind;
    public Alioss ali_oss;
    public int yuyueId = -1, accountId;
    private String custphone, custinfo, appointment_no, identity;
    private boolean canRefund;
    private boolean mainchanged, listchanged;

//    public int getMycurrentpage() {
//        return mycurrentpage;
//    }
//
//    public void setMycurrentpage(int mycurrentpage) {
//        this.mycurrentpage = mycurrentpage;
//    }

    public boolean isMainchanged() {
        return mainchanged;
    }

    public void setMainchanged(boolean mainchanged) {
        this.mainchanged = mainchanged;
    }

    public boolean isListchanged() {
        return listchanged;
    }

    public void setListchanged(boolean listchanged) {
        this.listchanged = listchanged;
    }

    public boolean isCanRefund() {
        return canRefund;
    }

    public void setCanRefund(boolean canRefund) {
        this.canRefund = canRefund;
    }

    public int getCityid(Activity activity) {
        if (cityid == 0){
            String s = (String) SharePref.get(activity,API.cityId,"0");
            return Integer.parseInt(s);
        }
        return cityid;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public void setCityid(int cityid) {
        this.cityid = cityid;
    }

    public double getMylatitude(Activity activity) {
        if (mylatitude==0){
            String s = (String) SharePref.get(activity,API.mylatitude,"0");
            return Double.parseDouble(s);
        }
        return mylatitude;
    }

    public void setMylatitude(double mylatitude) {
        this.mylatitude = mylatitude;
    }

    public double getMylongtitude(Activity activity) {
        if (mylongtitude==0){
            String s = (String) SharePref.get(activity,API.mylongtitude,"0");
            return Double.parseDouble(s);
        }
        return mylongtitude;
    }

    public void setMylongtitude(double mylongtitude) {
        this.mylongtitude = mylongtitude;
    }

    public String getAppointment_no(Activity activity) {
        if (appointment_no == null || appointment_no.equals("")) {
            return (String) SharePref.get(activity, API.appointment_no, "");
        }
        return appointment_no;
    }

    public void setAppointment_no(String appointment_no) {
        this.appointment_no = appointment_no;
    }

    public String getCustphone(Activity activity) {
        if (custphone == null || custphone.equals("")) {
            return (String) SharePref.get(activity, API.customphone, "");
        }
        return custphone;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public void setCustphone(String custphone) {
        this.custphone = custphone;
    }

    public String getCustinfo(Activity activity) {
        if (custinfo == null || custinfo.equals("")) {
            return (String) SharePref.get(activity, API.custominfo, "");
        }
        return custinfo;
    }

    public void setCustinfo(String custinfo) {
        this.custinfo = custinfo;
    }

    public int getYuyueId() {
        return yuyueId;
    }

    public void setYuyueId(int yuyueId) {
        this.yuyueId = yuyueId;
    }

    public Alioss getAli_oss() {
        return ali_oss;
    }

    public void setAli_oss(Alioss ali_oss) {
        this.ali_oss = ali_oss;
    }

    public UseBind getUse_bind() {
        return use_bind;
    }

    public void setUse_bind(UseBind use_bind) {
        this.use_bind = use_bind;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude(Activity activity) {
        if (latitude == 0) {
            String string = (String) SharePref.get(activity, API.latitude, "0");
            return Double.parseDouble(string);
        }
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtitude(Activity activity) {
        if (longtitude == 0) {
            String string = (String) SharePref.get(activity, API.longtitude, "0");
            return Double.parseDouble(string);
        }
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public String getCityName(Activity activity) {
        if (cityName == null){
            cityName = (String) SharePref.get(activity,API.cityName,"");
            return cityName;
        }
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean isIslogin() {
        return islogin;
    }

    public void setIslogin(boolean islogin) {
        this.islogin = islogin;
    }

    public StationInfo getStationInfo() {
        return stationInfo;
    }

    public void setStationInfo(StationInfo stationInfo) {
        this.stationInfo = stationInfo;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getUsebind() {
        return usebind;
    }

    public void setUsebind(int usebind) {
        this.usebind = usebind;
    }

    public class Alioss implements Serializable {
        //{"OssEndPoint":"oss-cn-shanghai.aliyuncs.com","StsEndPoint":"sts.cn-shanghai.aliyuncs.com","BucketName":"app-back-98039b9ced38"}}
        private String OssEndPoint, StsEndPoint, BucketName;

        public String getOssEndPoint() {
            return OssEndPoint;
        }

        public void setOssEndPoint(String ossEndPoint) {
            OssEndPoint = ossEndPoint;
        }

        public String getStsEndPoint() {
            return StsEndPoint;
        }

        public void setStsEndPoint(String stsEndPoint) {
            StsEndPoint = stsEndPoint;
        }

        public String getBucketName() {
            return BucketName;
        }

        public void setBucketName(String bucketName) {
            BucketName = bucketName;
        }
    }
}
