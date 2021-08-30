package com.kulun.energynet.mine;

import android.content.Intent;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kulun.energynet.R;
import com.kulun.energynet.databinding.ActivityMyActivityBinding;
import com.kulun.energynet.main.BaseActivity;
import com.kulun.energynet.model.Activity;
import com.kulun.energynet.model.ResponseModel;
import com.kulun.energynet.network.InternetWorkManager;
import com.kulun.energynet.network.MyObserver;
import com.kulun.energynet.network.RxHelper;
import com.kulun.energynet.utils.Utils;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PackageListActivity extends BaseActivity {

    private ActivityMyActivityBinding binding;
    private Myadapter myadapter;
    private List<Activity> list = new ArrayList<>();

    @Override
    public void initView(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_activity);
        binding.header.title.setText("活动列表");
        binding.header.left.setOnClickListener(view -> finish());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myadapter = new Myadapter(list);
        binding.recyclerView.setAdapter(myadapter);
        binding.smartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadData();
            }
        });
        loadData();
    }

    //pageNo[int]	是	第几页，默认1
    //pageSize[int]	是	页/条，默认20
    private void loadData() {
//        new MyRequest().myRequestNoBack(API.activitylist, true, null, false, this, null, binding.smartRefresh, new ResponseCode() {
//            @Override
//            public void response(JsonObject json, JsonArray jsonArray, boolean isNull, int code, String message) {
//                if (code != 0){
//                    binding.image.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);
//                }
//                if (jsonArray != null){
//                    list.clear();
//                    list.addAll(Mygson.getInstance().fromJson(jsonArray, new TypeToken<List<Activity>>() {
//                    }.getType()));
//                    binding.image.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);
//                    myadapter.notifyDataSetChanged();
//                }
//            }
//        });
        InternetWorkManager.getRequest().activityList()
                .compose(RxHelper.observableIOMain(this))
                .subscribe(new MyObserver<List<Activity>>() {
                    @Override
                    public void onSuccess(List<Activity> data) {
                        Utils.finishRefresh(binding.smartRefresh);
                        if (data != null) {
                            list.clear();
                            list.addAll(data);
                            binding.image.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);
                            myadapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFail(int code, String message) {
                        Utils.finishRefresh(binding.smartRefresh);
                        binding.image.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);
                    }

                    @Override
                    public void onError() {
                        Utils.finishRefresh(binding.smartRefresh);
                    }

                    @Override
                    public void onClearToken() {
                        Utils.toLogin(PackageListActivity.this);
                    }
                });
    }

    private class Myadapter extends RecyclerView.Adapter<MyViewHolder> {
        private List<Activity> list;

        public Myadapter(List<Activity> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(PackageListActivity.this).inflate(R.layout.adapter_activity, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Activity data = list.get(position);
            holder.name.setText(data.getName());
            holder.time.setText(data.getStartTime() + "至" + data.getEndTime());
            holder.re.setOnClickListener(view -> {
                Intent intent = new Intent(PackageListActivity.this, PackageDetailActivity.class);
                intent.putExtra("data", (Serializable) data);
                startActivity(intent);
//                finish();
            });
            if (data.getPhoto() != null && !data.getPhoto().equals("")) {
                Glide.with(PackageListActivity.this).load(data.getPhoto()).into(holder.imageView);
            } else {
                holder.imageView.setImageDrawable(getResources().getDrawable(R.mipmap.activity_icon));
            }
//            holder.text1.setText("费用:"+data.getPackagePrice());
//            holder.text2.setText("");
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name, time, text1, text2;
        private RelativeLayout re;
        private ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_name);
            time = itemView.findViewById(R.id.tv_time);
            re = itemView.findViewById(R.id.re);
            imageView = itemView.findViewById(R.id.image);
//            text1 = itemView.findViewById(R.id.text1);
//            text2 = itemView.findViewById(R.id.text2);
        }
    }
}
