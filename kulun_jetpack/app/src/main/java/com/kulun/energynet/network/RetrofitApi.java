package com.kulun.energynet.network;
//import io.reactivex.rxjava3.core.Observable;
import com.kulun.energynet.model.Activity;
import com.kulun.energynet.model.ActivityDetail;
import com.kulun.energynet.model.Appload;
import com.kulun.energynet.model.Bill;
import com.kulun.energynet.model.BillDetail;
import com.kulun.energynet.model.BilldetailModel;
import com.kulun.energynet.model.Car;
import com.kulun.energynet.model.CarApply;
import com.kulun.energynet.model.City;
import com.kulun.energynet.model.ConsumeListModel;
import com.kulun.energynet.model.Coupon;
import com.kulun.energynet.model.CurrentAppointmentModel;
import com.kulun.energynet.model.Daka;
import com.kulun.energynet.model.Message;
import com.kulun.energynet.model.OssTokenModel;
import com.kulun.energynet.model.QuestionshowModel;
import com.kulun.energynet.model.Reserver;
import com.kulun.energynet.model.ResponseModel;
import com.kulun.energynet.model.StationAll;
import com.kulun.energynet.model.StationInfo;
import com.kulun.energynet.model.TboxModel;
import com.kulun.energynet.model.User;
import com.kulun.energynet.model.WxPayModel;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitApi {

    @POST("user/login")//登录
    Observable<ResponseModel<User>> loginPassword(@Body RequestBody requestBody);

    @POST("user/profile")//上传个人信息
    Observable<ResponseModel<User>> infoPost(@Body RequestBody requestBody);

    @GET("user/profile")//获取个人信息
    Observable<ResponseModel<User>> infoGet();

    @POST("send_sms")//验证码
    Observable<ResponseModel<User>> sms(@Body RequestBody requestBody);

    @POST("user/register")//注册
    Observable<ResponseModel<User>> register(@Body RequestBody requestBody);

    @POST("site/list")//站点列表
    Observable<ResponseModel<List<StationInfo>>> sitelist(@Body RequestBody requestBody);

    @GET("site/all")//除了当前城市之前，全国站点
    Observable<ResponseModel<List<StationAll>>> sitelistall(@Query("exclude")int cityid);

    @POST("site/near")//一键预约
    Observable<ResponseModel<StationInfo>> reservation(@Body RequestBody requestBody);

    @GET("area/open")//城市列表
    Observable<ResponseModel<List<City>>> citylist();

    @POST("user/logout")//登出app
    Observable<ResponseModel<User>> logout();

    @POST("password/edit")//修改密码
    Observable<ResponseModel<User>> changepassword(@Body RequestBody requestBody);

    @POST("password/reset")//重置密码
    Observable<ResponseModel<User>> resetpassword(@Body RequestBody requestBody);

    @GET("car/tbox")//获取tbox
    Observable<ResponseModel<TboxModel>> gettbox(@Query("plate")String plates);

    @POST("car/bind")//绑定车辆上传
    Observable<ResponseModel<User>> carbindupload(@Body RequestBody requestBody);

    @GET("oss/token")//阿里云上传
    Observable<ResponseModel<OssTokenModel>> getalioss();

    @GET("car/list")//我的车辆
    Observable<ResponseModel<List<Car>>> carlist();

    @POST("car/inuse")//设为当前车辆
    Observable<ResponseModel<User>> setcurrentCar(@Body RequestBody requestBody);

    @POST("car/unbind")//解绑当前车辆
    Observable<ResponseModel<User>> unbindCar(@Body RequestBody requestBody);

    @GET("car/apply")//申请绑定列表
    Observable<ResponseModel<List<CarApply>>> carapplyList();

    @POST("refund/apply")//申请退款
    Observable<ResponseModel<User>> refundApply(@Body RequestBody requestBody);

    @GET("refund/amount")//获取退款金额数目
    Observable<ResponseModel<Double>> refundmoney();

    @POST("bill/comment")//提交星星
    Observable<ResponseModel<User>> startUpload(@Body RequestBody requestBody);

    @POST("bill/question/detail")//自费疑问展示
    Observable<ResponseModel<QuestionshowModel>> questionshow(@Body RequestBody requestBody);

    @POST("bill/question")//资费疑问上传
    Observable<ResponseModel<User>> questionUpload(@Body RequestBody requestBody);

    @POST("exchange/consume")//扫码换电获取列表
    Observable<ResponseModel<ConsumeListModel>> consumegetlist(@Body RequestBody requestBody);

    @POST("activity/package/buy")//购买套餐
    Observable<ResponseModel<User>> buypackage(@Body RequestBody requestBody);

    @GET("bill/detail")//账单详情
    Observable<ResponseModel<BilldetailModel>> billDetail(@Query("bid")int bid, @Query("cType")int ctype);

    @GET("bill/list")//账单列表
    Observable<ResponseModel<List<Bill>>> billlist(@Query("type")String type, @Query("page")String page);

    @GET("bill/list")//billlist with time
    Observable<ResponseModel<List<Bill>>> billlist(@Query("type")String type, @Query("page")String page, @Query("month")String time);

    @POST("coupon/list")//couponlist
    Observable<ResponseModel<List<Coupon>>> couponList(@Body RequestBody requestBody);

    @POST("driver/clock")//driver's clocking
    Observable<ResponseModel<User>> clock(@Body RequestBody requestBody);

    @POST("driver/clock/list")//driver's clocking list
    Observable<ResponseModel<List<Daka>>> clocklist(@Body RequestBody requestBody);

    @GET("book/get")//get current appointment
    Observable<ResponseModel<CurrentAppointmentModel>> currentAppointment();

    @POST("book/add")//add appointment
    Observable<ResponseModel<User>> addAppointment(@Body RequestBody requestBody);

    @POST("book/cancel")//cancel appointment
    Observable<ResponseModel<User>> cancelAppointment(@Body RequestBody requestBody);

    @POST("book/delay")//delay appointment
    Observable<ResponseModel<User>> delayAppointment(@Body RequestBody requestBody);

    @GET("book/list")//appointment list
    Observable<ResponseModel<List<Reserver>>> appointmentList();

    @POST("activity/list")//activity list
    Observable<ResponseModel<List<Activity>>> activityList();

    @POST("activity/package/detail")//package detail
    Observable<ResponseModel<ActivityDetail>> packagedetail(@Body RequestBody requestBody);

    @GET("load/list")//app info load
    Observable<ResponseModel<List<Appload>>> appinfo();

    @GET("message/list")//message list
    Observable<ResponseModel<List<Message>>> messageList(@Query("type")int type, @Query("page")int page, @Query("size")int size);//"type=1&page="+pageNo+"&size=20";

    @POST("pay/order")// alipay
    Observable<ResponseModel<String>> alipay(@Body RequestBody requestBody);

    @POST("pay/order")// alipay
    Observable<ResponseModel<WxPayModel>> weichatpay(@Body RequestBody requestBody);
 }
