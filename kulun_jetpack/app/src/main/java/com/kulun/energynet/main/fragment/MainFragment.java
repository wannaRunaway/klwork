package com.kulun.energynet.main.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.AmapPageType;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.kulun.energynet.R;
import com.kulun.energynet.bill.BillActivity;
import com.kulun.energynet.customizeView.PromotionCarSelect;
//import com.kulun.energynet.customizeView.ShakeListener;
import com.kulun.energynet.databinding.StationBaoAllPopupBinding;
import com.kulun.energynet.main.StationSelectActivity;
import com.kulun.energynet.model.ResponseModel;
import com.kulun.energynet.popup.StationBaoAllPopup;
import com.kulun.energynet.popup.StationOtherCitiesPopup;
import com.kulun.energynet.popup.StationPopup;
import com.kulun.energynet.customizeView.TimeFinishInterface;
import com.kulun.energynet.databinding.FragmentMainBinding;
import com.kulun.energynet.login.PrivacyActivity;
import com.kulun.energynet.login.UseProtocolActivity;
import com.kulun.energynet.main.ChoseCityActivity;
import com.kulun.energynet.main.TimeCount;
import com.kulun.energynet.main.UpdateManager;
import com.kulun.energynet.mine.DriverclockActivity;
import com.kulun.energynet.mine.MapCustomActivity;
import com.kulun.energynet.mine.MessageActivity;
import com.kulun.energynet.mine.MyCarActivity;
import com.kulun.energynet.mine.PackageListActivity;
import com.kulun.energynet.mine.RechargeActivity;
import com.kulun.energynet.mine.ScanActivity;
import com.kulun.energynet.model.Appload;
import com.kulun.energynet.model.City;
import com.kulun.energynet.model.ConsumeListModel;
import com.kulun.energynet.model.CurrentAppointmentModel;
import com.kulun.energynet.model.Message;
import com.kulun.energynet.model.StationAll;
import com.kulun.energynet.model.StationInfo;
import com.kulun.energynet.model.UseBind;
import com.kulun.energynet.model.User;
import com.kulun.energynet.network.InternetWorkManager;
import com.kulun.energynet.network.MyObserver;
import com.kulun.energynet.network.RxHelper;
import com.kulun.energynet.popup.ScanPopup;
import com.kulun.energynet.requestbody.AddAppointmentRequest;
import com.kulun.energynet.requestbody.CancelAppointmentRequest;
import com.kulun.energynet.requestbody.ConsumeRequest;
import com.kulun.energynet.requestbody.ReservationRequest;
import com.kulun.energynet.requestbody.SiteRequest;
import com.kulun.energynet.network.API;
import com.kulun.energynet.utils.BaseDialog;
import com.kulun.energynet.utils.DateUtils;
import com.kulun.energynet.utils.GpsUtil;
import com.kulun.energynet.utils.Mygson;
import com.kulun.energynet.utils.ListUtils;
import com.kulun.energynet.utils.SharePref;
import com.kulun.energynet.utils.Utils;
import com.superluo.textbannerlibrary.ITextBannerItemClickListener;
import com.trello.rxlifecycle4.components.support.RxFragment;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

import static com.yanzhenjie.permission.runtime.Permission.ACCESS_FINE_LOCATION;
import static com.yanzhenjie.permission.runtime.Permission.CAMERA;
import static com.yanzhenjie.permission.runtime.Permission.WRITE_EXTERNAL_STORAGE;

public class MainFragment extends RxFragment implements View.OnClickListener, PromotionCarSelect, TimeFinishInterface {
    private PrivacyDialog privacyDialog;
    private FragmentMainBinding binding;
    private UseBind useBind;
    public AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mLocationOption = null;
    private AMap aMap;
    private List<StationInfo> stationlist = new ArrayList<>();
    private List<Marker> markerList = new ArrayList<>();
    private final int REQUESTCODE_MAIN = 64, REQUESTCODE_SCAN = 1003, REQUESTCODE_GPS = 1010;
    private TimeCount timeCount;
    private GeocodeSearch geocodeSearch;
    private UpdateManager updateManager;
    private static BitmapDescriptor bitmap = null;
    private StationPopup stationPopup;
    private StationOtherCitiesPopup stationOtherCitiesPopup;
    private StationBaoAllPopup stationBaoAllPopup;
    private String appointment_no;
    private ScanPopup scanPopup;
    private int cityid;
    private List<StationAll> stationAllList = new ArrayList<>();
    private MainFragment mainFragment;
    private StationSelectActivity stationSelectActivity;
    public AMapLocationListener mLocationListener = new AMapLocationListener() {//
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    if (TextUtils.isEmpty(amapLocation.getCity())) {
                        //有些机型上地位获取到的城市等信息为空，需要根据经纬度查找城市
                        getAddressByLatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                    } else {
                        if (amapLocation.getLatitude() != 0 && amapLocation.getLongitude() != 0) {
                            locateInit(amapLocation.getLatitude(), amapLocation.getLongitude());
                        }
                        if (!amapLocation.getCity().isEmpty()) {
                            User.getInstance().setCityName(amapLocation.getCity());
                            SharePref.put(getActivity(), API.cityName, amapLocation.getCity());
                            binding.tvCity.setText(amapLocation.getCity());
                        }
                        Utils.log(null, "", amapLocation.getCity() + "定位地址" + amapLocation.getCityCode() + amapLocation.getErrorCode() + amapLocation.getErrorInfo() + amapLocation.getLatitude() + amapLocation.getLongitude());
                        refreshMap(User.getInstance().getLatitude(getActivity()), User.getInstance().getLongtitude(getActivity()));
                        loadCity(User.getInstance().getLatitude(getActivity()), User.getInstance().getLongtitude(getActivity()), binding.tvCity.getText().toString());
                    }
                } else {
                    User.getInstance().setCityName("");
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.d(Utils.TAG, "location Error, ErdrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }
            }
        }
    };

    private void loadCity(double latitude, double longtitude, String city) {
        InternetWorkManager.getRequest().citylist()
                .compose(RxHelper.observableIOMain(getActivity()))
                .subscribe(new MyObserver<List<City>>() {
                    @Override
                    public void onSuccess(List<City> data) {
                        int position = Utils.getCityPosition(data, city);
                        if (position != -1) {
                            cityid = data.get(position).getId();
                            User.getInstance().setCityid(cityid);
                            SharePref.put(getActivity(), API.cityId, cityid + "");
                            loadStation(longtitude, latitude, -1, cityid);
                        }
                    }

                    @Override
                    public void onFail(int code, String message) {

                    }

                    @Override
                    public void onError() {

                    }

                    @Override
                    public void onClearToken() {
                        Utils.toLogin(getActivity());
                    }
                });
//        new MyRequest().myRequest(API.CITYLIST, false, null, false, getActivity(), null, null, new Response() {
//            @Override
//            public void response(JsonObject json, JsonArray jsonArray, boolean isNull) {
//                List<City> cityList = Mygson.getInstance().fromJson(jsonArray, new TypeToken<List<City>>() {
//                }.getType());
//                int position = Utils.getCityPosition(cityList, city);
//                if (position != -1) {
//                    cityid = cityList.get(position).getId();
//                    User.getInstance().setCityid(cityid);
//                    SharePref.put(MainActivity.getActivity(), API.cityId, cityid + "");
//                    loadStation(longtitude, latitude, -1, cityid);
//                }
//            }
//        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, null);
        binding = DataBindingUtil.bind(view);
        appload();
        if (!(boolean) SharePref.oneget(getActivity(), "agree", false)) {
            if (privacyDialog == null) {
                privacyDialog = new PrivacyDialog(getActivity(), R.style.mydialog);
                privacyDialog.show();
            }
        }
        binding.mapview.onCreate(savedInstanceState);
        aMap = binding.mapview.getMap();
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.setOnMarkerClickListener(markerClickListener);
        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                float a = cameraPosition.zoom;
                Utils.log("", "", a + "");
                if (a <= 9 && stationAllList.size() == 0) {
                    loadAllstation();
                }
            }
        });
        mLocationClient = new AMapLocationClient(getContext());
        init();
        binding.finish.setOnClickListener(v -> {
            reservation();
        });
        binding.image2.setOnClickListener(v -> binding.message.setVisibility(View.GONE));
        binding.banner.setItemOnClickListener(new ITextBannerItemClickListener() {
            @Override
            public void onItemClick(String data, int position) {
                Intent intent = new Intent(getActivity(), MessageActivity.class);
                startActivity(intent);
            }
        });
        stationPopup = new StationPopup(getActivity(), binding.cardview);
        stationOtherCitiesPopup = new StationOtherCitiesPopup(getActivity(), binding.cardview);
        stationBaoAllPopup = new StationBaoAllPopup(getContext());
        mainFragment = this;
        return view;
    }

    public void reservation() {//一键预约
        if (Utils.getToken(getActivity()) == null || Utils.getToken(getActivity()).equals("")) {
            Utils.toLogin(getActivity());
            Utils.snackbar(getActivity(), "请先登录");
            return;
        }
        if (stationlist.size() == 0) {
            Utils.snackbar(getActivity(), "请等待站点刷新");
            return;
        }
        if (Utils.usebindisNotexist(useBind)) {
            Utils.snackbar(getActivity(), "请选择当前车辆");
            return;
        }
        ReservationRequest reservationRequest = new ReservationRequest();
        if (User.getInstance().getMylatitude(getActivity()) == 0) {//当前没有定位到
            reservationRequest.setLongitude(User.getInstance().getLongtitude(getActivity()));
            reservationRequest.setLatitude(User.getInstance().getLatitude(getActivity()));
        } else {
            reservationRequest.setLongitude(User.getInstance().getMylongtitude(getActivity()));
            reservationRequest.setLatitude(User.getInstance().getMylatitude(getActivity()));
        }
        reservationRequest.setBattery_cnt(useBind.getBattery_count());
        reservationRequest.setCity_id(User.getInstance().getCityid(getActivity()));
        InternetWorkManager.getRequest()
                .reservation(Utils.body(Mygson.getInstance().toJson(reservationRequest)))
                .compose(RxHelper.observableIOMain(getActivity()))
                .subscribe(new MyObserver<StationInfo>() {
                    @Override
                    public void onSuccess(StationInfo stationInfo) {
                        showPopDialog(stationInfo);
                        if (stationPopup != null) {
                            stationPopup.setStation(stationInfo);
                        }
                    }

                    @Override
                    public void onFail(int code, String message) {

                    }

                    @Override
                    public void onError() {

                    }

                    @Override
                    public void onClearToken() {
                        Utils.toLogin(getActivity());
                    }
                });
    }

    private void loadAllstation() {//加载全部站点 /api/site/all?exclude=320200   全国站点
        InternetWorkManager.getRequest().sitelistall(cityid)
                .compose(RxHelper.observableIOMain(getActivity()))
                .subscribe(new MyObserver<List<StationAll>>() {
                    @Override
                    public void onSuccess(List<StationAll> data) {
                        stationAllList.addAll(data);
                        markerAllCreate(stationAllList, null);
                    }

                    @Override
                    public void onFail(int code, String message) {

                    }

                    @Override
                    public void onError() {

                    }

                    @Override
                    public void onClearToken() {
                        Utils.toLogin(getActivity());
                    }
                });
//        String json = "exclude=" + cityid;
//        new MyRequest().spliceJson(API.siteall, false, json, getActivity(), null, null, new Response() {
//            @Override
//            public void response(JsonObject json, JsonArray jsonArray, boolean isNull) {
//                if (jsonArray != null) {
//                    stationAllList.addAll(Mygson.getInstance().fromJson(jsonArray, new TypeToken<List<StationAll>>() {
//                    }.getType()));
//                    markerAllCreate(stationAllList, null);
//                }
//            }
//        });
    }

    //app加载信息
    private void appload() {
        InternetWorkManager.getRequest().appinfo()
                .compose(RxHelper.observableIOMain(getActivity()))
                .subscribe(new MyObserver<List<Appload>>() {
                    @Override
                    public void onSuccess(List<Appload> data) {
                        List<Appload> list = data;
                        String workTime = "", workDay = "", url = "", latestversion = "";
                        boolean isforce = false;
                        for (int i = 0; i < list.size(); i++) {
                            Appload appload = list.get(i);
                            if (appload.getName().equals("serviceLine") && !appload.getRemark().equals("")) {
                                String phone = appload.getRemark();
                                User.getInstance().setCustphone(phone);
                                SharePref.put(getActivity(), API.customphone, phone);
                            }
                            if (appload.getName().equals("workDay") && !appload.getRemark().equals("")) {
                                workDay = appload.getRemark();
                            }
                            if (appload.getName().equals("workTime") && !appload.getRemark().equals("")) {
                                workTime = appload.getRemark();
                            }
                            if (appload.getName().equals("androidLink") && !appload.getRemark().equals("")) {
                                url = appload.getRemark();
                            }
                            if (appload.getName().equals("forceUpdateAndroid") && !appload.getRemark().equals("")) {//强制是1  不强制是0
                                isforce = appload.getRemark().equals("1") ? true : false;
                            }
                            if (appload.getName().equals("latestVersionAndroid") && !appload.getRemark().equals("")) {
                                latestversion = appload.getRemark();
                            }
                        }
                        if (!url.equals("") && !latestversion.equals("")) {
                            updateManager = new UpdateManager(mainFragment, getActivity(), latestversion, url);
                            updateManager.checkUpdate(isforce, "有新版本APP需要更新");
                        }
                        if (!"".equals(workTime) && !"".equals(workDay)) {
                            User.getInstance().setCustinfo(workTime + "\n" + workDay);
                            SharePref.put(getActivity(), API.custominfo, workTime + "\n" + workDay);
                        }
                    }

                    @Override
                    public void onFail(int code, String message) {

                    }

                    @Override
                    public void onError() {

                    }

                    @Override
                    public void onClearToken() {
                        Utils.toLogin(getActivity());
                    }
                });
//        new MyRequest().myRequest(API.appload, false, null, false, getActivity(), null, null, new Response() {
//            @Override
//            public void response(JsonObject json, JsonArray jsonArray, boolean isNull) {
//                List<Appload> list = Mygson.getInstance().fromJson(jsonArray, new TypeToken<List<Appload>>() {
//                }.getType());
//                String workTime = "", workDay = "", url = "", latestversion = "";
//                boolean isforce = false;
//                for (int i = 0; i < list.size(); i++) {
//                    Appload appload = list.get(i);
//                    if (appload.getName().equals("serviceLine") && !appload.getRemark().equals("")) {
//                        String phone = appload.getRemark();
//                        User.getInstance().setCustphone(phone);
//                        SharePref.put(MainActivity.getActivity(), API.customphone, phone);
//                    }
//                    if (appload.getName().equals("workDay") && !appload.getRemark().equals("")) {
//                        workDay = appload.getRemark();
//                    }
//                    if (appload.getName().equals("workTime") && !appload.getRemark().equals("")) {
//                        workTime = appload.getRemark();
//                    }
//                    if (appload.getName().equals("androidLink") && !appload.getRemark().equals("")) {
//                        url = appload.getRemark();
//                    }
//                    if (appload.getName().equals("forceUpdateAndroid") && !appload.getRemark().equals("")) {//强制是1  不强制是0
//                        isforce = appload.getRemark().equals("1") ? true : false;
//                    }
//                    if (appload.getName().equals("latestVersionAndroid") && !appload.getRemark().equals("")) {
//                        latestversion = appload.getRemark();
//                    }
//                }
//                if (!url.equals("") && !latestversion.equals("")) {
//                    updateManager = new UpdateManager(MainActivity.getActivity(), latestversion, url);
//                    updateManager.checkUpdate(isforce, "有新版本APP需要更新");
//                }
//                if (!"".equals(workTime) && !"".equals(workDay)) {
//                    User.getInstance().setCustinfo(workTime + "\n" + workDay);
//                    SharePref.put(MainActivity.getActivity(), API.custominfo, workTime + "\n" + workDay);
//                }
//            }
//        });
    }

    private void getAddressByLatLng(double latitude, double longitude) {
        geocodeSearch = new GeocodeSearch(getActivity());
        geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int code) {
                String city = regeocodeResult.getRegeocodeAddress().getCity();
                if (longitude != 0 && latitude != 0) {
                    locateInit(latitude, longitude);
                }
                if (!city.isEmpty()) {
                    User.getInstance().setCityName(city);
                    binding.tvCity.setText(city);
                    SharePref.put(getActivity(), API.cityName, city);
                }
                refreshMap(User.getInstance().getLatitude(getActivity()), User.getInstance().getLongtitude(getActivity()));
                loadCity(User.getInstance().getLatitude(getActivity()), User.getInstance().getLongtitude(getActivity()), binding.tvCity.getText().toString());
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int code) {
            }
        });
        LatLonPoint latLonPoint = new LatLonPoint(latitude, longitude);
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 500f, GeocodeSearch.AMAP);
        //异步查询
        geocodeSearch.getFromLocationAsyn(query);
    }

    private void locateInit(double latitude, double longitude) {
        User.getInstance().setLatitude(latitude);
        User.getInstance().setMylatitude(latitude);
        User.getInstance().setLongtitude(longitude);
        User.getInstance().setMylongtitude(longitude);
        SharePref.put(getActivity(), API.latitude, latitude + "");
        SharePref.put(getActivity(), API.mylatitude, latitude + "");
        SharePref.put(getActivity(), API.longtitude, longitude + "");
        SharePref.put(getActivity(), API.mylongtitude, longitude + "");
    }

    // 定义 Marker 点击事件监听
    AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            if (marker.getObject() instanceof StationInfo) {//当前城市的站点
                StationInfo stationInfo = (StationInfo) marker.getObject();
                markerCreate(stationlist, stationInfo);
                if (stationInfo.getChildSites() != null && stationInfo.getChildSites().size() > 0) {//大站显示 大站无预约或不是我显示原来的  大站预约显示换电宝
                    if (stationInfo.getChildSites().get(0).getId() == User.getInstance().getYuyueId()) {//预约中并且是当前的id
                        showPopDialog(stationInfo.getChildSites().get(0));
                        if (stationPopup != null) {
                            stationPopup.setStation(stationInfo.getChildSites().get(0));
                        }
                    } else {//不再预约中 或者不是当前的站点
                        showStationAndBaoDialog(stationInfo);
                    }
                } else {//小站显示
                    showPopDialog(stationInfo);
                    if (stationPopup != null) {
                        stationPopup.setStation(stationInfo);
                    }
                }
            } else {//除了当前城市所有站点
                markerAllCreate(stationAllList, (StationAll) marker.getObject());
                showAllPopDialog((StationAll) marker.getObject());
            }
            return true;
        }
    };

    private void showStationAndBaoDialog(StationInfo stationInfo) {
        StationInfo childSites = stationInfo.getChildSites().get(0);
        StationBaoAllPopupBinding popupBinding = stationBaoAllPopup.binding;
        popupBinding.tvName.setText(stationInfo.getName());
        popupBinding.tvAddress.setText(stationInfo.getAddress());
        popupBinding.tvPhone.setText(stationInfo.getPhone());
        popupBinding.tvFareStop.setVisibility(stationInfo.getParkFee().equals("") ? View.GONE : View.VISIBLE);
        popupBinding.tvFareStop.setText("停车" + stationInfo.getParkFee() + "元/小时");
        Drawable distancedrawable = getResources().getDrawable(R.mipmap.station_record_guide);
        distancedrawable.setBounds(0, 0, 60, 60);
        popupBinding.tvDistance.setCompoundDrawables(null, distancedrawable, null, null);
        float distance = AMapUtils.calculateLineDistance(new LatLng(User.getInstance().getMylatitude(getActivity()), User.getInstance().getMylongtitude(getActivity())), new LatLng(stationInfo.getLatitude(), stationInfo.getLongitude()));
        DecimalFormat df = new DecimalFormat("#.00");
        if (distance < 1000) {
            popupBinding.tvDistance.setText("导航\n" + df.format(distance) + "m");
        } else {
            popupBinding.tvDistance.setText("导航\n" + df.format(distance / 1000) + "km");
        }
        popupBinding.tvDistance.setOnClickListener(v -> {
            Poi start = new Poi("", new com.amap.api.maps.model.LatLng(User.getInstance().getMylatitude(getActivity()), User.getInstance().getMylongtitude(getActivity())), "");
            Poi end = new Poi("", new com.amap.api.maps.model.LatLng(stationInfo.getLatitude(), stationInfo.getLongitude()), "");
            AmapNaviPage.getInstance().showRouteActivity(getContext(), new AmapNaviParams(start, null, end, AmapNaviType.DRIVER, AmapPageType.NAVI), null, MapCustomActivity.class);
            if (stationPopup != null) {
                stationPopup.dismiss();
            }
        });
        int lefticon = 0;
        boolean worktime = false;
        String startTime = DateUtils.timeTotime(stationInfo.getStart_time());
        String endTime = DateUtils.timeTotime(stationInfo.getEnd_time());
        worktime = DateUtils.isBelong(startTime, endTime);
        if (stationInfo.getStatus() == 2) {
            lefticon = R.mipmap.station_repair;
        } else {
            if (stationInfo.getBattery() > 0 && worktime) {
                lefticon = R.mipmap.station_icon;
            } else if (stationInfo.getBattery() > 0 && !worktime) {//休息中
                lefticon = R.mipmap.station_rest;
            } else if (worktime && stationInfo.getBattery() == 0) {//无电池
                lefticon = R.mipmap.station_icon;
            } else {
                lefticon = R.mipmap.station_icon;
            }
        }
        Drawable leftDrawable = getResources().getDrawable(lefticon);
        leftDrawable.setBounds(0, 0, 80, 80);
        popupBinding.tvName.setCompoundDrawables(leftDrawable, null, null, null);
        //init second para
        popupBinding.tvNameZhan.setText(stationInfo.getName());
        popupBinding.tvPaiduiZhan.setText("排队人数：" + stationInfo.getWaiting());
        popupBinding.tvKucunZhan.setText(stationInfo.getBattery() > 0 ? "电池库存：有" : "电池库存：无");
        String text_sign;
        int color_sign, drawable_clock, drawable_sign;
        if (stationInfo.getStatus() == 2) {
            text_sign = "维修中";
            color_sign = R.color.list_gray;
            drawable_clock = R.drawable.shape_gray;
            drawable_sign = R.drawable.shape_fill_gray;
        } else {
            if (stationInfo.getBattery() > 0 && worktime) {
                text_sign = "运营中";
                color_sign = R.color.text_color;
                drawable_clock = R.drawable.shape_blue;
                drawable_sign = R.drawable.shape_fill_blue;
            } else if (stationInfo.getBattery() > 0 && !worktime) {//休息中
                text_sign = "休息中";
                color_sign = R.color.yellow;
                drawable_clock = R.drawable.shape_yellow;
                drawable_sign = R.drawable.shape_fill_yellow;
            } else if (worktime && stationInfo.getBattery() == 0) {//无电池
                text_sign = "运营中";
                color_sign = R.color.text_color;
                drawable_clock = R.drawable.shape_blue;
                drawable_sign = R.drawable.shape_fill_blue;

            } else {
                text_sign = "运营中";
                color_sign = R.color.text_color;
                drawable_clock = R.drawable.shape_blue;
                drawable_sign = R.drawable.shape_fill_blue;
            }
        }
        popupBinding.tvSignZhan.setText(text_sign);
        popupBinding.tvSignZhan.setBackground(getResources().getDrawable(drawable_sign));
        popupBinding.tvClockZhan.setTextColor(getResources().getColor(color_sign));
        popupBinding.tvClockZhan.setBackground(getResources().getDrawable(drawable_clock));
        popupBinding.tvClockZhan.setText(DateUtils.datetoTime(stationInfo.getStart_time()) + "~" + DateUtils.datetoTime(stationInfo.getEnd_time()));
        //bao init
        popupBinding.tvNameBao.setText(childSites.getName());
        popupBinding.tvDianchiBao.setText("可换电池：" + childSites.getBattery());
        boolean worktimebao = false;
        String startTimebao = DateUtils.timeTotime(childSites.getStart_time());
        String endTimebao = DateUtils.timeTotime(childSites.getEnd_time());
        worktimebao = DateUtils.isBelong(startTimebao, endTimebao);
        String text_sign_bao;
        int color_sign_bao, drawable_clock_bao, drawable_sign_bao;
        if (childSites.getStatus() == 2) {
            text_sign_bao = "维修中";
            color_sign_bao = R.color.list_gray;
            drawable_clock_bao = R.drawable.shape_gray;
            drawable_sign_bao = R.drawable.shape_fill_gray;
            popupBinding.tvYue.setVisibility(View.GONE);
        } else {
            if (childSites.getBattery() > 0 && worktimebao) {
                /*text_sign = "可预约";
                        color_sign = R.color.green;
                        drawable_clock = R.drawable.shape_green;
                        drawable_sign = R.drawable.shape_fill_green;*/
                text_sign_bao = "可预约";
                color_sign_bao = R.color.green;
                drawable_clock_bao = R.drawable.shape_green;
                drawable_sign_bao = R.drawable.shape_fill_green;
                popupBinding.tvYue.setVisibility(View.VISIBLE);
            } else if (childSites.getBattery() > 0 && !worktimebao) {//休息中
                /*text_sign = "休息中";
                color_sign = R.color.yellow;
                drawable_clock = R.drawable.shape_yellow;
                drawable_sign = R.drawable.shape_fill_yellow;*/
                text_sign_bao = "休息中";
                color_sign_bao = R.color.yellow;
                drawable_clock_bao = R.drawable.shape_yellow;
                drawable_sign_bao = R.drawable.shape_fill_yellow;
                popupBinding.tvYue.setVisibility(View.GONE);
            } else if (worktimebao && childSites.getBattery() == 0) {//无电池
                /*  text_sign = "无电池";
                    color_sign = R.color.list_gray;
                    drawable_clock = R.drawable.shape_gray;
                    drawable_sign = R.drawable.shape_fill_gray;*/
                text_sign_bao = "无电池";
                color_sign_bao = R.color.list_gray;
                drawable_clock_bao = R.drawable.shape_gray;
                drawable_sign_bao = R.drawable.shape_fill_gray;
                popupBinding.tvYue.setVisibility(View.GONE);
            } else {
                text_sign_bao = "无电池";
                color_sign_bao = R.color.list_gray;
                drawable_clock_bao = R.drawable.shape_gray;
                drawable_sign_bao = R.drawable.shape_fill_gray;
                popupBinding.tvYue.setVisibility(View.GONE);
            }
        }
        popupBinding.tvSignBao.setText(text_sign_bao);
        popupBinding.tvSignBao.setBackground(getResources().getDrawable(drawable_sign_bao));
        popupBinding.tvClockBao.setBackground(getResources().getDrawable(drawable_clock_bao));
        popupBinding.tvClockBao.setTextColor(getResources().getColor(color_sign_bao));
        popupBinding.tvClockBao.setText(DateUtils.datetoTime(childSites.getStart_time()) + "~" + DateUtils.datetoTime(childSites.getEnd_time()));
        popupBinding.tvYue.setOnClickListener(v -> {
            if (Utils.usebindisNotexist(useBind)) {
                Utils.snackbar(getActivity(), "请先选择您的车辆，才能预约站点");
                return;
            }
            stationBaoAllPopup.dismiss();
            appoint(childSites.getId(), useBind.getCar_id());
        });
        stationBaoAllPopup.setBackgroundColor(Color.TRANSPARENT);
        stationBaoAllPopup.showPopupWindow();
    }

    private void showPopDialog(StationInfo contentBean) {
        stationPopup.name.setText(contentBean.getName());
        stationPopup.address.setText(contentBean.getAddress());
        stationPopup.phone.setText(contentBean.getPhone());
        stationPopup.phone.setOnClickListener(v -> {
            if (stationPopup != null) {
                stationPopup.dismiss();
            }
            Intent intent = new Intent(Intent.ACTION_DIAL);
            Uri data = Uri.parse("tel:" + contentBean.getPhone());
            intent.setData(data);
            startActivity(intent);
        });
        stationPopup.tv_clock.setText(DateUtils.datetoTime(contentBean.getStart_time()) + "~" + DateUtils.datetoTime(contentBean.getEnd_time()));
        if (contentBean.getType() == 0) {
            stationPopup.paidui.setText("排队人数：" + contentBean.getWaiting());
            stationPopup.kucun.setVisibility(View.VISIBLE);
        } else if (contentBean.getType() == 1) {
            stationPopup.paidui.setText("可换电池：" + contentBean.getBattery());
            stationPopup.kucun.setVisibility(View.INVISIBLE);
        }
        stationPopup.kucun.setText(contentBean.getBattery() > 0 ? "电池库存：有" : "电池库存：无");
        boolean worktime = false;
        String startTime = DateUtils.timeTotime(contentBean.getStart_time());
        String endTime = DateUtils.timeTotime(contentBean.getEnd_time());
        worktime = DateUtils.isBelong(startTime, endTime);
        int lefticon = 0;
        String text_sign;
        int color_sign, drawable_clock, drawable_sign;
        if (contentBean.getStatus() == 2) {
            text_sign = "维修中";
            color_sign = R.color.list_gray;
            drawable_clock = R.drawable.shape_gray;
            drawable_sign = R.drawable.shape_fill_gray;
            if (contentBean.getType() == 0) {
                lefticon = R.mipmap.station_repair;
            } else {
                lefticon = R.mipmap.bao_nopower;
            }
        } else {
            if (contentBean.getBattery() > 0 && worktime) {
                if (contentBean.getType() == 0) {//0是换电站
                    text_sign = "运营中";
                    color_sign = R.color.text_color;
                    drawable_clock = R.drawable.shape_blue;
                    lefticon = R.mipmap.station_icon;
                    drawable_sign = R.drawable.shape_fill_blue;
                } else {
                    if (contentBean.isAppointment()) {
                        text_sign = "可预约";
                        color_sign = R.color.green;
                        drawable_clock = R.drawable.shape_green;
                        drawable_sign = R.drawable.shape_fill_green;
                    } else {
                        text_sign = "运营中";
                        color_sign = R.color.text_color;
                        drawable_clock = R.drawable.shape_blue;
                        drawable_sign = R.drawable.shape_fill_blue;
                    }
                    lefticon = R.mipmap.bao_working;
                }
            } else if (contentBean.getBattery() > 0 && !worktime) {//休息中
                text_sign = "休息中";
                color_sign = R.color.yellow;
                drawable_clock = R.drawable.shape_yellow;
                drawable_sign = R.drawable.shape_fill_yellow;
                if (contentBean.getType() == 0) {//0是换电站
                    lefticon = R.mipmap.station_rest;
                } else {
                    lefticon = R.mipmap.bao_rest;
                }
            } else if (worktime && contentBean.getBattery() == 0) {//无电池
                if (contentBean.getType() == 0) {//0是换电站
                    lefticon = R.mipmap.station_icon;
                    text_sign = "运营中";
                    color_sign = R.color.text_color;
                    drawable_clock = R.drawable.shape_blue;
                    drawable_sign = R.drawable.shape_fill_blue;
                } else {
                    lefticon = R.mipmap.bao_nopower;
                    text_sign = "无电池";
                    color_sign = R.color.list_gray;
                    drawable_clock = R.drawable.shape_gray;
                    drawable_sign = R.drawable.shape_fill_gray;
                }
            } else {
                if (contentBean.getType() == 0) {//0是换电站
                    lefticon = R.mipmap.station_icon;
                    text_sign = "运营中";
                    color_sign = R.color.text_color;
                    drawable_clock = R.drawable.shape_blue;
                    drawable_sign = R.drawable.shape_fill_blue;
                } else {
                    lefticon = R.mipmap.bao_nopower;
                    text_sign = "无电池";
                    color_sign = R.color.list_gray;
                    drawable_clock = R.drawable.shape_gray;
                    drawable_sign = R.drawable.shape_fill_gray;
                }
            }
        }
        stationPopup.tv_sign.setText(text_sign);
        stationPopup.tv_sign.setBackground(getResources().getDrawable(drawable_sign));
        stationPopup.tv_clock.setBackground(getResources().getDrawable(drawable_clock));
        stationPopup.tv_clock.setTextColor(getResources().getColor(color_sign));
        if (contentBean.getParkFee().equals("")) {
            stationPopup.tv_fare_stop.setVisibility(View.GONE);
        } else {
            stationPopup.tv_fare_stop.setVisibility(View.VISIBLE);
            stationPopup.tv_fare_stop.setText("停车" + contentBean.getParkFee() + "元/小时");
        }
        Drawable leftDrawable = getResources().getDrawable(lefticon);
        leftDrawable.setBounds(0, 0, 80, 80);
        stationPopup.name.setCompoundDrawables(leftDrawable, null, null, null);
        Drawable distancedrawable = getResources().getDrawable(R.mipmap.station_record_guide);
        distancedrawable.setBounds(0, 0, 60, 60);
        stationPopup.distance.setCompoundDrawables(null, distancedrawable, null, null);
        if (contentBean.getStatus() == 2) {//2是维修中状态
            stationPopup.appoint.setVisibility(View.GONE);
            stationPopup.redelayDismiss.setVisibility(View.GONE);
            stationPopup.time.setVisibility(View.GONE);
        } else {
            if (User.getInstance().getYuyueId() != -1) {//预约状态
                if (User.getInstance().getYuyueId() == contentBean.getId()) {//预约状态并且是当前得站点
                    stationPopup.appoint.setVisibility(View.GONE);
                    stationPopup.redelayDismiss.setVisibility(View.VISIBLE);
                    stationPopup.time.setVisibility(View.VISIBLE);
                } else {
                    if (contentBean.isAppointment()) {
                        if (worktime && contentBean.getBattery() > 0) {//在运营时间内 并且电池数量大于0
                            stationPopup.appoint.setVisibility(View.VISIBLE);
                            stationPopup.redelayDismiss.setVisibility(View.GONE);
                            stationPopup.time.setVisibility(View.GONE);
                        } else {
                            stationPopup.appoint.setVisibility(View.GONE);
                            stationPopup.redelayDismiss.setVisibility(View.GONE);
                            stationPopup.time.setVisibility(View.GONE);
                        }
                    } else {
                        stationPopup.appoint.setVisibility(View.GONE);
                        stationPopup.redelayDismiss.setVisibility(View.GONE);
                        stationPopup.time.setVisibility(View.GONE);
                    }
                }
            } else {//不是预约状态
                if (contentBean.isAppointment()) {//可以预约
                    if (worktime && contentBean.getBattery() > 0) {//在运营时间内 并且电池数量大于0
                        stationPopup.appoint.setVisibility(View.VISIBLE);
                        stationPopup.redelayDismiss.setVisibility(View.GONE);
                        stationPopup.time.setVisibility(View.GONE);
                    } else {
                        stationPopup.appoint.setVisibility(View.GONE);
                        stationPopup.redelayDismiss.setVisibility(View.GONE);
                        stationPopup.time.setVisibility(View.GONE);
                    }
                } else {
                    stationPopup.appoint.setVisibility(View.GONE);
                    stationPopup.redelayDismiss.setVisibility(View.GONE);
                    stationPopup.time.setVisibility(View.GONE);
                }
            }
        }
        stationPopup.appoint.setOnClickListener(v -> {
            if (Utils.usebindisNotexist(useBind)) {
                Utils.snackbar(getActivity(), "请先选择您的车辆，才能预约站点");
                return;
            }
            appoint(contentBean.getId(), useBind.getCar_id());
        });
        stationPopup.delayappointcancel.setOnClickListener(v -> {
            if (Utils.usebindisNotexist(useBind)) {
                Utils.snackbar(getActivity(), "请在选择您的车辆，才能取消预约站点");
                return;
            }
            cancelAppoint();
        });
        stationPopup.delayappoint.setOnClickListener(v -> {
            if (Utils.usebindisNotexist(useBind)) {
                Utils.snackbar(getActivity(), "请在选择您的车辆，才能延时预约站点");
                return;
            }
            delayappoint();
        });
//        if (UserLogin.getInstance().getAccount() != null) {
        float distance = AMapUtils.calculateLineDistance(new LatLng(User.getInstance().getMylatitude(getActivity()), User.getInstance().getMylongtitude(getActivity())), new LatLng(contentBean.getLatitude(), contentBean.getLongitude()));
        DecimalFormat df = new DecimalFormat("#.00");
        if (distance < 1000) {
            stationPopup.distance.setText("导航\n" + df.format(distance) + "m");
        } else {
            stationPopup.distance.setText("导航\n" + df.format(distance / 1000) + "km");
        }
        stationPopup.distance.setOnClickListener(view -> {
            if (User.getInstance().getYuyueId() != contentBean.getId() && contentBean.getType() == 1) {
                Utils.snackbar(getActivity(), "请预约成功后前往换电");
            } else {
                Poi start = new Poi("", new com.amap.api.maps.model.LatLng(User.getInstance().getMylatitude(getActivity()), User.getInstance().getMylongtitude(getActivity())), "");
                Poi end = new Poi("", new com.amap.api.maps.model.LatLng(contentBean.getLatitude(), contentBean.getLongitude()), "");
                AmapNaviPage.getInstance().showRouteActivity(getContext(), new AmapNaviParams(start, null, end, AmapNaviType.DRIVER, AmapPageType.NAVI), null, MapCustomActivity.class);
                if (stationPopup != null) {
                    stationPopup.dismiss();
                }
            }
        });
        stationPopup.setBackgroundColor(Color.TRANSPARENT);
        stationPopup.showPopupWindow();
    }

    private void showAllPopDialog(StationAll contentBean) {
        stationOtherCitiesPopup.name.setText(contentBean.getName());
        stationOtherCitiesPopup.address.setText(contentBean.getAddress());
        stationOtherCitiesPopup.phone.setText(contentBean.getPhone());
        stationOtherCitiesPopup.phone.setOnClickListener(v -> {
            if (stationOtherCitiesPopup != null) {
                stationOtherCitiesPopup.dismiss();
            }
            Intent intent = new Intent(Intent.ACTION_DIAL);
            Uri data = Uri.parse("tel:" + contentBean.getPhone());
            intent.setData(data);
            startActivity(intent);
        });
        stationOtherCitiesPopup.worktime.setText(DateUtils.datetoTime(contentBean.getStart_time()) + "~" + DateUtils.datetoTime(contentBean.getEnd_time()));
        int mipmap = 0;
        if (contentBean.getType() == 0) {//换电站
            mipmap = R.mipmap.station_icon;
        } else {
            mipmap = R.mipmap.bao_working;
        }
        Drawable leftDrawable = getResources().getDrawable(mipmap);
        leftDrawable.setBounds(0, 0, 80, 80);
        stationOtherCitiesPopup.name.setCompoundDrawables(leftDrawable, null, null, null);
        stationOtherCitiesPopup.setBackgroundColor(Color.TRANSPARENT);
        stationOtherCitiesPopup.showPopupWindow();
    }

    private void delayappoint() {//    "appointment_no": "AP2949782536458240"
//        HashMap<String, String> map = new HashMap<>();
//        map.put("appointment_no", appointment_no);
//        new MyRequest().myRequest(API.delayappoint, true, map, true, getActivity(), null, null, new Response() {
//            @Override
//            public void response(JsonObject json, JsonArray jsonArray, boolean isNull) {
//                Utils.snackbar(MainActivity.getActivity(), "延时预约成功");
//                getcurrentAppint();
//            }
//        });
        CancelAppointmentRequest cancelAppointmentRequest = new CancelAppointmentRequest();
        cancelAppointmentRequest.setAppointment_no(appointment_no);
        InternetWorkManager.getRequest().delayAppointment(Utils.body(Mygson.getInstance().toJson(cancelAppointmentRequest)))
                .compose(RxHelper.observableIOMain(getActivity()))
                .subscribe(new MyObserver<User>() {
                    @Override
                    public void onSuccess(User data) {
                        Utils.snackbar(getActivity(), "延时预约成功");
                        getcurrentAppint();
                    }

                    @Override
                    public void onFail(int code, String message) {

                    }

                    @Override
                    public void onError() {

                    }

                    @Override
                    public void onClearToken() {
                        Utils.toLogin(getActivity());
                    }
                });
    }

    private void cancelAppoint() {
//        HashMap<String, String> map = new HashMap<>();
//        map.put("appointment_no", appointment_no);
//        new MyRequest().myRequest(API.cancelappoint, true, map, true, getActivity(), null, null, new Response() {
//            @Override
//            public void response(JsonObject json, JsonArray jsonArray, boolean isNull) {
//                Utils.snackbar(MainActivity.getActivity(), "取消预约成功");
//                getcurrentAppint();
//            }
//        });
        CancelAppointmentRequest cancelAppointmentRequest = new CancelAppointmentRequest();
        cancelAppointmentRequest.setAppointment_no(appointment_no);
        InternetWorkManager.getRequest().cancelAppointment(Utils.body(Mygson.getInstance().toJson(cancelAppointmentRequest)))
                .compose(RxHelper.observableIOMain(getActivity()))
                .subscribe(new MyObserver<User>() {
                    @Override
                    public void onSuccess(User data) {
                        Utils.snackbar(getActivity(), "取消预约成功");
                        if (stationPopup.isShowing()) {
                            stationPopup.dismiss();
                        }
                        getcurrentAppint();
                    }

                    @Override
                    public void onFail(int code, String message) {

                    }

                    @Override
                    public void onError() {

                    }

                    @Override
                    public void onClearToken() {
                        Utils.toLogin(getActivity());
                    }
                });
    }

    private void appoint(int stationid, int carid) {//预约
        //{
        //    "site_id": 99,
        //    "car_id": 11391
        //}
//        HashMap<String, String> map = new HashMap<>();
//        map.put("site_id", String.valueOf(station));
//        map.put("car_id", String.valueOf(carid));
//        new MyRequest().myRequest(API.appoint, true, map, false, MainActivity.getActivity(), null, null, new Response() {
//            @Override
//            public void response(JsonObject json, JsonArray jsonArray, boolean isNull) {
//                Utils.snackbar(MainActivity.getActivity(), "预约成功");
//                getcurrentAppint();
//            }
//        });
        AddAppointmentRequest addAppointmentRequest = new AddAppointmentRequest();
        addAppointmentRequest.setSite_id(stationid);
        addAppointmentRequest.setCar_id(carid);
        InternetWorkManager.getRequest().addAppointment(Utils.body(Mygson.getInstance().toJson(addAppointmentRequest)))
                .compose(RxHelper.observableIOMain(getActivity()))
                .subscribe(new MyObserver<User>() {
                    @Override
                    public void onSuccess(User data) {
                        Utils.snackbar(getActivity(), "预约成功");
                        getcurrentAppint();
                    }

                    @Override
                    public void onFail(int code, String message) {

                    }

                    @Override
                    public void onError() {

                    }

                    @Override
                    public void onClearToken() {
                        Utils.toLogin(getActivity());
                    }
                });
    }

    private void loadStation(double longitude, double latitude, int type, int cityId) {
        SiteRequest siteRequest = new SiteRequest();
        siteRequest.setLongitude(longitude);
        siteRequest.setLatitude(latitude);
        siteRequest.setType(type);
        siteRequest.setCity_Id(cityId);
        siteRequest.setUse(1);
        siteRequest.setaId(User.getInstance().getAccountId());
        InternetWorkManager.getRequest().sitelist(Utils.body(Mygson.getInstance().toJson(siteRequest)))
                .compose(RxHelper.observableIOMain(getActivity()))
                .subscribe(new MyObserver<List<StationInfo>>() {
                    @Override
                    public void onSuccess(List<StationInfo> data) {
                        stationlist = data;
                        boolean ishasHuandianbao = false;
                        for (int i = 0; i < stationlist.size(); i++) {
                            if (stationlist.get(i).getType() == 1) {//0是换电站
                                ishasHuandianbao = true;
                            }
                        }
                        if (ishasHuandianbao) {
                            binding.finish.setVisibility(View.VISIBLE);
                        } else {
                            binding.finish.setVisibility(View.GONE);
                        }
                        markerCreate(stationlist, null);
                        getcurrentAppint();
                    }

                    @Override
                    public void onFail(int code, String message) {

                    }

                    @Override
                    public void onError() {

                    }

                    @Override
                    public void onClearToken() {
                        Utils.toLogin(getActivity());
                    }

                });
//        HashMap<String, String> map = new HashMap<>();
//        map.put("longitude", String.valueOf(longitude));
//        map.put("latitude", String.valueOf(latitude));
//        map.put("type", type);
//        map.put("city_Id", String.valueOf(cityId));
//        new MyRequest().myRequest(API.SITELIST, true, map, false, getActivity(), null, null, new Response() {
//            @Override
//            public void response(JsonObject json, JsonArray jsonArray, boolean isNull) {
//                stationlist = Mygson.getInstance().fromJson(jsonArray, new TypeToken<List<StationInfo>>() {
//                }.getType());
//                boolean ishasHuandianbao = false;
//                for (int i = 0; i < stationlist.size(); i++) {
//                    if (stationlist.get(i).getType() == 1) {//0是换电站
//                        ishasHuandianbao = true;
//                    }
//                }
//                if (ishasHuandianbao) {
//                    binding.finish.setVisibility(View.VISIBLE);
//                } else {
//                    binding.finish.setVisibility(View.GONE);
//                }
//                markerCreate(stationlist, null);
//                getcurrentAppint();
//            }
//        });
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.msg:
                if (Utils.getToken(getActivity()) == null || Utils.getToken(getActivity()).equals("")) {
                    Utils.toLogin(getActivity());
                    Utils.snackbar(getActivity(), "请先登录");
                    return;
                }
                intent = new Intent(getActivity(), MessageActivity.class);
                startActivity(intent);
                break;
//            case R.id.tv_right:
////                if (UserLogin.getInstance().getCityid(MainActivity.getActivity()) == 0){
////                    Utils.snackbar(MainActivity.getActivity(), "请先等待定位");
////                    return;
////                }
//                intent = new Intent(getActivity(), ListFragment.class);
//                intent.putExtra("useBind", useBind);
//                startActivityForResult(intent, REQUESTCODE_LIST);
//                break;
            case R.id.tv_city:
                intent = new Intent(getActivity(), ChoseCityActivity.class);
                startActivityForResult(intent, REQUESTCODE_MAIN);
                break;
            case R.id.re_carplate:
                if (Utils.getToken(getActivity()) == null || Utils.getToken(getActivity()).equals("")) {
                    Utils.toLogin(getActivity());
                    Utils.snackbar(getActivity(), "请先登录");
                    return;
                }
                intent = new Intent(getActivity(), MyCarActivity.class);
                startActivity(intent);
                break;
            case R.id.qrcode:
                if (Utils.getToken(getActivity()) == null || Utils.getToken(getActivity()).equals("")) {
                    Utils.toLogin(getActivity());
                    Utils.snackbar(getActivity(), "请先登录");
                    return;
                }
                String carplate = "";
                if (Utils.usebindisNotexist(useBind)) {
                    carplate = "";
                } else {
                    carplate = useBind.getPlate_number();
                }
                BaseDialog.showQrDialog(getActivity(), carplate);
                break;
            case R.id.recharge:
                if (Utils.getToken(getActivity()) == null || Utils.getToken(getActivity()).equals("")) {
                    Utils.toLogin(getActivity());
                    Utils.snackbar(getActivity(), "请先登录");
                    return;
                }
                intent = new Intent(getActivity(), RechargeActivity.class);
                startActivity(intent);
                break;
            case R.id.daka:
                if (Utils.getToken(getActivity()) == null || Utils.getToken(getActivity()).equals("")) {
                    Utils.toLogin(getActivity());
                    Utils.snackbar(getActivity(), "请先登录");
                    return;
                }
                if (Utils.usebindisNotexist(useBind)) {
                    Utils.snackbar(getActivity(), "请先选择车辆");
                    return;
                }
                intent = new Intent(getActivity(), DriverclockActivity.class);
                startActivity(intent);
                break;
            case R.id.scan:
                scan();
                break;
            case R.id.img_kefu:
                Utils.loadkefu(getActivity(), binding.imgKefu);
                break;
            case R.id.tv_station_name:
//                if (stationlist != null) {
                stationSelectActivity = new StationSelectActivity(mainFragment);
                stationSelectActivity.show(getChildFragmentManager(), "data");
//                }
                break;
            default:
                break;
        }
    }

    public void installApk() {
        if (updateManager != null) {
            updateManager.installApk();
        }
    }

    public void setProgress() {
        if (updateManager != null) {
            updateManager.setProgress();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE_MAIN && resultCode == 1) {//选择城市
            String cityName = data.getStringExtra(API.cityName);
            binding.tvCity.setText(cityName);
            User.getInstance().setCityName(cityName);
            SharePref.put(getActivity(), API.cityName, cityName);
            double lat = data.getDoubleExtra(API.latitude, 0);
            User.getInstance().setLatitude(lat);
            SharePref.put(getActivity(), API.latitude, lat + "");
            double lon = data.getDoubleExtra(API.longtitude, 0);
            User.getInstance().setLongtitude(lon);
            SharePref.put(getActivity(), API.longtitude, lon + "");
            cityid = data.getIntExtra(API.cityId, 0);
            User.getInstance().setCityid(cityid);
            SharePref.put(getActivity(), API.cityId, cityid + "");
            refreshMap(lat, lon);
            User.getInstance().setMainchanged(true);
        } else if (requestCode == REQUESTCODE_SCAN && resultCode == 2) {//换点扫码哦
            String message = data.getStringExtra(API.code);
            if (message.indexOf("=") == -1) {
                Utils.snackbar(getActivity(), "请确认当前二维码是站点二维码");
                return;
            }
            if (message.indexOf("station") != -1) {
                String[] splite = message.split("=");
                postScan(splite[1]);
            }
        } else if (requestCode == REQUESTCODE_GPS) {
            setLocate();
        }
    }

//    //地图显示刷新的位置
//    private void refreshLocation(double lat, double lng) {
//        LatLng latLng = new LatLng(lat, lng);
//        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
//    }

    private void postScan(String data) {
        int stationId = 0;
        if (User.getInstance().getYuyueId() == -1) {
            stationId = Integer.parseInt((String) SharePref.get(getActivity(), API.siteid, "-1"));
        } else {
            stationId = User.getInstance().getYuyueId();
        }
        if (stationId == -1) {
            Utils.snackbar(getActivity(), "当前没有预约，请先预约站点");
            return;
        }
        if (!data.equals(String.valueOf(stationId))) {
            Utils.snackbar(getActivity(), "当前预约站点不是扫码站点");
            return;
        }
        BaseDialog.showDialog(getActivity(), "温馨提示", "您已扫码成功，请确认是否换电", "确认", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                HashMap<String, String> map = new HashMap<>();
//                map.put("appointmentNo", User.getInstance().getAppointment_no(MainActivity.getActivity()));
//                new MyRequest().myRequest(API.scanpay, true, map, true, MainActivity.getActivity(), null, null, new Response() {
//                    @Override
//                    public void response(JsonObject json, JsonArray jsonArray, boolean isNull) {
//                        List<BillDetail> list = Mygson.getInstance().fromJson(json.get("detail").getAsJsonArray(), new TypeToken<List<BillDetail>>() {
//                        }.getType());
//                        String name = json.get("siteName").getAsString();
//                        String fare = json.get("realFare").getAsString();
//                        int recordId = json.get("recordId").getAsInt();
//                        scanPopup = new ScanPopup(MainActivity.getActivity(), MainActivity.getActivity(), list, name, fare, User.getInstance().getAppointment_no(MainActivity.getActivity()),
//                                recordId);
//                        scanPopup.showPopupWindow();
//                    }
//                });
                ConsumeRequest consumeRequest = new ConsumeRequest();
                consumeRequest.setAppointmentNo(User.getInstance().getAppointment_no(getActivity()));
                InternetWorkManager.getRequest().consumegetlist(Utils.body(Mygson.getInstance().toJson(consumeRequest)))
                        .compose(RxHelper.observableIOMain(getActivity()))
                        .subscribe(new MyObserver<ConsumeListModel>() {
                            @Override
                            public void onSuccess(ConsumeListModel data) {
                                scanPopup = new ScanPopup(mainFragment, getContext(), data.getDetail(), data.getSiteName(),
                                        data.getRealFare(), User.getInstance().getAppointment_no(getActivity()),
                                        data.getRecordId());
                                scanPopup.showPopupWindow();
                            }

                            @Override
                            public void onFail(int code, String message) {

                            }

                            @Override
                            public void onError() {

                            }

                            @Override
                            public void onClearToken() {
                                Utils.toLogin(getActivity());
                            }
                        });
            }
        });
    }

    public class PrivacyDialog extends Dialog {
        Context context;

        public PrivacyDialog(Context context, int themeResId) {
            super(context, themeResId);
            this.context = context;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_privacy);
            TextView xieyiTv = findViewById(R.id.tv_xieyi);
            TextView privacyTv = findViewById(R.id.tv_privacy);
            TextView cancelTv = findViewById(R.id.tv_cancel);
            TextView agreeTv = findViewById(R.id.tv_agree);
            setCancelable(false);
            setCanceledOnTouchOutside(false);
            cancelTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            agreeTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    SharePref.oneput(context, "agree", true);
                }
            });
            xieyiTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, UseProtocolActivity.class);
                    context.startActivity(intent);
                }
            });
            privacyTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PrivacyActivity.class);
                    context.startActivity(intent);
                }
            });
        }
    }


    private void init() {
        BottomSheetBehavior behavior = BottomSheetBehavior.from(binding.cardview);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        behavior.setPeekHeight(85);
        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case 3://open
                        binding.mainArrow.setImageDrawable(getResources().getDrawable(R.mipmap.maindown_arrow));
                        break;
                    case 4://hide
                        binding.mainArrow.setImageDrawable(getResources().getDrawable(R.mipmap.mainup_arrow));
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
        binding.reArrow.setOnClickListener(v -> {
            if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
            if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        bindClick();
        AndPermission.with(getActivity())
                .runtime()
                .permission(WRITE_EXTERNAL_STORAGE, Permission.READ_PHONE_STATE, ACCESS_FINE_LOCATION, CAMERA)
                .onGranted(permissions -> {
                    setLocate();
                    bluePointLocat(); //定位蓝点
//                    loadStation(UserLogin.getInstance().getLongtitude(MainActivity.getActivity()),UserLogin.getInstance().getLatitude(MainActivity.getActivity()),
//                            "-1");
//                    loadServiceData();
                })
                .onDenied(permissions -> {
                })
                .start();
    }

    //获取当前预约//{
    //                    "appointment_no": "AP2946390799945728",
    //                            "end_time": "2020-12-25T14:19:52+08:00",
    //                            "site_id": 99
    public void getcurrentAppint() {
        InternetWorkManager.getRequest().currentAppointment()
                .compose(RxHelper.observableIOMain(getActivity()))
                .subscribe(new MyObserver<CurrentAppointmentModel>() {
                    @Override
                    public void onSuccess(CurrentAppointmentModel data) {
                        if (data == null) {
                            User.getInstance().setAppointment_no("");
                            User.getInstance().setYuyueId(-1);
                            SharePref.put(getActivity(), API.appointment_no, "");
                            SharePref.put(getActivity(), API.siteid, "-1");
                            if (stationPopup != null) {
                                if (stationPopup.getStationInfo() != null) {
                                    StationInfo stationInfo = stationPopup.getStationInfo();
                                    boolean worktime = false;
                                    String startTime = DateUtils.timeTotime(stationInfo.getStart_time());
                                    String endTime = DateUtils.timeTotime(stationInfo.getEnd_time());
                                    worktime = DateUtils.isBelong(startTime, endTime);
                                    if (stationInfo.getType() == 1 && stationInfo.getStatus() != 2 && worktime) {//换电宝营业中
                                        stationPopup.appoint.setVisibility(View.VISIBLE);
                                    } else {
                                        stationPopup.appoint.setVisibility(View.GONE);
                                    }
                                } else {
                                    stationPopup.appoint.setVisibility(View.GONE);
                                }
                                stationPopup.redelayDismiss.setVisibility(View.GONE);
                                stationPopup.time.setVisibility(View.GONE);
                            }
                            if (timeCount != null) {
                                timeCount.cancel();
                            }
                        } else {
                            if (data.getAppointment_no() != null) {
                                appointment_no = data.getAppointment_no();
                                User.getInstance().setAppointment_no(appointment_no);
                                SharePref.put(getActivity(), API.appointment_no, appointment_no);
                            }
                            if (data.getEnd_time() != null) {
                                long endTime = DateUtils.date2TimeStamp(data.getEnd_time());
                                long currentTime = System.currentTimeMillis() / 1000;
                                long time = endTime - currentTime;
                                if (time > 0) {
                                    if (stationPopup != null) {
                                        stationPopup.appoint.setVisibility(View.GONE);
                                        stationPopup.redelayDismiss.setVisibility(View.VISIBLE);
                                        stationPopup.time.setVisibility(View.VISIBLE);
                                    }
                                    if (timeCount != null) {
                                        timeCount.cancel();
                                        timeCount = new TimeCount(time * 1000, stationPopup.time, MainFragment.this::timeFinish);
                                        timeCount.start();
                                    } else {
                                        timeCount = new TimeCount(time * 1000, stationPopup.time, MainFragment.this::timeFinish);
                                        timeCount.start();
                                    }
                                }
                                Utils.log("", "", endTime + "分割线" + currentTime);
                            }
                            if (data.getSite_id() != 0) {
                                User.getInstance().setYuyueId(data.getSite_id());
                                SharePref.put(getActivity(), API.siteid, data.getSite_id() + "");
                                StationInfo stationInfo = Utils.getstation(stationlist, data.getSite_id());
                                if (stationlist.size() != 0 && stationInfo != null) {
                                    markerCreate(stationlist, stationInfo);
                                    showPopDialog(stationInfo);//获取我的预约
                                    if (stationPopup != null) {
                                        stationPopup.setStation(stationInfo);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onFail(int code, String message) {

                    }

                    @Override
                    public void onError() {

                    }

                    @Override
                    public void onClearToken() {
                        Utils.toLogin(getActivity());
                    }
                });
    }

    public void scan() {
        if (Utils.getToken(getActivity()) == null || Utils.getToken(getActivity()).equals("")) {
            Utils.toLogin(getActivity());
            Utils.snackbar(getActivity(), "请先登录");
            return;
        }
        if (Utils.usebindisNotexist(useBind)) {
            Utils.snackbar(getActivity(), "请先选择车辆");
            return;
        }
        Intent myintent = new Intent(getActivity(), ScanActivity.class);
        startActivityForResult(myintent, REQUESTCODE_SCAN);
    }

    @Override
    public void timeFinish() {
        timeCount.cancel();
        User.getInstance().setYuyueId(-1);
        User.getInstance().setAppointment_no("");
        SharePref.put(getActivity(), API.appointment_no, "");
        SharePref.put(getActivity(), API.siteid, "-1");
//        HashMap<String, String> map = new HashMap<>();
//        map.put("appointment_no", appointment_no);
//        new MyRequest().myRequest(API.cancelappoint, true, map, true, getActivity(), null, null, new Response() {
//            @Override
//            public void response(JsonObject json, JsonArray jsonArray, boolean isNull) {
//                getcurrentAppint();
//            }
//        });
        CancelAppointmentRequest cancelAppointmentRequest = new CancelAppointmentRequest();
        cancelAppointmentRequest.setAppointment_no(appointment_no);
        InternetWorkManager.getRequest().cancelAppointment(Utils.body(Mygson.getInstance().toJson(cancelAppointmentRequest)))
                .compose(RxHelper.observableIOMain(getActivity()))
                .subscribe(new MyObserver<User>() {
                    @Override
                    public void onSuccess(User data) {
                        getcurrentAppint();
                    }

                    @Override
                    public void onFail(int code, String message) {

                    }

                    @Override
                    public void onError() {

                    }

                    @Override
                    public void onClearToken() {
                        Utils.toLogin(getActivity());
                    }
                });
        if (stationPopup != null) {
            stationPopup.redelayDismiss.setVisibility(View.GONE);
            stationPopup.appoint.setVisibility(View.VISIBLE);
            stationPopup.time.setVisibility(View.GONE);
        }
    }

    private void bindClick() {
        binding.tvCity.setOnClickListener(this);
        binding.imgKefu.setOnClickListener(this);
        binding.reCarplate.setOnClickListener(this);
        binding.tvStationName.setOnClickListener(this);
        binding.qrcode.setOnClickListener(this);
        binding.scan.setOnClickListener(this);
        binding.msg.setOnClickListener(this);
        binding.recharge.setOnClickListener(this);
        binding.daka.setOnClickListener(this);
    }

    public void paysuccessLoad() {
        getcurrentAppint();
        Intent intent = new Intent(getActivity(), BillActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        //        mLocationClient.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        binding.mapview.onDestroy();
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
        }
        if (timeCount != null) {
            timeCount.cancelTime();
            timeCount = null;
        }
        if (privacyDialog != null) {
            privacyDialog.dismiss();
            privacyDialog = null;
        }
        super.onDestroy();
    }

//    @Override
//    public void onStop() {
//        super.onStop();
//        binding.banner.stopViewAnimator();
////        mLocationClient.stopLocation();
//    }


    @Override
    public void onDetach() {
        super.onDetach();
        binding.banner.stopViewAnimator();
    }

    private void setLocate() {
        if (GpsUtil.isOPen(getActivity())) {
            //设置定位回调监听
            mLocationClient.setLocationListener(mLocationListener);
            //初始化AMapLocationClientOption对象
            mLocationOption = new AMapLocationClientOption();
            //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //获取一次定位结果：
            //该方法默认为false。
            mLocationOption.setOnceLocation(true);
            mLocationClient.setLocationOption(mLocationOption);
            mLocationClient.startLocation();
        } else {
            new AlertDialog.Builder(getActivity()).setTitle("定位失败").setMessage("请检查是否开启定位服务").setPositiveButton("开启", (dialogInterface, i) -> {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, REQUESTCODE_GPS);
            }).setNegativeButton("取消", (dialogInterface, i) -> {
                dialogInterface.dismiss();
                User.getInstance().setCityName("");
            }).create().show();
        }
    }

    private void bluePointLocat() {
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);
        myLocationStyle.interval(1000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        //aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        aMap.getUiSettings().setRotateGesturesEnabled(false);
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
    }

    private void markerCreate(List<StationInfo> list, StationInfo s) {
        if (ListUtils.isEmpty(list)) {
            return;
        }
        int position = list.indexOf(s);
        for (Marker marker : markerList) {
            if (marker.getObject() instanceof StationInfo) {
                marker.remove();
            }
        }
        for (int i = 0; i < list.size(); i++) {
            StationInfo station = list.get(i);
            MarkerOptions markerOptions = new MarkerOptions();
            LatLng latLng = new LatLng(station.getLatitude(), station.getLongitude());
            markerOptions.position(latLng);
            boolean worktime = false;
            String startTime = DateUtils.timeTotime(station.getStart_time());
            String endTime = DateUtils.timeTotime(station.getEnd_time());
            worktime = DateUtils.isBelong(startTime, endTime);
            int mipmap = 0; // 0运营 1关闭 2维修
            if (station.getStatus() == 2) {
                if (station.getType() == 0) {
                    mipmap = R.mipmap.station_repair_map;
                } else {
                    mipmap = R.mipmap.bao_nopower_map;
                }
            } else {
                if (station.getBattery() > 0 && worktime) {
                    if (station.getType() == 0) {//0是换电站
                        mipmap = R.mipmap.station_working_map;
                    } else {
                        mipmap = R.mipmap.bao_working_map;
                    }
                } else if (station.getBattery() > 0 && !worktime) {//休息中
                    if (station.getType() == 0) {//0是换电站
                        mipmap = R.mipmap.station_rest_map;
                    } else {
                        mipmap = R.mipmap.bao_rest_map;
                    }
                } else if (worktime && station.getBattery() == 0) {//无电池
                    if (station.getType() == 0) {//0是换电站
                        mipmap = R.mipmap.station_working_map;
                    } else {
                        mipmap = R.mipmap.bao_nopower_map;
                    }
                } else {
                    if (station.getType() == 0) {//0是换电站
                        mipmap = R.mipmap.station_working_map;
                    } else {
                        mipmap = R.mipmap.bao_nopower_map;
                    }
                }
            }
            if (position == i) {
                bitmap = BitmapDescriptorFactory.fromBitmap(setImgSize(BitmapFactory.decodeResource(getResources(), mipmap), 128, 144));
            } else {
                bitmap = BitmapDescriptorFactory.fromBitmap(setImgSize(BitmapFactory.decodeResource(getResources(), mipmap), 96, 108));
            }
            markerOptions.icon(bitmap);
            Marker marker = aMap.addMarker(markerOptions);
            marker.setObject(station);
//            marker.setMarkerOptions(markerOptions);
            markerList.add(marker);
        }
    }

    private void markerAllCreate(List<StationAll> list, StationAll s) {
        if (ListUtils.isEmpty(list)) {
            return;
        }
        int position = list.indexOf(s);
        for (Marker marker : markerList) {
            if (marker.getObject() instanceof StationAll) {
                marker.remove();
            }
        }
        for (int i = 0; i < list.size(); i++) {
            StationAll station = list.get(i);
            MarkerOptions markerOptions = new MarkerOptions();
            LatLng latLng = new LatLng(station.getLatitude(), station.getLongitude());
            markerOptions.position(latLng);
            int mipmap = 0; // 0运营 1关闭 2维修
            if (station.getType() == 0) {
                mipmap = R.mipmap.station_working_map;
            } else {
                mipmap = R.mipmap.bao_working_map;
            }
            if (position == i) {
                bitmap = BitmapDescriptorFactory.fromBitmap(setImgSize(BitmapFactory.decodeResource(getResources(), mipmap), 128, 144));
            } else {
                bitmap = BitmapDescriptorFactory.fromBitmap(setImgSize(BitmapFactory.decodeResource(getResources(), mipmap), 96, 108));
            }
            markerOptions.icon(bitmap);
            Marker marker = aMap.addMarker(markerOptions);
            marker.setObject(station);
            markerList.add(marker);
        }
    }

    //地图显示刷新的位置
    private void refreshMap(double lat, double lng) {
        LatLng latLng = new LatLng(lat, lng);
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
    }

    public Bitmap setImgSize(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {//fragment show
            onresume();
            if (User.getInstance().isListchanged()) {//listfragment changed
                binding.tvCity.setText(User.getInstance().getCityName(getActivity()));
                User.getInstance().setMainchanged(false);
                refreshMap(User.getInstance().getLatitude(getActivity()), User.getInstance().getLongtitude(getActivity()));
            }
        }
    }

    public void onresume() {//mainfragment show
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        binding.mapview.onResume();
        infoGet();
        if (User.getInstance().getLatitude(getActivity()) != 0) {
            loadCity(User.getInstance().getLatitude(getActivity()), User.getInstance().getLongtitude(getActivity()), User.getInstance().getCityName(getActivity()));
        }
        loadDialogMessage();
        loadScrollMessage();
    }

    public void refresh() {
        binding.tvCity.setText(User.getInstance().getCityName(getActivity()));
        refreshMap(User.getInstance().getLatitude(getActivity()), User.getInstance().getLongtitude(getActivity()));
        User.getInstance().setMainchanged(true);
        if (User.getInstance().getLatitude(getActivity()) != 0) {
            loadCity(User.getInstance().getLatitude(getActivity()), User.getInstance().getLongtitude(getActivity()), User.getInstance().getCityName(getActivity()));
        }
    }

    private void loadDialogMessage() {
        InternetWorkManager.getRequest().messageList(3, 0, 20)
                .compose(RxHelper.observableIOMain(getActivity()))
                .subscribe(new MyObserver<List<Message>>() {
                    @Override
                    public void onSuccess(List<Message> data) {
                        if (data != null && data.size() > 0) {
                            showmessageDialog(data);
                        }
                    }

                    @Override
                    public void onFail(int code, String message) {

                    }

                    @Override
                    public void onError() {

                    }

                    @Override
                    public void onClearToken() {
                        Utils.toLogin(getActivity());
                    }
                });
    }

    private void showmessageDialog(List<Message> data) {
        if (data.size() > 0) {
            BaseDialog.showcodeDialog(getContext(), "温馨提示", data.get(0).getContent(), "知道了", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    data.remove(0);
                    showmessageDialog(data);
                }
            });
        }
    }

    private void loadScrollMessage() {
        InternetWorkManager.getRequest().messageList(2, 0, 1)
                .compose(RxHelper.observableIOMain(getActivity()))
                .subscribe(new MyObserver<List<Message>>() {
                    @Override
                    public void onSuccess(List<Message> data) {
                        if (data != null && data.size() > 0) {
                            Message message = data.get(0);
                            String ss = message.getContent();
                            if (!ss.equals("") && ss != null) {
                                binding.banner.setDatas(Utils.getStrList(ss, 18));
                                binding.message.setVisibility(View.VISIBLE);
                                binding.banner.startViewAnimator();
                                return;
                            }
                            binding.message.setVisibility(View.GONE);
                            binding.banner.stopViewAnimator();
                        }
                    }

                    @Override
                    public void onFail(int code, String message) {

                    }

                    @Override
                    public void onError() {

                    }

                    @Override
                    public void onClearToken() {
                        Utils.toLogin(getActivity());
                    }
                });
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.mapview.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        binding.mapview.onSaveInstanceState(outState);
    }


    @Override
    public void click(StationInfo station) {
        if (station == null) {
            binding.tvStationName.setText("");
            return;
        }
        if (ListUtils.isEmpty(stationlist)) {
            return;
        }
        markerCreate(stationlist, station);
        binding.tvStationName.setText(station.getName());
        if (station.getChildSites() != null && station.getChildSites().size() > 0) {//大站显示 大站无预约或不是我显示原来的  大站预约显示换电宝
            if (station.getChildSites().get(0).getId() == User.getInstance().getYuyueId()) {//预约中并且是当前的id
                showPopDialog(station.getChildSites().get(0));
                if (stationPopup != null) {
                    stationPopup.setStation(station.getChildSites().get(0));
                }
            } else {//不再预约中 或者不是当前的站点
                showStationAndBaoDialog(station);
            }
        } else {//小站显示
            showPopDialog(station);
            if (stationPopup != null) {
                stationPopup.setStation(station);
            }
        }
    }

    private void infoGet() {
        InternetWorkManager.getRequest().infoGet()
                .compose(RxHelper.observableIOMain(getActivity()))
                .subscribe(new Observer<ResponseModel<User>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull ResponseModel<User> userResponseModel) {
                        User user = userResponseModel.getData();
                        if (user == null) return;
                        Utils.shareprefload(user, getActivity());//存储个人信息和阿里云信息
                        User.getInstance().setAccountId(user.getAccountId());
                        if (user.getUnread() > 0) {
                            binding.imgMsg.setImageDrawable(getResources().getDrawable(R.mipmap.icon_message_unread));
                        } else {
                            binding.imgMsg.setImageDrawable(getResources().getDrawable(R.mipmap.icon_message));
                        }
                        if (user.getUse_bind() != null) {
                            if (user.getUse_bind().getFirst_exchange_sites() != null && !user.getUse_bind().getFirst_exchange_sites().equals("")) {
//                                BaseDialog.showcodeDialog(getContext(), "温馨提示", user.getUse_bind().getFirst_exchange_sites(), "知道了", new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                    }
//                                });
                                String s = user.getUse_bind().getFirst_exchange_sites();
                                try {
                                    if (s.contains("【") && s.contains("】")) {
                                        BaseDialog.showcodeblueDialog(getContext(), "温馨提示", s.substring(0, s.indexOf("【")), s.substring(s.indexOf("【")), "知道了", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                            }
                                        });
                                    }
                                }catch (Exception e){
                                }
                            }
                        }
                        Utils.setUseBind(getActivity(), user.getUse_bind());
                        if (!Utils.usebindisNotexist(user.getUse_bind())) {
                            binding.tvCarplate.setText(user.getUse_bind().getPlate_number());
                            if (user.getUse_bind().getBusiness_type() == 5) {//5是出租车司机
                                binding.daka.setVisibility(View.VISIBLE);
                            } else {
                                binding.daka.setVisibility(View.GONE);
                            }
                        } else {
                            binding.tvCarplate.setText("请选择车辆 >");
                        }
                        useBind = user.getUse_bind();
                        if (userResponseModel.getCode() == 7) {
                            BaseDialog.showcodeDialog(getActivity(), "温馨提示", userResponseModel.getMessage() + "", "去购买", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getActivity(), PackageListActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }
                        if (userResponseModel.getCode() == 5 || userResponseModel.getCode() == 4) {//4是过期，5是token不对，其他的错误暂时都是-1
                            Utils.toLogin(getActivity());
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }
}
