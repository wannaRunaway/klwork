package com.kulun.energynet.main.fragment;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kulun.energynet.R;
import com.kulun.energynet.bill.BillActivity;
import com.kulun.energynet.databinding.FragmentMineBinding;
import com.kulun.energynet.mine.AppointmentKtActivity;
import com.kulun.energynet.mine.CouponKtActivity;
import com.kulun.energynet.mine.DriverclockActivity;
import com.kulun.energynet.mine.MessageActivity;
import com.kulun.energynet.mine.MyCarActivity;
import com.kulun.energynet.mine.PackageListActivity;
import com.kulun.energynet.mine.RechargeActivity;
import com.kulun.energynet.mine.SystemSettingActivity;
import com.kulun.energynet.model.UseBind;
import com.kulun.energynet.model.User;
import com.kulun.energynet.network.InternetWorkManager;
import com.kulun.energynet.network.MyObserver;
import com.kulun.energynet.network.RxHelper;
import com.kulun.energynet.utils.BaseDialog;
import com.kulun.energynet.utils.Utils;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.trello.rxlifecycle4.components.support.RxFragment;

public class MineFragment extends RxFragment implements View.OnClickListener {
    private FragmentMineBinding binding;
    private UseBind useBind;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, null);
        binding = DataBindingUtil.bind(view);
        bindClick();
        binding.smartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadData();
            }
        });
        loadData();
//        StateAppBar.setStatusBarColor(getActivity(), getResources().getColor(R.color.text_color));
//        ImmersionBar.with(this).init();
        return view;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){//fragment show
            onresume();
        }
    }

    public void onresume(){//fragment show
        loadData();
    }

    private void loadData() {
        InternetWorkManager.getRequest().infoGet()
                .compose(RxHelper.observableIOMain(getActivity()))
                .subscribe(new MyObserver<User>() {
                    @Override
                    public void onSuccess(User user) {
                        Utils.finishRefresh(binding.smartRefresh);
                        if (user == null) return;
                        Utils.shareprefload(user, getActivity());
                        if (user.getUse_bind() != null && user.getUse_bind().getId() != 0) {
                            binding.reCar.setVisibility(View.VISIBLE);
                            binding.name.setText(user.getUse_bind().getPlate_number() + "");
                            if (user.getUse_bind().getBind_status() == 1) {
                                Drawable drawable = getResources().getDrawable(R.mipmap.sign_binding);
                                drawable.setBounds(0, 0, 100, 50);
                                binding.name.setCompoundDrawables(null, null, drawable, null);
                            } else if (user.getUse_bind().getBind_status() == 3) {
                                Drawable drawable = getResources().getDrawable(R.mipmap.sign_lock);
                                drawable.setBounds(0, 0, 100, 50);
                                binding.name.setCompoundDrawables(null, null, drawable, null);
                            }
                            binding.t1.setText(user.getUse_bind().getSoc() + "%");
                            binding.t2.setText(user.getUse_bind().getCar_type());
                            binding.t3.setText(user.getUse_bind().isBattery_status() ? "正常" : "不正常");
                        } else {
                            binding.reCar.setVisibility(View.INVISIBLE);
                        }
                        if (user.getUnread() > 0) {
                            binding.ivMsg.setImageDrawable(getResources().getDrawable(R.mipmap.mine_message_news));
                        } else {
                            binding.ivMsg.setImageDrawable(getResources().getDrawable(R.mipmap.mine_message_normal));
                        }
                        if (user.getName() == null || user.getName().equals("")) {
                            binding.tvName.setText("我");
                        } else {
                            binding.tvName.setText(user.getName() + "");
                        }
                        if (user.getBalance() > 0) {
                            binding.tvYue.setText("" + user.getBalance());
                        } else {
                            binding.tvYue.setText("0");
                        }
                        BaseDialog.createQRCodeWithLogo(binding.imgQrcode, getContext(),false, Utils.getAccount(getActivity()), 80);
                        useBind = user.getUse_bind();
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
//        new MyRequest().myRequest(API.INFO, false, null, true, this, null, binding.smartRefresh, new Response() {
//            @Override
//            public void response(JsonObject json, JsonArray jsonArray,boolean isNull) {
//                //        "name": "王霞",
//                //        "balance": 700.14
//                Utils.shareprefload(json, MineActivity.this);
//                useBind = Utils.getusebind( MineActivity.this);
//                if (useBind !=null && useBind.getId() != 0) {
//                    binding.reCar.setVisibility(View.VISIBLE);
//                    binding.name.setText(useBind.getPlate_number() + "");
//                    if (useBind.getBind_status()==1){
//                        Drawable drawable = getResources().getDrawable(R.mipmap.sign_binding);
//                        drawable.setBounds(0, 0, 100, 50);
//                        binding.name.setCompoundDrawables(null,null,drawable,null);
//                    }else if (useBind.getBind_status()==3){
//                        Drawable drawable = getResources().getDrawable(R.mipmap.sign_lock);
//                        drawable.setBounds(0, 0, 100, 50);
//                        binding.name.setCompoundDrawables(null,null,drawable,null);
//                    }
//                    binding.t1.setText(useBind.getSoc() + "%");
//                    binding.t2.setText(useBind.getCar_type());
//                    binding.t3.setText(useBind.isBattery_status() ? "正常" : "不正常");
//                } else {
//                    binding.reCar.setVisibility(View.INVISIBLE);
//                }
//                if (json != null) {
//                    User user = Mygson.getInstance().fromJson(json, User.class);
//                    if (user.getUnread() > 0){
//                        binding.ivMsg.setImageDrawable(getResources().getDrawable(R.mipmap.mine_message_news));
//                    }else {
//                        binding.ivMsg.setImageDrawable(getResources().getDrawable(R.mipmap.mine_message_normal));
//                    }
//                    if (user.getName() == null || user.getName().equals("")) {
//                        binding.tvName.setText("我");
//                    }else {
//                        binding.tvName.setText(user.getName()+"");
//                    }
//                    if (user.getBalance() > 0) {
//                        binding.tvYue.setText("余额：" + user.getBalance());
//                    } else {
//                        binding.tvYue.setText("余额：0.00");
//                    }
//                    BaseDialog.createQRCodeWithLogo(binding.imgQrcode, MineActivity.this, Utils.getAccount(MineActivity.this), 80);
//                }
//            }
//        });
    }

//    public static int dip2px(Context context, float dpValue) {
//        final float scale = context.getResources().getDisplayMetrics().density;
//        return (int) (dpValue * scale + 0.5f);
//    }

    private void bindClick() {
        binding.imgQrcode.setOnClickListener(new MyClickListener(getActivity(), this));
        binding.ivMsg.setOnClickListener(new MyClickListener(getActivity(), this));
        binding.recharge.setOnClickListener(new MyClickListener(getActivity(), this));
        binding.bill.setOnClickListener(new MyClickListener(getActivity(), this));
        binding.binding.setOnClickListener(new MyClickListener(getActivity(), this));
        binding.activity.setOnClickListener(new MyClickListener(getActivity(), this));
        binding.coupon.setOnClickListener(new MyClickListener(getActivity(), this));
        binding.reservation.setOnClickListener(new MyClickListener(getActivity(), this));
        binding.clock.setOnClickListener(new MyClickListener(getActivity(), this));
        binding.setting.setOnClickListener(new MyClickListener(getActivity(), this));
        binding.tvName.setOnClickListener(new MyClickListener(getActivity(), this));
        binding.customer.setOnClickListener(this);
        Drawable drawable = getResources().getDrawable(R.mipmap.arrow_right);
        drawable.setBounds(0,0,15,27);
        binding.bill.setCompoundDrawables(null,null,drawable,null);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.iv_msg:
                intent = new Intent(getActivity(), MessageActivity.class);
                startActivity(intent);
                break;
            case R.id.recharge:
                intent = new Intent(getActivity(), RechargeActivity.class);
                startActivity(intent);
                break;
            case R.id.binding:
                intent = new Intent(getActivity(), MyCarActivity.class);
                startActivity(intent);
                break;
            case R.id.reservation:
                intent = new Intent(getActivity(), AppointmentKtActivity.class);
                startActivity(intent);
                break;
            case R.id.coupon:
                intent = new Intent(getActivity(), CouponKtActivity.class);
                startActivity(intent);
                break;
            case R.id.clock:
                if (Utils.usebindisNotexist(useBind)) {
                    Utils.snackbar(getActivity(), "请先绑定车辆");
                    return;
                }
                if (useBind.getBusiness_type() != 5) {//5是出租车司机
                    Utils.snackbar(getActivity(), "抱歉，此功能只适用于出租车");
                    return;
                }
                intent = new Intent(getActivity(), DriverclockActivity.class);
                startActivity(intent);
                break;
            case R.id.bill:
                intent = new Intent(getActivity(), BillActivity.class);
                startActivity(intent);
                break;
            case R.id.activity:
                intent = new Intent(getActivity(), PackageListActivity.class);
                startActivity(intent);
                break;
            case R.id.setting:
                intent = new Intent(getActivity(), SystemSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.img_qrcode:
                String carplate = "";
                if (Utils.usebindisNotexist(useBind)) {
                    carplate = "";
                } else {
                    carplate = useBind.getPlate_number();
                }
                BaseDialog.showQrDialog(getActivity(), carplate);
                break;
            case R.id.customer:
                Utils.loadkefu(getActivity(), binding.customer);
                break;
            default:
                break;
        }
    }
}
