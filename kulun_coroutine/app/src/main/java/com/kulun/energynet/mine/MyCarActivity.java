package com.kulun.energynet.mine;

import android.content.Intent;

import androidx.databinding.DataBindingUtil;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kulun.energynet.R;
import com.kulun.energynet.databinding.ActivityMyCarBinding;
import com.kulun.energynet.databinding.AdapterCarBinding;
import com.kulun.energynet.main.BaseActivity;
import com.kulun.energynet.model.Car;
import com.kulun.energynet.model.ResponseModel;
import com.kulun.energynet.model.User;
import com.kulun.energynet.network.InternetWorkManager;
import com.kulun.energynet.network.MyObserver;
import com.kulun.energynet.network.RxHelper;
import com.kulun.energynet.requestbody.SetCurrentCarRequest;
import com.kulun.energynet.requestbody.UnbindCarRequest;
import com.kulun.energynet.network.API;
import com.kulun.energynet.utils.BaseDialog;
import com.kulun.energynet.utils.Mygson;
import com.kulun.energynet.utils.Utils;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import me.leefeng.promptlibrary.PromptDialog;

public class MyCarActivity extends BaseActivity implements View.OnClickListener {
    private ActivityMyCarBinding binding;
    private MyAdapter adapter;
    private List<Car> list = new ArrayList<>();
    private PromptDialog promptDialog;

    @Override
    public void initView(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_car);
        binding.header.left.setOnClickListener(this);
        binding.header.title.setText("我的车");
        binding.header.right.setText("绑定申请");
        binding.imgAddCar.setOnClickListener(this);
        adapter = new MyAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(MyCarActivity.this));
        binding.recyclerView.setAdapter(adapter);
        binding.smartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadData();
            }
        });
        binding.header.right.setOnClickListener(this);
        promptDialog = new PromptDialog(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        InternetWorkManager.getRequest().carlist()
                .compose(RxHelper.observableIOMain(this))
                .subscribe(new MyObserver<List<Car>>() {
                    @Override
                    public void onSuccess(List<Car> data) {
                        Utils.finishRefresh(binding.smartRefresh);
                        list.clear();
                        if (data != null && data.size() > 0) {
                            list.addAll(data);
                        }
                        adapter.notifyDataSetChanged();
                        binding.image.setVisibility(list.size() > 0 ? View.GONE : View.VISIBLE);
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
                        Utils.toLogin(MyCarActivity.this);
                    }
                });
//        new MyRequest().myRequest(API.getmycarlist, false, null, false, this, null, binding.smartRefresh, new Response() {
//            @Override
//            public void response(JsonObject json, JsonArray jsonArray, boolean isNull) {
//                if (jsonArray != null) {
//                    list.clear();
//                    list.addAll(Mygson.getInstance().fromJson(jsonArray, new TypeToken<List<Car>>() {
//                    }.getType()));
//                    adapter.notifyDataSetChanged();
//                    binding.image.setVisibility(list.size() > 0 ? View.GONE : View.VISIBLE);
//                }
//            }
//        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left:
                finish();
                break;
            case R.id.img_add_car:
                loadinfo();
                break;
            case R.id.right:
                Intent intent1 = new Intent(MyCarActivity.this, CarbindApplyActivity.class);
                startActivity(intent1);
                break;
        }
    }

    private void loadinfo() {
        InternetWorkManager.getRequest().infoGet()
                .compose(RxHelper.observableIOMain(this))
                .subscribe(new MyObserver<User>() {
                    @Override
                    public void onSuccess(User user) {
                        if (user != null) {
                            if (!TextUtils.isEmpty(user.getName()) && !TextUtils.isEmpty(user.getIdentity())) {
                                AndPermission.with(MyCarActivity.this)
                                        .runtime()
                                        .permission(Permission.CAMERA)
                                        .onGranted(permissions -> {
                                            Intent intent = new Intent(MyCarActivity.this, AddCarActivity.class);
                                            startActivity(intent);
                                        })
                                        .onDenied(permissions -> {
                                            // Storage permission are not allowed.
                                        })
                                        .start();
                            } else {
                                uploadInfo();
                            }
                        }
                    }

                    @Override
                    public void onFail(int code, String message) {
                        uploadInfo();
                    }

                    @Override
                    public void onError() {
                    }

                    @Override
                    public void onClearToken() {
                        Utils.toLogin(MyCarActivity.this);
                    }
                });
//        new MyRequest().myRequest(API.INFO, false, null, true, this, null, null, new Response() {
//            @Override
//            public void response(JsonObject json, JsonArray jsonArray, boolean isNull) {
//                if (json == null || isNull) {
//                    uploadInfo();
//                    return;
//                }
//                User user = Mygson.getInstance().fromJson(json, User.class);
//                if (user != null) {
//                    if (!TextUtils.isEmpty(user.getName()) && !TextUtils.isEmpty(user.getIdentity())) {
//                        AndPermission.with(MyCarActivity.this)
//                                .runtime()
//                                .permission(Permission.CAMERA)
//                                .onGranted(permissions -> {
//                                    Intent intent = new Intent(MyCarActivity.this, AddCarActivity.class);
//                                    startActivity(intent);
//                                })
//                                .onDenied(permissions -> {
//                                    // Storage permission are not allowed.
//                                })
//                                .start();
//                    } else {
//                        uploadInfo();
//                    }
//                } else {
//                    uploadInfo();
//                }
//            }
//        });
    }

    private void uploadInfo() {
        BaseDialog.showDialog(MyCarActivity.this, "温馨提示","请您上传个人信息","确认", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyCarActivity.this, PersonalActivity.class);
                intent.putExtra(API.register, false);
                startActivity(intent);
            }
        });
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MyCarActivity.this).inflate(R.layout.adapter_car, parent, false);
            AdapterCarBinding adapterCarBinding = DataBindingUtil.bind(view);
            MyViewHolder holder = new MyViewHolder(view);
            holder.setBinding(adapterCarBinding);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            //bind_status=0解绑bind_status=1绑定中bind_status=2解绑中bind_status=3锁定
            Car car = list.get(position);
            holder.getBinding().name.setText(car.getPlate_number());
            holder.getBinding().t1.setText(car.getSoc() + "%");
            holder.getBinding().t2.setText(car.getCar_type());
            holder.getBinding().t3.setText(car.isBattery_status() ? "正常" : "不正常");
            Drawable circledraable = getResources().getDrawable(R.drawable.recharge_choice);
            circledraable.setBounds(0, 0, 55, 55);
            holder.getBinding().now.setCompoundDrawables(circledraable, null, null, null);
            if (car.getBind_status() == 1) {
                Drawable drawable = getResources().getDrawable(R.mipmap.sign_binding);
                drawable.setBounds(0, 0, 100, 50);
                holder.getBinding().name.setCompoundDrawables(null, null, drawable, null);
            } else if (car.getBind_status() == 3) {
                Drawable drawable = getResources().getDrawable(R.mipmap.sign_lock);
                drawable.setBounds(0, 0, 100, 50);
                holder.getBinding().name.setCompoundDrawables(null, null, drawable, null);
            }
            if (car.getMonth_mile() == 0) {
                holder.getBinding().progressbar.setVisibility(View.GONE);
                holder.getBinding().mile.setVisibility(View.GONE);
            } else {
                holder.getBinding().mile.setVisibility(View.VISIBLE);
                holder.getBinding().progressbar.setVisibility(View.VISIBLE);
                holder.getBinding().mile.setText("当前剩余：" + car.getLeft_mile() + "  |  " + "套餐总量：" + car.getMonth_mile());
                holder.getBinding().progressbar.setProgress(car.getLeft_mile() * 100 / car.getMonth_mile());
            }
            holder.getBinding().now.setOnClickListener(v -> {
                dangqian(car.getId());
            });
            holder.getBinding().unbind.setOnClickListener(v -> {
                BaseDialog.showDialog(MyCarActivity.this,"温馨提示", "是否确认解绑","确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelCar(car.getPlate_number());
                    }
                });
            });
            if (car.isInuse()) {
                holder.getBinding().now.setSelected(true);
            } else {
                holder.getBinding().now.setSelected(false);
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    private void dangqian(int id) {
        //bind_id
        promptDialog.showLoading("正在设为当前车辆");
        SetCurrentCarRequest setCurrentCarRequest = new SetCurrentCarRequest();
        setCurrentCarRequest.setBind_id(id);
        InternetWorkManager.getRequest().setcurrentCar(Utils.body(Mygson.getInstance().toJson(setCurrentCarRequest)))
                .compose(RxHelper.observableIOMain(this))
                .subscribe(new MyObserver<User>() {
                    @Override
                    public void onSuccess(User data) {
                        loadData();
                        promptDialog.dismiss();
                    }

                    @Override
                    public void onFail(int code, String message) {
                        promptDialog.dismiss();
                    }

                    @Override
                    public void onError() {
                        promptDialog.dismiss();
                    }

                    @Override
                    public void onClearToken() {
                        Utils.toLogin(MyCarActivity.this);
                    }
                });
//        String json = JsonSplice.leftparent + JsonSplice.yin + "bind_id" + JsonSplice.yinandmao + id + JsonSplice.rightparent;
//        new MyRequest().spliceJson(API.carnow, true, json, this, promptDialog, null, new Response() {
//            @Override
//            public void response(JsonObject json, JsonArray jsonArray, boolean isNull) {
//                loadData();
//            }
//        });
    }

    //bindId[int]	是	绑定ID
    private void cancelCar(String plateNo) {
        //{
        //    "plate": "浙ARB104"
        //}
        promptDialog.showLoading("正在解绑");
//        HashMap<String, String> map = new HashMap<>();
//        map.put("plate", plateNo);
//        new MyRequest().myRequest(API.carunbind, true, map, true, this, promptDialog, null, new Response() {
//            @Override
//            public void response(JsonObject json, JsonArray jsonArray, boolean isNull) {
//                loadData();
//            }
//        });
        UnbindCarRequest unbindCarRequest = new UnbindCarRequest();
        unbindCarRequest.setPlate(plateNo);
        InternetWorkManager.getRequest().unbindCar(Utils.body(Mygson.getInstance().toJson(unbindCarRequest)))
                .compose(RxHelper.observableIOMain(this))
                .subscribe(new MyObserver<User>() {
                    @Override
                    public void onSuccess(User data) {
                        Utils.snackbar("申请解绑成功");
                        promptDialog.dismiss();
                        loadData();
                    }

                    @Override
                    public void onFail(int code, String message) {
                        promptDialog.dismiss();
                    }

                    @Override
                    public void onError() {
                        promptDialog.dismiss();
                    }

                    @Override
                    public void onClearToken() {
                        Utils.toLogin(MyCarActivity.this);
                    }
                });
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        private AdapterCarBinding adapterCarBinding;

        public MyViewHolder(View view) {
            super(view);
        }

        public void setBinding(AdapterCarBinding adapterCarBinding) {
            this.adapterCarBinding = adapterCarBinding;
        }

        public AdapterCarBinding getBinding() {
            return adapterCarBinding;
        }
    }
}
