package com.kulun.energynet.popup;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import com.kulun.energynet.R;
import com.kulun.energynet.databinding.StationBaoAllPopupBinding;
import razerdp.basepopup.BasePopupWindow;

public class StationBaoAllPopup extends BasePopupWindow {
    public StationBaoAllPopupBinding binding;
    private Context context;
    public StationBaoAllPopup(Context context) {
        super(context);
        setPopupGravity(Gravity.BOTTOM);
        this.context = context;
    }

    public StationBaoAllPopup(Context context, int width, int height) {
        super(context, width, height);
    }

    public StationBaoAllPopup(Fragment fragment) {
        super(fragment);
    }

    public StationBaoAllPopup(Fragment fragment, int width, int height) {
        super(fragment, width, height);
    }

    public StationBaoAllPopup(Dialog dialog) {
        super(dialog);
    }

    public StationBaoAllPopup(Dialog dialog, int width, int height) {
        super(dialog, width, height);
    }

    @Override
    public View onCreateContentView() {
        View view = createPopupById(R.layout.station_bao_all_popup);
        binding = DataBindingUtil.bind(view);
        return view;
    }
}
