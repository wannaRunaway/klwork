package com.kulun.energynet.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;

import com.hjq.toast.ToastUtils;
import com.kulun.energynet.login.PasswordLoginActivity;
import com.kulun.energynet.main.MainActivity;
import com.kulun.energynet.main.fragment.MainFragment;
import com.kulun.energynet.model.City;
import com.kulun.energynet.model.ResponseModel;
import com.kulun.energynet.model.StationInfo;
import com.kulun.energynet.model.UseBind;
import com.kulun.energynet.model.User;
import com.kulun.energynet.network.API;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.leefeng.promptlibrary.PromptDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * created by xuedi on 2019/7/31
 */
public class Utils {
    public static final String TAG = "xuedi";

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void snackbar(Activity activity, String message) {
        if (message != null && !message.equals("") && !message.equals("缺少token")) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            int height = display.getHeight();
            ToastUtils.setGravity(Gravity.CENTER, 0, 0);
            ToastUtils.show(message);
        }
    }

    public static void snackbar(String message) {
        if (message != null && !message.equals("") && !message.equals("缺少token")) {
//            Display display = activity.getWindowManager().getDefaultDisplay();
//            int height = display.getHeight();
            ToastUtils.setGravity(Gravity.CENTER, 0, 0);
            ToastUtils.show(message);
        }
    }

    public static void log(String url, String params, String json) {
        Log.d(TAG, "url:" + url + "params:" + params + "json:" + json);
    }

    public static void log(String json) {
        Log.d(TAG, "response:" + json);
    }

    public static boolean isPhone(String phone, Activity activity) {
//        return false;
//        String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
        if (phone.length() != 11) {
            snackbar(activity, "手机号应为11位数");
            return false;
        }
        return true;
//        } else {
//            Pattern p = Pattern.compile(regex);
//            Matcher m = p.matcher(phone);
//            boolean isMatch = m.matches();
//            if (!isMatch) {
//                snackbar(activity, "请填入正确的手机号");
//            }
//            return isMatch;
//        }
    }

    public static boolean teshu(String string) {
        String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(string);
        return m.find();
    }

    public static String formatDateTime(long mss) {
        String DateTimes = null;
        long days = mss / (60 * 60 * 24);
        long hours = (mss % (60 * 60 * 24)) / (60 * 60);
        long minutes = (mss % (60 * 60)) / 60;
        long seconds = mss % 60;
        if (days > 0) {
            DateTimes = days + "天" + hours + "小时" + minutes + "分钟" + seconds + "秒";
        } else if (hours > 0) {
            DateTimes = hours + "小时" + minutes + "分钟" + seconds + "秒";
        } else if (minutes > 0) {
            DateTimes = minutes + "分钟" + seconds + "秒";
        } else {
            DateTimes = seconds + "秒";
        }
        return DateTimes;
    }

    /**
     * 功能：身份证的有效验证
     *
     * @param IDStr 身份证号
     * @return true 有效：false 无效
     * @throws ParseException
     */
    public static boolean IDCardValidate(String IDStr) {
        String[] ValCodeArr = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
        String[] Wi = {"7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2"};
        String Ai = "";
        // ================ 号码的长度18位 ================
        if (IDStr.length() != 18) {
            return false;
        }
        // ================ 数字 除最后以为都为数字 ================
        if (IDStr.length() == 18) {
            Ai = IDStr.substring(0, 17);
        }
        if (isNumeric(Ai) == false) {
            //errorInfo = "身份证15位号码都应为数字 ; 18位号码除最后一位外，都应为数字。";
            return false;
        }
        // ================ 出生年月是否有效 ================
        String strYear = Ai.substring(6, 10);// 年份
        String strMonth = Ai.substring(10, 12);// 月份
        String strDay = Ai.substring(12, 14);// 日
        if (isDate(strYear + "-" + strMonth + "-" + strDay) == false) {
//          errorInfo = "身份证生日无效。";
            return false;
        }
        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150 || (gc.getTime().getTime() - s.parse(strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
                //errorInfo = "身份证生日不在有效范围。";
                return false;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
            //errorInfo = "身份证月份无效";
            return false;
        }
        if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
            //errorInfo = "身份证日期无效";
            return false;
        }
        // ================ 地区码时候有效 ================
        Hashtable h = GetAreaCode();
        if (h.get(Ai.substring(0, 2)) == null) {
            //errorInfo = "身份证地区编码错误。";
            return false;
        }
        // ================ 判断最后一位的值 ================
        int TotalmulAiWi = 0;
        for (int i = 0; i < 17; i++) {
            TotalmulAiWi = TotalmulAiWi + Integer.parseInt(String.valueOf(Ai.charAt(i))) * Integer.parseInt(Wi[i]);
        }
        int modValue = TotalmulAiWi % 11;
        String strVerifyCode = ValCodeArr[modValue];
        Ai = Ai + strVerifyCode;

        if (IDStr.length() == 18) {
            if (Ai.equals(IDStr) == false) {
                //errorInfo = "身份证无效，不是合法的身份证号码";
                return false;
            }
        } else {
            return true;
        }
        return true;
    }

    /**
     * 功能：设置地区编码
     *
     * @return Hashtable 对象
     */
    @SuppressWarnings("unchecked")
    private static Hashtable GetAreaCode() {
        Hashtable hashtable = new Hashtable();
        hashtable.put("11", "北京");
        hashtable.put("12", "天津");
        hashtable.put("13", "河北");
        hashtable.put("14", "山西");
        hashtable.put("15", "内蒙古");
        hashtable.put("21", "辽宁");
        hashtable.put("22", "吉林");
        hashtable.put("23", "黑龙江");
        hashtable.put("31", "上海");
        hashtable.put("32", "江苏");
        hashtable.put("33", "浙江");
        hashtable.put("34", "安徽");
        hashtable.put("35", "福建");
        hashtable.put("36", "江西");
        hashtable.put("37", "山东");
        hashtable.put("41", "河南");
        hashtable.put("42", "湖北");
        hashtable.put("43", "湖南");
        hashtable.put("44", "广东");
        hashtable.put("45", "广西");
        hashtable.put("46", "海南");
        hashtable.put("50", "重庆");
        hashtable.put("51", "四川");
        hashtable.put("52", "贵州");
        hashtable.put("53", "云南");
        hashtable.put("54", "西藏");
        hashtable.put("61", "陕西");
        hashtable.put("62", "甘肃");
        hashtable.put("63", "青海");
        hashtable.put("64", "宁夏");
        hashtable.put("65", "新疆");
        hashtable.put("71", "台湾");
        hashtable.put("81", "香港");
        hashtable.put("82", "澳门");
        hashtable.put("91", "国外");
        return hashtable;
    }

    /**
     * 功能：判断字符串是否为日期格式
     *
     * @return
     */
    public static boolean isDate(String strDate) {
        Pattern pattern = Pattern
                .compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
        Matcher m = pattern.matcher(strDate);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 功能：判断字符串是否为数字
     *
     * @param str
     * @return
     */
    private static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (isNum.matches()) {
            return true;
        } else {
            return false;
        }
    }

    public static void toLogin(Activity activity) {
        Intent intent = new Intent(activity, PasswordLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    /**
     * 判断当前应用是否是debug状态
     */
    public static boolean isApkInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getToken(Activity activity) {
        String token = null;
        if (User.getInstance().getToken() == null || User.getInstance().getToken().equals("")) {
            token = (String) SharePref.get(activity, API.token, "");
        } else {
            token = User.getInstance().getToken();
        }
        return token;
    }

    public static String getToken(Context activity) {
        String token = null;
        if (User.getInstance().getToken() == null || User.getInstance().getToken().equals("")) {
            token = (String) SharePref.get(activity, API.token, "");
        } else {
            token = User.getInstance().getToken();
        }
        return token;
    }

    public static String getUseName(Activity activity) {
        String username = null;
        if (User.getInstance().getName() == null || User.getInstance().getName().equals("")) {
            username = (String) SharePref.get(activity, API.username, "");
        } else {
            username = User.getInstance().getName();
        }
        return username;
    }

    public static double getBalance(Activity activity) {
        double balance = 0;
        if (User.getInstance().getBalance() == 0) {
            balance = (double) SharePref.get(activity, API.balance, "0");
        } else {
            balance = User.getInstance().getBalance();
        }
        return balance;
    }

    public static String getPhone(Activity activity) {
        String phone = null;
        if (User.getInstance().getPhone() == null || User.getInstance().getPhone().equals("")) {
            phone = (String) SharePref.get(activity, API.phone, "");
        } else {
            phone = User.getInstance().getPhone();
        }
        return phone;
    }

    public static String getAccount(Activity activity) {
        String account = null;
        if (User.getInstance().getAccount() == null || User.getInstance().getAccount().equals("")) {
            account = (String) SharePref.get(activity, API.account, "");
        } else {
            account = User.getInstance().getAccount();
        }
        return account;
    }

    public static String getAliyunEndpoint(Activity activity) {
        return "http://" + (String) SharePref.get(activity, API.aliyunEndpoint, "");
    }

    public static String getBucketName(Activity activity) {
        return (String) SharePref.get(activity, API.bucketName, "");
    }

    public static String getStsserver(Activity activity) {
        return "http://" + (String) SharePref.get(activity, API.aliyunStsServer, "");
    }

    public static void userParse(Activity activity) {
        User.getInstance().setIslogin(true);
        Intent intent = new Intent(activity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void shareprefload(User user, Activity activity) {
        if (user != null) {
            SharePref.put(activity, API.balance, user.getBalance() + "");
            SharePref.put(activity, API.phone, user.getPhone());
            SharePref.put(activity, API.username, user.getName());
            SharePref.put(activity, API.account, user.getAccount());
            User.getInstance().setAccount(user.getAccount());
            SharePref.put(activity, API.aliyunEndpoint, user.getAli_oss().getOssEndPoint());
            SharePref.put(activity, API.aliyunStsServer, user.getAli_oss().getStsEndPoint());
            SharePref.put(activity, API.bucketName, user.getAli_oss().getBucketName());
        }
    }

    public static UseBind getusebind(Activity activity) {
        String usebind = (String) SharePref.get(activity, API.usebind, "");
        if (usebind != null && !usebind.equals("")) {
            return Mygson.getInstance().fromJson(usebind, UseBind.class);
        }
        return null;
    }

    public static void setUseBind(Activity activity, UseBind useBind) {
        SharePref.put(activity.getApplicationContext(), API.usebind, Mygson.getInstance().toJson(useBind));
    }

    public static void refreandproUIdismiss(Activity activity, PromptDialog promptDialog, SmartRefreshLayout smartRefreshLayout) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (promptDialog != null) {
                    promptDialog.dismiss();
                }
                if (smartRefreshLayout != null) {
                    smartRefreshLayout.finishRefresh();
                    smartRefreshLayout.finishLoadMore();
                }
            }
        });
    }

    public static void finishRefresh(SmartRefreshLayout smartRefreshLayout){
        if (smartRefreshLayout!=null){
            smartRefreshLayout.finishRefresh();
            smartRefreshLayout.finishLoadMore();
        }
    }

    //循环2次 单个站点循环一次 里面的换电包再循环一次
    public static StationInfo getstation(List<StationInfo> list, int stationId) {
        int position = -1;
        for (int i = 0; i < list.size(); i++) {
            if (stationId == list.get(i).getId()) {
                position = i;
            }
        }
        if (position != -1){
            return list.get(position);
        }
        if (position == -1) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getChildSites() != null && list.get(i).getChildSites().size()>0){
                    if (stationId == list.get(i).getChildSites().get(0).getId()){
                        position = i;
                    }
                }
            }
            if (position != -1){
                return list.get(position).getChildSites().get(0);
            }
        }
        return null;
    }

    public static int getCityPosition(List<City> list, String city) {
        if (list.size() == 0) {
            return -1;
        }
        int position = 0;
        for (int i = 0; i < list.size(); i++) {
            if (city.equals(list.get(i).getName())) {
                position = i;
            }
        }
        return position;
    }

    public static boolean usebindisNotexist(UseBind useBind) {
        if (useBind == null) {
            return true;
        }
        if (useBind.getId() == 0) {
            return true;
        }
        return false;
    }

    public static String getDate(String string) {
        String fromjson = string.replaceAll("年", "-").replaceAll("月", "");
        String start[] = fromjson.split("-");
        if (start[1].length() == 1) {
            start[1] = "0" + start[1];
            return start[0] + "-" + start[1];
        }
        return fromjson;
    }

    public static void loadkefu(Activity activity, View imageView) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + User.getInstance().getCustphone(activity));
        intent.setData(data);
        activity.startActivity(intent);
    }

    /**
     * 把原始字符串分割成指定长度的字符串列表
     *
     * @param inputString 原始字符串
     * @param length      指定长度
     * @return
     */
    public static List<String> getStrList(String inputString, int length) {
        int size = inputString.length() / length;
        if (inputString.length() % length != 0) {
            size += 1;
        }
        return getStrList(inputString, length, size);
    }

    /**
     * 把原始字符串分割成指定长度的字符串列表
     *
     * @param inputString 原始字符串
     * @param length      指定长度
     * @param size        指定列表大小
     * @return
     */
    public static List<String> getStrList(String inputString, int length,
                                          int size) {
        List<String> list = new ArrayList<String>();
        for (int index = 0; index < size; index++) {
            String childStr = substring(inputString, index * length,
                    (index + 1) * length);
            list.add(childStr);
        }
        return list;
    }

    /**
     * 分割字符串，如果开始位置大于字符串长度，返回空
     *
     * @param str 原始字符串
     * @param f   开始位置
     * @param t   结束位置
     * @return
     */
    public static String substring(String str, int f, int t) {
        if (f > str.length())
            return null;
        if (t > str.length()) {
            return str.substring(f, str.length());
        } else {
            return str.substring(f, t);
        }
    }

    // 两次点击间隔不能少于1000ms
    private static final int FAST_CLICK_DELAY_TIME = 5000;
    private static long lastClickTime;

    public static boolean isFastClick() {
        boolean flag = true;
        if ((System.currentTimeMillis() - lastClickTime) >= FAST_CLICK_DELAY_TIME) {
            flag = false;
        }
        lastClickTime = System.currentTimeMillis();
        return flag;
    }

    public static RequestBody body(String string) {
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), string);
    }

    public static int responseCode(ResponseModel responseModel, Activity activity){
        if (responseModel.getCode()!=0){
            snackbar(activity, responseModel.getMessage());
        }
        return responseModel.getCode();
    }
}
