package com.kulun.energynet.main;

import android.content.Intent;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import androidx.annotation.NonNull;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.kulun.energynet.R;
import com.kulun.energynet.databinding.ActivityChoseBinding;
import com.kulun.energynet.model.City;
import com.kulun.energynet.model.ResponseModel;
import com.kulun.energynet.model.User;
import com.kulun.energynet.network.InternetWorkManager;
import com.kulun.energynet.network.MyObserver;
import com.kulun.energynet.network.RxHelper;
import com.kulun.energynet.requestparams.Response;
import com.kulun.energynet.network.API;
import com.kulun.energynet.utils.Mygson;
import com.kulun.energynet.requestparams.MyRequest;
import com.kulun.energynet.utils.Utils;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ChoseCityActivity extends BaseActivity {
    private ActivityChoseBinding binding;
    private List<City> cityList = new ArrayList<>();
    private MyAdapter adapter;

    @Override
    public void initView(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chose);
        binding.header.title.setText("选择城市");
        binding.header.left.setOnClickListener(view -> finish());
        adapter = new MyAdapter(cityList);
        binding.gridview.setAdapter(adapter);
        binding.gridview.setVerticalSpacing(60);
        if (TextUtils.isEmpty(User.getInstance().getCityName(this))) {
            binding.citySelect.setText("定位失败");
        } else {
            if (User.getInstance().getCityName(this).equals("")) {
                binding.citySelect.setText("定位失败");
            } else {
                binding.citySelect.setText(User.getInstance().getCityName(this));
            }
        }

        binding.smartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadCity();
            }
        });
        loadCity();
    }

    private void loadCity() {
        InternetWorkManager.getRequest().citylist()
                .compose(RxHelper.observableIOMain(this))
                .subscribe(new MyObserver<List<City>>(){
                    @Override
                    public void onSuccess(List<City> data) {
                        Utils.finishRefresh(binding.smartRefresh);
                        cityList.clear();
                        cityList.addAll(data);
                        adapter.notifyDataSetChanged();
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
                        Utils.toLogin(ChoseCityActivity.this);
                    }
                });
//        new MyRequest().myRequest(API.CITYLIST, false, null, false, this, null, binding.smartRefresh, new Response() {
//            @Override
//            public void response(JsonObject json, JsonArray jsonArray,boolean isNull) {
//                cityList.clear();
//                cityList.addAll(Mygson.getInstance().fromJson(jsonArray, new TypeToken<List<City>>() {
//                }.getType()));
//                adapter.notifyDataSetChanged();
//            }
//        });
    }

    private class MyAdapter extends BaseAdapter {
        private List<City> list;

        public MyAdapter(List<City> content) {
            this.list = content;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            City dataBean = list.get(i);
            View myView = LayoutInflater.from(ChoseCityActivity.this).inflate(R.layout.gridview_item, viewGroup, false);
            TextView carplate = myView.findViewById(R.id.carplate);
            carplate.setText(dataBean.getName());
            carplate.setOnClickListener(view1 -> {
                Intent intent = new Intent();
//                intent.putExtra(API.cityId, dataBean.getId());
                intent.putExtra(API.longtitude, dataBean.getLongitude());
                intent.putExtra(API.latitude, dataBean.getLatitude());
                intent.putExtra(API.cityName, dataBean.getName());
                intent.putExtra(API.cityId, dataBean.getId());
                setResult(1, intent);
                finish();
            });
            return myView;
        }
    }
}
