package com.kulun.energynet.bill;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kulun.energynet.R;
import com.kulun.energynet.databinding.ActivityBillBinding;
import com.kulun.energynet.databinding.AdapterBillBinding;
import com.kulun.energynet.mine.RechargeActivity;
import com.kulun.energynet.model.Bill;
import com.kulun.energynet.model.BilldetailModel;
import com.kulun.energynet.model.User;
import com.kulun.energynet.network.InternetWorkManager;
import com.kulun.energynet.network.MyObserver;
import com.kulun.energynet.network.RxHelper;
import com.kulun.energynet.popup.BillDatePopup;
import com.kulun.energynet.popup.BilltypePopup;
import com.kulun.energynet.utils.Utils;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import me.leefeng.promptlibrary.PromptDialog;

public class BillActivity extends AppCompatActivity implements View.OnClickListener, TypePopclick, BillDatePopup.Popdateclick {
    private ActivityBillBinding binding;
    private MyAdapter adapter;
    private List<Bill> list = new ArrayList<>();
    private int page;
    private PromptDialog promptDialog;
    private BilltypePopup billtypePopup;
    private BillDatePopup billDatePopup;
    private int mytype = 0;
    //    private boolean isload = false;
    private TypePopclick typePopclick;
    private BillDatePopup.Popdateclick popdateclick;
    private boolean canRefund;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bill);
        typePopclick = this;
        popdateclick = this;
        binding.header.title.setText("????????????");
        binding.header.left.setOnClickListener(this);
        binding.pay.setOnClickListener(this);
        binding.refund.setOnClickListener(this);
        binding.date.setOnClickListener(this);
        binding.type.setOnClickListener(this);
        promptDialog = new PromptDialog(this);
        billtypePopup = new BilltypePopup(BillActivity.this, typePopclick);
        billDatePopup = new BillDatePopup(BillActivity.this, popdateclick);
        adapter = new MyAdapter(list);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
        binding.smartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                page = 0;
                load(true, String.valueOf(mytype), String.valueOf(page));
                loadInfo();
            }
        });
        binding.smartRefresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                page = page + 1;
                load(false, String.valueOf(mytype), String.valueOf(page));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        page=0;
        load(true, "0", String.valueOf(page));
        binding.type.setText("??????");
        binding.date.setText("????????????");
        loadInfo();
    }

    private void loadInfo() {//?????????????????????
        InternetWorkManager.getRequest().infoGet()
                .compose(RxHelper.observableIOMain(this))
                .subscribe(new MyObserver<User>() {
                    @Override
                    public void onSuccess(User user) {
                        if (user != null) {
                            binding.money.setText(user.getBalance() + "");
                            billtypePopup.typebinding.money.setText(user.getBalance() + "");
                            billDatePopup.binding.money.setText(user.getBalance() + "");
                            canRefund = user.isCanRefund();
                            if (canRefund) {
                                binding.refund.setVisibility(View.VISIBLE);
                            } else {
                                binding.refund.setVisibility(View.GONE);
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
                        Utils.toLogin(BillActivity.this);
                    }
                });
//        new MyRequest().myRequest(API.INFO, false, null, true, this, null, null, new Response() {
//            @Override
//            public void response(JsonObject json, JsonArray jsonArray, boolean isNull) {
//                if (json != null || jsonArray != null) {
//                    User user = Mygson.getInstance().fromJson(json, User.class);
//                    binding.money.setText(user.getBalance() + "");
//                    billtypePopup.typebinding.money.setText(user.getBalance() + "");
//                    billDatePopup.binding.money.setText(user.getBalance() + "");
//                    Utils.getusebind(true, json, BillActivity.this);
//                    canRefund = user.isCanRefund();
//                    if (canRefund){
//                        binding.refund.setVisibility(View.VISIBLE);
//                    }else {
//                        binding.refund.setVisibility(View.GONE);
//                    }
//                }
//            }
//        });
    }

    private void load(boolean isRefresh, String type, String page) {
        //type??????[string]		0????????????;1????????????;2????????????;3????????????;4????????????
        //page[string]		??????????????????0??????0??????
        //size[string]		????????????????????????20
        //from[string]		2006-01
        //to[string]		2006-01
        InternetWorkManager.getRequest().billlist(type, page)
                .compose(RxHelper.observableIOMain(this))
                .subscribe(new MyObserver<List<Bill>>() {
                    @Override
                    public void onSuccess(List<Bill> data) {
                        Utils.finishRefresh(binding.smartRefresh);
                        if (data == null) {
                            if (isRefresh) {
                                list.clear();
                                adapter.notifyDataSetChanged();
                            }
                            binding.image.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);
                        } else {
                            if (isRefresh) {
                                list.clear();
                            }
                            list.addAll(data);
                            adapter.notifyDataSetChanged();
                            binding.image.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);
                        }  
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
                        Utils.toLogin(BillActivity.this);
                    }
                });
//        String json = "type=" + type + "&page=" + page;
//        new MyRequest().spliceJson(API.billlist, false, json, this, null, binding.smartRefresh, new Response() {
//            @Override
//            public void response(JsonObject json, JsonArray jsonArray, boolean isNull) {
//                if (isNull) {
//                    if (isRefresh) {
//                        list.clear();
//                        adapter.notifyDataSetChanged();
//                    }
//                    binding.image.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);
//                }
//                if (jsonArray != null) {
//                    if (isRefresh) {
//                        list.clear();
//                    }
//                    list.addAll(Mygson.getInstance().fromJson(jsonArray, new TypeToken<List<Bill>>() {
//                    }.getType()));
//                    adapter.notifyDataSetChanged();
//                    binding.image.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);
//                }
//            }
//        });
    }

    private void load(boolean isRefresh, String type, String page, String time) {
        //type??????[string]		0????????????;1????????????;2????????????;3????????????;4????????????
        //page[string]		??????????????????0??????0??????
        //size[string]		????????????????????????20
        //from[string]		2006-01
        //to[string]		2006-01
//        String json = "type=" + type + "&page=" + page + "&month=" + time;
//        new MyRequest().spliceJson(API.billlist, false, json, this, null, binding.smartRefresh, new Response() {
//            @Override
//            public void response(JsonObject json, JsonArray jsonArray, boolean isNull) {
//                if (json == null || isNull) {
//                    if (isRefresh) {
//                        list.clear();
//                        adapter.notifyDataSetChanged();
//                        binding.image.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);
//                    }
//                }
//                if (jsonArray != null) {
//                    if (isRefresh) {
//                        list.clear();
//                    }
//                    list.addAll(Mygson.getInstance().fromJson(jsonArray, new TypeToken<List<Bill>>() {
//                    }.getType()));
//                    adapter.notifyDataSetChanged();
//                    binding.image.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);
//                }
//            }
//        });
        InternetWorkManager.getRequest().billlist(type, page, time)
                .compose(RxHelper.observableIOMain(BillActivity.this))
                .subscribe(new MyObserver<List<Bill>>() {
                    @Override
                    public void onSuccess(List<Bill> data) {
                        Utils.finishRefresh(binding.smartRefresh);
                        if (data == null) {
                            if (isRefresh) {
                                list.clear();
                                adapter.notifyDataSetChanged();
                                binding.image.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);
                            }
                        } else {
                            if (isRefresh) {
                                list.clear();
                            }
                            list.addAll(data);
                            adapter.notifyDataSetChanged();
                            binding.image.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);
                        }
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
                        Utils.toLogin(BillActivity.this);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left:
                finish();
                break;
            case R.id.pay:
                Intent intent = new Intent(BillActivity.this, RechargeActivity.class);
                startActivity(intent);
                break;
            case R.id.refund:
                Intent intent1 = new Intent(BillActivity.this, RefundActivity.class);
                startActivity(intent1);
                break;
            case R.id.date:
                billDatePopup.showPopupWindow();
                break;
            case R.id.type:
                billtypePopup.showPopupWindow();
                break;
            default:
                break;
        }
    }

    @Override
    public void billpopclick(String type) {
        page = 0;
        switch (type) {//type=0??????type=1????????????type=2????????????type=3??????????????????????????????cType????????????????????????cType??????
            // 	0????????????;1????????????;2????????????;3????????????;4????????????
            case "??????":
                mytype = 0;
                load(true, "0", String.valueOf(page));
                break;
            case "??????":
                mytype = 1;
                load(true, "1", String.valueOf(page));
                break;
            case "??????":
                mytype = 3;
                load(true, "3", String.valueOf(page));
                break;
            case "??????":
                mytype = 2;
                load(true, "2", String.valueOf(page));
                break;
            case "??????":
                mytype = 4;
                load(true, "4", String.valueOf(page));
                break;
            default:
                break;
        }
        binding.type.setText(type + "");
    }

    @Override
    public void datepopclick(String time) {//???????????????
        if (time.equals("")) {
            binding.date.setText("??????");
            load(true, String.valueOf(mytype), String.valueOf(page));
        } else {
//            String fromjson = from.replaceAll("???", "-").replaceAll("???", "");
//            String tojson = to.replaceAll("???", "-").replaceAll("???", "");
//            String json = fromjson +"~"+ tojson;
            binding.date.setText(time);
            load(true, String.valueOf(mytype), String.valueOf(page), time);
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private List<Bill> adapterlist;

        public MyAdapter(List<Bill> list) {
            this.adapterlist = list;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(BillActivity.this).inflate(R.layout.adapter_bill, null);
            AdapterBillBinding binding = AdapterBillBinding.bind(view);
            MyViewHolder holder = new MyViewHolder(view);
            holder.setBinding(binding);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Bill bill = list.get(position);
            String type = "";//change_type(-4?????????????????????,-3??????????????? -2??????,-1???????????????1???????????????2??????????????????,3pad??????,
            // 4??????????????????,5?????????????????????),6 pad????????????  7app???????????????12??????????????????;13??????
            int mipmap = 0;
            switch (bill.getcType()) {
                case 1:
                case 2:
                case 3:
                    type = "??????";
                    mipmap = R.mipmap.bill_recharge;
                    break;
                case 6:
                case 7:
                    type = "??????";
                    mipmap = R.mipmap.icon_activity_bill;
                    break;
                case -1:
                    type = "??????";
                    mipmap = R.mipmap.bill_amount;
                    break;
                case -2:
                    mipmap = R.mipmap.bill_refund;
                    type = "??????";
                    break;
                default:
                    break;
            }
            holder.binding.name.setText(type);
            holder.binding.image.setImageResource(mipmap);
            holder.binding.time.setText(bill.getCreate_time());
            String left = type.equals("??????") ? "+" : "";
            holder.binding.money.setText(left + bill.getChange_balance() + "???");
            holder.binding.money.setTextColor(type.equals("??????") ? getResources().getColor(R.color.red) : getResources().getColor(R.color.black));
            holder.binding.re.setOnClickListener(v -> {

                //bid[string]	???
                //cType[string]	???
//                String json = "bid=" + bill.getBid() + "&cType=" + bill.getcType();
                promptDialog.showLoading("?????????...");
                InternetWorkManager.getRequest().billDetail(bill.getBid(), bill.getcType())
                        .compose(RxHelper.observableIOMain(BillActivity.this))
                        .subscribe(new MyObserver<BilldetailModel>() {
                            @Override
                            public void onSuccess(BilldetailModel data) {
                                promptDialog.dismiss();
                                if (data != null) {
                                    if (data.getDetail() != null && data.getDetail().size() > 0) {
                                        switch (bill.getcType()) {//change_type(-4?????????????????????,-3??????????????? -2??????,-1???????????????1???????????????2??????????????????,3pad??????,
                                            // 4??????????????????,5?????????????????????),6 pad????????????  7app???????????????12??????????????????;13??????
                                            case 1:
                                            case 2:
                                            case 3:
                                                Intent intent = new Intent(BillActivity.this, BillRechargeDetailsActivity.class);
                                                try {
                                                    intent.putExtra("data", (Serializable) data.getDetail());
                                                    intent.putExtra("bill", bill);
                                                } catch (Exception e) {
                                                }
                                                startActivity(intent);
                                                break;
                                            case 6:
                                            case 7:
                                                Intent intentpackage = new Intent(BillActivity.this, BillActivityActivity.class);
                                                try {
                                                    intentpackage.putExtra("data", (Serializable) data.getDetail());
                                                    intentpackage.putExtra("bill", bill);
                                                } catch (Exception e) {
                                                }
                                                startActivity(intentpackage);
                                                break;
                                            case -1:
                                                Intent intentconsume = new Intent(BillActivity.this, BillConsumeActivity.class);
                                                //"questioned":false,"commented":false,
//                                    try {
//                                                intentconsume.putExtra("data", (Serializable) data.getDetail());
                                                intentconsume.putExtra("bill", bill);
//                                                intentconsume.putExtra("questioned", data.isQuestioned());
//                                                intentconsume.putExtra("commented", data.isCommented());
//                                                intentconsume.putExtra("siteid", data.getSiteId());
//                                                intentconsume.putExtra("site", data.getSite());
//                                                intentconsume.putExtra("exId", data.getExId());
//                                                intentconsume.putExtra("orderNo", data.getOrderNo());
//                                    }catch (Exception e){
//                                    }
                                                startActivity(intentconsume);
                                                break;
                                            case -2:
                                                Intent intentrefund = new Intent(BillActivity.this, BillRefundActivity.class);
                                                try {
                                                    intentrefund.putExtra("data", (Serializable) data.getDetail());
                                                    intentrefund.putExtra("bill", bill);
                                                    intentrefund.putExtra("status", data.getStatus());
                                                    intentrefund.putExtra("amount", data.getAmount());
                                                } catch (Exception e) {
                                                }
                                                startActivity(intentrefund);
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                }
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
                                Utils.toLogin(BillActivity.this);
                            }
                        });
            });
        }

        @Override
        public int getItemCount() {
            return adapterlist.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        private AdapterBillBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setBinding(AdapterBillBinding binding) {
            this.binding = binding;
        }
    }
}
