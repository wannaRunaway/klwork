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
import com.kulun.energynet.utils.Utils;

import razerdp.basepopup.BasePopupWindow;

public class StationOtherCitiesPopup extends BasePopupWindow {

    public TextView name, address, phone, worktime;
    private CardView cardView;

    public StationOtherCitiesPopup(Context context, CardView cardView) {
        super(context);
        setPopupGravity(Gravity.BOTTOM);
        initView();
        this.cardView = cardView;
    }

    private void initView() {
        name = findViewById(R.id.tv_name);
        address = findViewById(R.id.tv_address);
        phone = findViewById(R.id.tv_phone);
        worktime = findViewById(R.id.tv_clock);
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.stationall_map);
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
}