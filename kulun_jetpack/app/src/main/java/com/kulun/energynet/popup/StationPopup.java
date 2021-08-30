package com.kulun.energynet.popup;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.kulun.energynet.R;
import com.kulun.energynet.model.StationInfo;
import com.kulun.energynet.utils.Utils;

import razerdp.basepopup.BasePopupWindow;
public class StationPopup extends BasePopupWindow {

    public TextView name,address,phone,distance,paidui,kucun,time;
    public TextView delayappointcancel,delayappoint,appoint, tv_sign, tv_clock, tv_fare_stop;
    public LinearLayout redelayDismiss;
    private StationInfo stationInfo;
    private CardView cardView;
    public StationPopup(Context context, CardView cardView) {
        super(context);
        setPopupGravity(Gravity.BOTTOM);
        initView();
        this.cardView = cardView;
    }

    public void setStation(StationInfo station){
        this.stationInfo = station;
    }

    public StationInfo getStationInfo(){
        return stationInfo;
    }

    private void initView() {
        name = findViewById(R.id.tv_name);
        address = findViewById(R.id.tv_address);
        phone = findViewById(R.id.tv_phone);
        distance = findViewById(R.id.tv_distance);
        paidui = findViewById(R.id.tv_paidui);
        kucun = findViewById(R.id.tv_kucun);
        delayappoint = findViewById(R.id.img_yuyue_delay);
        appoint = findViewById(R.id.img_yuyue_imm);
        delayappointcancel = findViewById(R.id.img_yuyue_cancel);
        redelayDismiss = findViewById(R.id.re_bottom);
        time = findViewById(R.id.tv_count_time);
        tv_sign = findViewById(R.id.tv_sign);
        tv_clock = findViewById(R.id.tv_clock);
        tv_fare_stop = findViewById(R.id.tv_fare_stop);
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.station_map);
    }

    @Override
    public void onDismiss() {
        super.onDismiss();
        cardView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onShowing() {
        super.onShowing();
        cardView.setVisibility(View.GONE);
    }

//    @Override
//    protected Animation onCreateShowAnimation() {
//        return super.getShowAnimation();
//    }
}
