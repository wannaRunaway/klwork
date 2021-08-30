package com.kulun.energynet.main.fragment;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.AmapPageType;
import com.google.android.material.appbar.AppBarLayout;
import com.kulun.energynet.R;
import com.kulun.energynet.databinding.AdapterStationBinding;
import com.kulun.energynet.databinding.ListFragmentBinding;
import com.kulun.energynet.main.ChoseCityActivity;
import com.kulun.energynet.main.MainActivity;
import com.kulun.energynet.mine.DriverclockActivity;
import com.kulun.energynet.mine.MapCustomActivity;
import com.kulun.energynet.mine.MessageActivity;
import com.kulun.energynet.mine.MyCarActivity;
import com.kulun.energynet.mine.PackageListActivity;
import com.kulun.energynet.mine.RechargeActivity;
import com.kulun.energynet.mine.ScanActivity;
import com.kulun.energynet.model.ConsumeListModel;
import com.kulun.energynet.model.StationInfo;
import com.kulun.energynet.model.UseBind;
import com.kulun.energynet.model.User;
import com.kulun.energynet.network.InternetWorkManager;
import com.kulun.energynet.network.MyObserver;
import com.kulun.energynet.network.RxHelper;
import com.kulun.energynet.popup.ScanPopup;
import com.kulun.energynet.requestbody.AddAppointmentRequest;
import com.kulun.energynet.requestbody.ConsumeRequest;
import com.kulun.energynet.requestbody.ReservationRequest;
import com.kulun.energynet.requestbody.SiteRequest;
import com.kulun.energynet.network.API;
import com.kulun.energynet.utils.BaseDialog;
import com.kulun.energynet.utils.DateUtils;
import com.kulun.energynet.utils.Mygson;
import com.kulun.energynet.utils.SharePref;
import com.kulun.energynet.utils.Utils;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.trello.rxlifecycle4.components.support.RxFragment;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListFragment extends RxFragment implements View.OnClickListener {
    private ListFragmentBinding binding;
    private List<StationInfo> list = new ArrayList<>();
    public MyAdapter adapter;
    private final int REQUESTCODE_LIST = 89;
    //    private StationSelectFragment stationSelectFragment;
//    private int stationId = -1;
    private UseBind useBind;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, null);
        binding = DataBindingUtil.bind(view);
        initclick();
//        binding.yuyue.setOnClickListener(this);
//        useBind = (UseBind) getIntent().getSerializableExtra("useBind");
        useBind = Utils.getusebind(getActivity());
        if (useBind !=null) {
            binding.header.tvCarplate.setText(useBind.getPlate_number() + "");
        }
        binding.etStationName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    //此处做逻辑处理
                    loadData(User.getInstance().getLatitude(getActivity()), User.getInstance().getLongtitude(getActivity()), -1, User.getInstance().getCityid(getActivity()), binding.etStationName.getText().toString());
                    return true;
                }
                return false;
            }
        });
        adapter = new MyAdapter(list);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setNestedScrollingEnabled(false);
        binding.recyclerView.setAdapter(adapter);
        binding.smartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
//                binding.tvChoseStation.setText("全部");
                binding.etStationName.setText("");
                loadData(User.getInstance().getLatitude(getActivity()), User.getInstance().getLongtitude(getActivity()), -1, User.getInstance().getCityid(getActivity()), binding.etStationName.getText().toString());
                selectAll();
            }
        });
        binding.appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int offset = Math.abs(verticalOffset);
                //标题栏的渐变
                binding.appBar.setBackgroundColor(changeAlpha(Color.parseColor("#F5F5F5")
                        , Math.abs(offset * 1.0f) / appBarLayout.getTotalScrollRange()));
                binding.appBar.setBackgroundColor(changeAlpha(Color.parseColor("#F5F5F5")
                        , Math.abs(offset * 1.0f) / appBarLayout.getTotalScrollRange()));
            }
        });
        cityChosedInit();
        binding.tvAll.setSelected(true);
        return view;
    }

    private void cityChosedInit() {
        if (!TextUtils.isEmpty(User.getInstance().getCityName(getActivity()))) {
            binding.tvCity.setText(User.getInstance().getCityName(getActivity()));
        }
        loadData(User.getInstance().getLatitude(getActivity()), User.getInstance().getLongtitude(getActivity()), -1, User.getInstance().getCityid(getActivity()), binding.etStationName.getText().toString());
    }

    /**
     * 根据百分比改变颜色透明度
     */
    public int changeAlpha(int color, float fraction) {
        int alpha = (int) (Color.alpha(color) * fraction);
        return Color.argb(alpha, 250, 250, 250);
    }


    private void initclick() {
        binding.tvCity.setOnClickListener(this);
        binding.imgKefu.setOnClickListener(this);
//        binding.header.reCarplate.setOnClickListener(this);
//        binding.header.finish.setOnClickListener(this);
//        binding.header.qrcode.setOnClickListener(this);
//        binding.header.scan.setOnClickListener(this);
//        binding.header.msg.setOnClickListener(this);
//        binding.header.recharge.setOnClickListener(this);
//        binding.header.daka.setOnClickListener(this);
        binding.header.finish.setOnClickListener(new MyClickListener(getActivity(), this));
        binding.header.qrcode.setOnClickListener(new MyClickListener(getActivity(), this));
        binding.header.scan.setOnClickListener(new MyClickListener(getActivity(), this));
        binding.header.msg.setOnClickListener(new MyClickListener(getActivity(), this));
        binding.header.recharge.setOnClickListener(new MyClickListener(getActivity(), this));
        binding.header.daka.setOnClickListener(new MyClickListener(getActivity(), this));
        binding.header.reCarplate.setOnClickListener(new MyClickListener(getActivity(), this));
        binding.tvAll.setOnClickListener(this);
        binding.tvSwipStation.setOnClickListener(this);
        binding.tvSwipBao.setOnClickListener(this);
    }

    private void loadData(double latitude, double longitude, int type, int cityId, String search) {
        if (latitude == 0 || longitude == 0) {
            return;
        }
        SiteRequest siteRequest = new SiteRequest();
        siteRequest.setLongitude(longitude);
        siteRequest.setLatitude(latitude);
        siteRequest.setType(type);
        siteRequest.setCity_Id(cityId);
        siteRequest.setSearch(search);
        siteRequest.setaId(User.getInstance().getAccountId());
        InternetWorkManager.getRequest().sitelist(Utils.body(Mygson.getInstance().toJson(siteRequest)))
                .compose(RxHelper.observableIOMain(getActivity()))
                .subscribe(new MyObserver<List<StationInfo>>() {
                    @Override
                    public void onSuccess(List<StationInfo> data) {
                        Utils.finishRefresh(binding.smartRefresh);
                        list.clear();
                        list.addAll(data);
                        for (int i = 0; i < list.size(); i++) {
                            StationInfo stationInfo = list.get(i);
                            float distance = AMapUtils.calculateLineDistance(new LatLng(User.getInstance().getMylatitude(getActivity()), User.getInstance().getMylongtitude(getActivity())), new LatLng(stationInfo.getLatitude(), stationInfo.getLongitude()));
                            stationInfo.setDistances(distance);
                        }
                        Collections.sort(list);
                        List<StationInfo> frontlist = new ArrayList<>();
                        for (int i = 0; i < list.size(); i++) {
                            StationInfo stationInfo = list.get(i);
                            boolean worktime = false;
                            String startTime = DateUtils.timeTotime(stationInfo.getStart_time());
                            String endTime = DateUtils.timeTotime(stationInfo.getEnd_time());
                            worktime = DateUtils.isBelong(startTime, endTime);
                            if (stationInfo.getType() == 0) {//0换电站
                                if (stationInfo.getStatus() == 2 | stationInfo.getBattery() == 0 | !worktime) {
                                    frontlist.add(stationInfo);
                                    list.remove(stationInfo);
                                    i--;
                                }
                            } else if (stationInfo.getType() == 1) {//1充电站
                                if (stationInfo.getStatus() == 2 | stationInfo.getBattery() == 0 | !worktime | !stationInfo.isAppointment()) {
                                    frontlist.add(stationInfo);
                                    list.remove(stationInfo);
                                    i--;
                                }
                            }
                        }
                        list.addAll(frontlist);
                        boolean ishasHuandianbao = false;
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getType() == 1) {//0是换电站
                                ishasHuandianbao = true;
                            }
                        }
                        if (ishasHuandianbao) {
                            binding.header.finish.setVisibility(View.VISIBLE);
                        } else {
                            binding.header.finish.setVisibility(View.GONE);
                        }
                        adapter.notifyDataSetChanged();
                        binding.image.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);
                    }

                    @Override
                    public void onFail(int code, String message) {
                        Utils.finishRefresh(binding.smartRefresh);
                    }

                    @Override
                    public void onError() {
                        Utils.finishRefresh(binding.smartRefresh);
                    }

                    @Override
                    public void onClearToken() {
                        Utils.toLogin(getActivity());
                    }
                });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_kefu:
                Utils.loadkefu(getActivity(), binding.imgKefu);
                break;
            case R.id.tv_city:
                Intent intent = new Intent(getActivity(), ChoseCityActivity.class);
                startActivityForResult(intent, REQUESTCODE_LIST);
                break;
            case R.id.re_carplate:
//                if (Utils.getToken(getActivity()) == null || Utils.getToken(getActivity()).equals("")) {
//                    Utils.toLogin(getActivity());
//                    Utils.snackbar(getActivity(), "请先登陆");
//                    return;
//                }
                intent = new Intent(getActivity(), MyCarActivity.class);
                startActivity(intent);
                break;
            case R.id.finish://跳转到首页
//                ((MainActivity) getActivity()).listtomainClickOnePunchReserver();
                reservation();
                break;
            case R.id.qrcode:
//                if (Utils.getToken(getActivity()) == null || Utils.getToken(getActivity()).equals("")) {
//                    Utils.toLogin(getActivity());
//                    Utils.snackbar(getActivity(), "请先登陆");
//                    return;
//                }
                String carplate = "";
                if (Utils.usebindisNotexist(useBind)) {
                    carplate = "";
                } else {
                    carplate = useBind.getPlate_number();
                }
                BaseDialog.showQrDialog(getActivity(), carplate);
                break;
            case R.id.recharge:
//                if (Utils.getToken(getActivity()) == null || Utils.getToken(getActivity()).equals("")) {
//                    Utils.toLogin(getActivity());
//                    Utils.snackbar(getActivity(), "请先登陆");
//                    return;
//                }
                intent = new Intent(getActivity(), RechargeActivity.class);
                startActivity(intent);
                break;
            case R.id.daka:
//                if (Utils.getToken(getActivity()) == null || Utils.getToken(getActivity()).equals("")) {
//                    Utils.toLogin(getActivity());
//                    Utils.snackbar(getActivity(), "请先登陆");
//                    return;
//                }
                if (Utils.usebindisNotexist(useBind)) {
                    Utils.snackbar(getActivity(), "请先选择车辆");
                    return;
                }
                intent = new Intent(getActivity(), DriverclockActivity.class);
                startActivity(intent);
                break;
            case R.id.scan:
//                if (Utils.getToken(getActivity()) == null || Utils.getToken(getActivity()).equals("")) {
//                    Utils.toLogin(getActivity());
//                    Utils.snackbar(getActivity(), "请先登陆");
//                    return;
//                }
                if (Utils.usebindisNotexist(useBind)) {
                    Utils.snackbar(getActivity(), "请先选择车辆");
                    return;
                }
//                AndPermission.with(getActivity())
//                        .runtime()
//                        .permission(Permission.CAMERA)
//                        .onGranted(permissions -> {
//                            // Storage permission are allowed.
////                            Intent myintent = new Intent(MainActivity.getActivity(), ScanActivity.class);
////                            startActivityForResult(myintent, REQUESTCODE_SCAN);
//                        })
//                        .onDenied(permissions -> {
//                            // Storage permission are not allowed.
//                        })
//                        .start();
//                Intent myintent = new Intent(getActivity(), ScanActivity.class);
//                startActivityForResult(myintent, REQUESTCODE_SCAN);
                ((MainActivity)getActivity()).mainScan();
                break;
            case R.id.msg:
//                if (Utils.getToken(getActivity()) == null || Utils.getToken(getActivity()).equals("")) {
//                    Utils.toLogin(getActivity());
//                    Utils.snackbar(getActivity(), "请先登陆");
//                    return;
//                }
                intent = new Intent(getActivity(), MessageActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_all:
                selectAll();
                loadData(User.getInstance().getLatitude(getActivity()), User.getInstance().getLongtitude(getActivity()), -1, User.getInstance().getCityid(getActivity()), binding.etStationName.getText().toString());
                break;
            case R.id.tv_swip_bao:
                binding.tvAll.setSelected(false);
                binding.tvSwipBao.setSelected(true);
                binding.tvSwipStation.setSelected(false);
                binding.tvAll.setTextColor(getResources().getColor(R.color.black));
                binding.tvSwipBao.setTextColor(getResources().getColor(R.color.text_color));
                binding.tvSwipStation.setTextColor(getResources().getColor(R.color.black));
                loadData(User.getInstance().getLatitude(getActivity()), User.getInstance().getLongtitude(getActivity()), 1, User.getInstance().getCityid(getActivity()), binding.etStationName.getText().toString());
                break;
            case R.id.tv_swip_station:
                binding.tvAll.setSelected(false);
                binding.tvSwipBao.setSelected(false);
                binding.tvSwipStation.setSelected(true);
                binding.tvAll.setTextColor(getResources().getColor(R.color.black));
                binding.tvSwipBao.setTextColor(getResources().getColor(R.color.black));
                binding.tvSwipStation.setTextColor(getResources().getColor(R.color.text_color));
                loadData(User.getInstance().getLatitude(getActivity()), User.getInstance().getLongtitude(getActivity()), 0, User.getInstance().getCityid(getActivity()), binding.etStationName.getText().toString());
                break;
            default:
                break;
        }
    }

    private void selectAll(){
        binding.tvAll.setSelected(true);
        binding.tvSwipBao.setSelected(false);
        binding.tvSwipStation.setSelected(false);
        binding.tvAll.setTextColor(getResources().getColor(R.color.text_color));
        binding.tvSwipBao.setTextColor(getResources().getColor(R.color.black));
        binding.tvSwipStation.setTextColor(getResources().getColor(R.color.black));
    }

    private void reservation() {//一件预约
//        if (Utils.getToken(getActivity()) == null || Utils.getToken(getActivity()).equals("")) {
//            Utils.toLogin(getActivity());
//            Utils.snackbar(getActivity(), "请先登陆");
//            return;
//        }
        if (list.size() == 0) {
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
                        for (int i = 0; i < list.size(); i++) {
                            list.get(i).setIsreserverclick(false);
                            list.get(i).setListclick(false);
                        }
                        for (int i = 0; i < list.size(); i++) {
                            if (stationInfo.getId() == list.get(i).getId()){
                                list.get(i).setIsreserverclick(true);
                                list.get(i).setListclick(true);
                            }
                        }
                        adapter.notifyDataSetChanged();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE_LIST && resultCode == 1) {
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
            int cityId = data.getIntExtra(API.cityId, 0);
            User.getInstance().setCityid(cityId);
            SharePref.put(getActivity(), API.cityId, cityId + "");
//            binding.tvChoseStation.setText("全部");
            binding.etStationName.setText("");
            loadData(User.getInstance().getLatitude(getActivity()), User.getInstance().getLongtitude(getActivity()), -1, User.getInstance().getCityid(getActivity()), binding.etStationName.getText().toString());
            User.getInstance().setListchanged(true);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            onresume();
            if (User.getInstance().isMainchanged()) {//fragment show && mainfragment chosedcity
                cityChosedInit();
                User.getInstance().setMainchanged(false);
            }
        }
    }

    public void onresume() {//listfragment show
        infoGet();
    }

    private void infoGet() {
        InternetWorkManager.getRequest().infoGet()
                .compose(RxHelper.observableIOMain(getActivity()))
                .subscribe(new MyObserver<User>() {
                    @Override
                    public void onSuccess(User user) {
                        if (user == null) return;
                        Utils.shareprefload(user, getActivity());//存储个人信息和阿里云信息
                        if (user.getUnread() > 0) {
                            binding.header.imgMsg.setImageDrawable(getResources().getDrawable(R.mipmap.icon_message_unread));
                        } else {
                            binding.header.imgMsg.setImageDrawable(getResources().getDrawable(R.mipmap.icon_message));
                        }
                        Utils.setUseBind(getActivity(), user.getUse_bind());
                        if (!Utils.usebindisNotexist(user.getUse_bind())) {
                            binding.header.tvCarplate.setText(user.getUse_bind().getPlate_number());
                            if (user.getUse_bind().getBusiness_type() == 5) {//5是出租车司机
                                binding.header.daka.setVisibility(View.VISIBLE);
                            } else {
                                binding.header.daka.setVisibility(View.GONE);
                            }
                        }
                        useBind = user.getUse_bind();
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

    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private List<StationInfo> myDatas;

        public MyAdapter(List<StationInfo> list) {
            this.myDatas = list;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(getActivity()).inflate(R.layout.adapter_station, null);
            AdapterStationBinding binding = AdapterStationBinding.bind(view);
            MyViewHolder holder = new MyViewHolder(view, binding);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            StationInfo station = myDatas.get(position);
            if (station.isListclick()) {
                holder.getBinding().content.setBackground(getResources().getDrawable(R.drawable.station_item1));
            } else {
                holder.getBinding().content.setBackground(getResources().getDrawable(R.drawable.station_item2));
            }
            if (station.isIsreserverclick()) {
                holder.getBinding().tvYue.setVisibility(View.VISIBLE);
                holder.getBinding().tvDistance.setVisibility(View.GONE);
            } else {
                holder.getBinding().tvYue.setVisibility(View.GONE);
                holder.getBinding().tvDistance.setVisibility(View.VISIBLE);
            }
            boolean worktime = false;
            String startTime = DateUtils.timeTotime(station.getStart_time());
            String endTime = DateUtils.timeTotime(station.getEnd_time());
            worktime = DateUtils.isBelong(startTime, endTime);
            boolean finalWorktime = worktime;
            holder.getBinding().re.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < myDatas.size(); i++) {
                        myDatas.get(i).setListclick(false);
                        myDatas.get(i).setIsreserverclick(false);
                    }
                    station.setListclick(true);
                    if (station.isAppointment()) {
                        if (finalWorktime && station.getBattery() > 0 && station.getStatus() != 2) {//在运营时间内，且电池数量》0,并且不再维修状态
                            station.setIsreserverclick(true);
                        } else {
                            station.setIsreserverclick(false);
                        }
                    } else {
                        station.setIsreserverclick(false);
                    }
//                    stationId = station.getId();
                    adapter.notifyDataSetChanged();
                }
            });
            int lefticon = 0;
            String text_sign;
            int color_sign, drawable_clock, drawable_sign;
            if (station.getStatus() == 2) {
                text_sign = "维修中";
                color_sign = R.color.list_gray;
                drawable_clock = R.drawable.shape_gray;
                drawable_sign = R.drawable.shape_fill_gray;
                if (station.getType() == 0) {
                    lefticon = R.mipmap.station_repair;
                } else {
                    lefticon = R.mipmap.bao_nopower;
                }
            } else {
                if (station.getBattery() > 0 && worktime) {
                    if (station.getType() == 0) {//0是换电站
                        text_sign = "运营中";
                        color_sign = R.color.text_color;
                        drawable_clock = R.drawable.shape_blue;
                        lefticon = R.mipmap.station_icon;
                        drawable_sign = R.drawable.shape_fill_blue;
                    } else {
                        if (station.isAppointment()) {
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
                } else if (station.getBattery() > 0 && !worktime) {//休息中
                    text_sign = "休息中";
                    color_sign = R.color.yellow;
                    drawable_clock = R.drawable.shape_yellow;
                    drawable_sign = R.drawable.shape_fill_yellow;
                    if (station.getType() == 0) {//0是换电站
                        lefticon = R.mipmap.station_rest;
                    } else {
                        lefticon = R.mipmap.bao_rest;
                    }
                } else if (worktime && station.getBattery() == 0) {//无电池
                    if (station.getType() == 0) {//0是换电站
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
                    if (station.getType() == 0) {//0是换电站
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
            holder.getBinding().tvSign.setText(text_sign);
            holder.getBinding().tvSign.setBackground(getResources().getDrawable(drawable_sign));
            holder.getBinding().tvClock.setBackground(getResources().getDrawable(drawable_clock));
            holder.getBinding().tvClock.setTextColor(getResources().getColor(color_sign));
            if (station.getParkFee().equals("")){
                holder.getBinding().tvFareStop.setVisibility(View.GONE);
            }else {
                holder.getBinding().tvFareStop.setVisibility(View.VISIBLE);
                holder.getBinding().tvFareStop.setText("停车"+station.getParkFee()+"元/小时");
            }
            Drawable leftDrawable = getResources().getDrawable(lefticon);
            leftDrawable.setBounds(0, 0, 80, 80);
            holder.getBinding().tvName.setCompoundDrawables(leftDrawable, null, null, null);
            Drawable distancedrawable = getResources().getDrawable(R.mipmap.station_record_guide);
            distancedrawable.setBounds(0, 0, 60, 60);
            holder.getBinding().tvDistance.setCompoundDrawables(null, distancedrawable, null, null);
            holder.getBinding().tvName.setText(station.getName());
            holder.getBinding().tvAddress.setText(station.getAddress());
            holder.getBinding().tvPhone.setText(station.getPhone());
            holder.getBinding().tvPhone.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + station.getPhone());
                intent.setData(data);
                startActivity(intent);
            });
            holder.getBinding().tvClock.setText(DateUtils.datetoTime(station.getStart_time()) + "~" + DateUtils.datetoTime(station.getEnd_time()));
            if (station.getType() == 0) {
                holder.getBinding().tvPaidui.setText("排队人数：" + station.getWaiting());
                holder.getBinding().tvKucun.setVisibility(View.VISIBLE);
            } else if (station.getType() == 1) {
                holder.getBinding().tvPaidui.setText("可换电池：" + station.getBattery());
                holder.getBinding().tvKucun.setVisibility(View.INVISIBLE);
            }
            holder.getBinding().tvKucun.setText(station.getBattery() > 0 ? "电池库存：有" : "电池库存：无");
            float distance = AMapUtils.calculateLineDistance(new LatLng(User.getInstance().getMylatitude(getActivity()), User.getInstance().getMylongtitude(getActivity())), new LatLng(station.getLatitude(), station.getLongitude()));
            DecimalFormat df = new DecimalFormat("#.00");
            if (distance < 1000) {
                holder.getBinding().tvDistance.setText("导航\n" + df.format(distance) + "m");
            } else {
                holder.getBinding().tvDistance.setText("导航\n" + df.format(distance / 1000) + "km");
            }
            holder.getBinding().tvDistance.setOnClickListener(view ->
            {
                if (User.getInstance().getYuyueId() == -1 && station.getType() == 1) {
                    Utils.snackbar(getActivity(), "请预约成功后前往换电");
                } else {
                    Poi start = new Poi("", new com.amap.api.maps.model.LatLng(User.getInstance().getMylatitude(getActivity()), User.getInstance().getMylongtitude(getActivity())), "");
                    /**终点传入的是北京站坐标,但是POI的ID "B000A83M61"对应的是北京西站，所以实际算路以北京西站作为终点**/
                    /**Poi支持传入经纬度和PoiID，PoiiD优先级更高，使用Poiid算路，导航终点会更合理**/
                    Poi end = new Poi("", new com.amap.api.maps.model.LatLng(station.getLatitude(), station.getLongitude()), "");
                    AmapNaviPage.getInstance().showRouteActivity(getContext(), new AmapNaviParams(start, null, end, AmapNaviType.DRIVER, AmapPageType.NAVI), null, MapCustomActivity.class);
                }
            });
            holder.getBinding().tvYue.setOnClickListener(v -> {
                if (useBind == null) {
                    Utils.snackbar("请先绑定车辆");
                    return;
                }
                AddAppointmentRequest addAppointmentRequest = new AddAppointmentRequest();
                addAppointmentRequest.setSite_id(station.getId());
                addAppointmentRequest.setCar_id(useBind.getCar_id());
                InternetWorkManager.getRequest().addAppointment(Utils.body(Mygson.getInstance().toJson(addAppointmentRequest)))
                        .compose(RxHelper.observableIOMain(getActivity()))
                        .subscribe(new MyObserver<User>() {
                            @Override
                            public void onSuccess(User data) {
                                Utils.snackbar(getActivity(), "预约成功");
                                ((MainActivity) getActivity()).listomainFragmentClickAdapterReserver();
                            }
                            @Override
                            public void onFail(int code, String message) {
                                ((MainActivity) getActivity()).listomainFragmentClickAdapterReserver();
                            }

                            @Override
                            public void onError() {
                            }
                            @Override
                            public void onClearToken() {
                                Utils.toLogin(getActivity());
                            }
                        });
            });
        }

        @Override
        public int getItemCount() {
            return myDatas.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        private AdapterStationBinding binding;

        public MyViewHolder(View itemView, AdapterStationBinding binding) {
            super(itemView);
            this.binding = binding;
        }

        public AdapterStationBinding getBinding() {
            return binding;
        }
    }
}
