package com.kulun.energynet.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kulun.energynet.R;
import com.kulun.energynet.databinding.ActivityCarSelectorBinding;
import com.kulun.energynet.main.fragment.MainFragment;
import com.kulun.energynet.model.StationInfo;
import com.kulun.energynet.model.User;
import com.kulun.energynet.network.API;
import com.kulun.energynet.network.InternetWorkManager;
import com.kulun.energynet.network.MyObserver;
import com.kulun.energynet.network.RxHelper;
import com.kulun.energynet.requestbody.SiteRequest;
import com.kulun.energynet.utils.DateUtils;
import com.kulun.energynet.utils.Mygson;
import com.kulun.energynet.utils.SharePref;
import com.kulun.energynet.utils.Utils;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

/**
 * created by xuedi on 2019/9/9
 */
public class StationSelectActivity extends DialogFragment {
    private ActivityCarSelectorBinding binding;
    private List<StationInfo> list = new ArrayList<>();
    private CarSelectorAdapter adapter;
    private final int REQUESTCODE_MAIN = 64;
    private MainFragment mainFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_car_selector, null);
        binding = DataBindingUtil.bind(view);
        initView();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_NoActionBar_Fullscreen);
    }

    public StationSelectActivity(MainFragment mainFragment) {
        this.mainFragment = mainFragment;
    }

    public void initView() {
        binding.tvCity.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChoseCityActivity.class);
            startActivityForResult(intent, REQUESTCODE_MAIN);
        });
        binding.tvCancel.setOnClickListener(v -> {
            dismissAllowingStateLoss();
        });
        if (User.getInstance().getCityName(getActivity()) != null && !User.getInstance().getCityName(getActivity()).equals("")) {
            binding.tvCity.setText(User.getInstance().getCityName(getActivity()));
        }
        adapter = new CarSelectorAdapter(getContext(), list);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);
        binding.smartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadData(User.getInstance().getLatitude(getActivity()), User.getInstance().getLongtitude(getActivity()), -1, User.getInstance().getCityid(getActivity()), binding.etStationName.getText().toString());
            }
        });
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
        binding.smartRefresh.autoRefresh();
    }

    private void loadData(double latitude, double longtitude, int i, int cityid, String string) {
        SiteRequest siteRequest = new SiteRequest();
        siteRequest.setLongitude(longtitude);
        siteRequest.setLatitude(latitude);
        siteRequest.setType(-1);
        siteRequest.setCity_Id(cityid);
        siteRequest.setaId(User.getInstance().getAccountId());
        siteRequest.setSearch(string);
        InternetWorkManager.getRequest().sitelist(Utils.body(Mygson.getInstance().toJson(siteRequest)))
                .compose(RxHelper.observableIOMain(getActivity()))
                .subscribe(new MyObserver<List<StationInfo>>() {
                    @Override
                    public void onSuccess(List<StationInfo> data) {
                        Utils.finishRefresh(binding.smartRefresh);
                        list.clear();
                        list.addAll(data);
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

    private String cityName;
    private double lat, lon;
    private int cityid;
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE_MAIN && resultCode == 1) {
            cityName = data.getStringExtra(API.cityName);
            binding.tvCity.setText(cityName);
            User.getInstance().setCityName(cityName);
            SharePref.put(getActivity(), API.cityName, cityName);
            lat = data.getDoubleExtra(API.latitude, 0);
            User.getInstance().setLatitude(lat);
            SharePref.put(getActivity(), API.latitude, lat + "");
            lon = data.getDoubleExtra(API.longtitude, 0);
            User.getInstance().setLongtitude(lon);
            SharePref.put(getActivity(), API.longtitude, lon + "");
            cityid = data.getIntExtra(API.cityId, 0);
            User.getInstance().setCityid(cityid);
            SharePref.put(getActivity(), API.cityId, cityid + "");
            binding.smartRefresh.autoRefresh();
            ((MainFragment)mainFragment).refresh();
        }
    }

    private class CarSelectorAdapter extends RecyclerView.Adapter<CarSelectorAdapter.CarSelectorViewHolder> {
        private Context context;
        private List<StationInfo> stationList;

        public CarSelectorAdapter(Context context, List<StationInfo> list) {
            this.context = context;
            this.stationList = list;
        }

        @Override
        public CarSelectorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_carselectdialog, null);
            CarSelectorViewHolder holder = new CarSelectorViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(CarSelectorViewHolder holder, int position) {
            holder.re_contain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mainFragment.click(stationList.get(position));
                    dismissAllowingStateLoss();
//                    Intent intent = new Intent(getActivity(), MainActivity.class);
//                    intent.putExtra("data", stationList.get(position));
//                    startActivity(intent);
                }
            });
            holder.car_num.setText(stationList.get(position).getName());
            holder.tv_address.setText(stationList.get(position).getAddress());
            StationInfo stationInfo = stationList.get(position);
            boolean worktime = false;
            String startTime = DateUtils.timeTotime(stationInfo.getStart_time());
            String endTime = DateUtils.timeTotime(stationInfo.getEnd_time());
            worktime = DateUtils.isBelong(startTime, endTime);
            int lefticon = 0;
            if (stationInfo.getStatus() == 2) {
                if (stationInfo.getType() == 0) {
                    lefticon = R.mipmap.station_repair;
                } else {
                    lefticon = R.mipmap.bao_nopower;
                }
            } else {
                if (stationInfo.getBattery() > 0 && worktime) {
                    if (stationInfo.getType() == 0) {//0是换电站
                        lefticon = R.mipmap.station_icon;
                    } else {
                        lefticon = R.mipmap.bao_working;
                    }
                } else if (stationInfo.getBattery() > 0 && !worktime) {//休息中
                    if (stationInfo.getType() == 0) {//0是换电站
                        lefticon = R.mipmap.station_rest;
                    } else {
                        lefticon = R.mipmap.bao_rest;
                    }
                } else if (worktime && stationInfo.getBattery() == 0) {//无电池
                    if (stationInfo.getType() == 0) {//0是换电站
                        lefticon = R.mipmap.station_icon;
                    } else {
                        lefticon = R.mipmap.bao_nopower;
                    }
                } else {
                    if (stationInfo.getType() == 0) {//0是换电站
                        lefticon = R.mipmap.station_icon;
                    } else {
                        lefticon = R.mipmap.bao_nopower;
                    }
                }
            }
            holder.imageView.setImageDrawable(getResources().getDrawable(lefticon));
        }

        @Override
        public int getItemCount() {
            return stationList.size();
        }

        class CarSelectorViewHolder extends RecyclerView.ViewHolder {
            private TextView car_num, tv_address;
            private RelativeLayout re_contain;
            private ImageView imageView;

            public CarSelectorViewHolder(View itemView) {
                super(itemView);
                car_num = itemView.findViewById(R.id.tv_station);
                re_contain = itemView.findViewById(R.id.re_contain);
                imageView = itemView.findViewById(R.id.image);
                tv_address = itemView.findViewById(R.id.tv_address);
            }
        }
    }
}
