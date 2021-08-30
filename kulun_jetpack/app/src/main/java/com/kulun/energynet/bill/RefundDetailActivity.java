package com.kulun.energynet.bill;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.kulun.energynet.R;
import com.kulun.energynet.databinding.ActivityRefundDetailFromShowBinding;
import com.kulun.energynet.model.RefundDetail;
import com.kulun.energynet.utils.Utils;
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity;

import java.util.List;

public class RefundDetailActivity extends RxAppCompatActivity {
    private ActivityRefundDetailFromShowBinding binding;
    private RefundDetail refundDetail;
    private Drawable drawable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this ,R.layout.activity_refund_detail_from_show);
        binding.header.left.setOnClickListener(v -> finish());
        binding.header.title.setText("账单详情");
        refundDetail = (RefundDetail) getIntent().getSerializableExtra("data");
        binding.money.setText("-"+refundDetail.getAmount()+"元");
        String s = "";
        switch (refundDetail.getStatus()){
            case 0:
                s="退款中";
                binding.type.setTextColor(getResources().getColor(R.color.yellow));
                binding.result.setText("审核中");
                binding.result.setTextColor(getResources().getColor(R.color.info));
                drawable = getResources().getDrawable(R.mipmap.icon_choice_normal);
                drawable.setBounds(0, 0, 50, 50);
                binding.result.setCompoundDrawables(drawable, null, null, null);
                binding.time1.setText(refundDetail.getProcess().get(0).get(1));
                binding.content1.setText(refundDetail.getProcess().get(0).get(2));
                binding.time2.setText(refundDetail.getProcess().get(1).get(1));
                binding.content2.setText(refundDetail.getProcess().get(1).get(2));
                binding.line1.setBackgroundColor(getResources().getColor(R.color.gray));
                break;
            case 1:
                s="退款已通过";
                binding.type.setTextColor(getResources().getColor(R.color.black));
                binding.result.setText("退款已通过");
                binding.result.setTextColor(getResources().getColor(R.color.text_color));
                drawable = getResources().getDrawable(R.mipmap.icon_choice_selected);
                drawable.setBounds(0, 0, 50, 50);
                binding.result.setCompoundDrawables(drawable, null, null, null);
                binding.time1.setText(refundDetail.getProcess().get(0).get(1));
                binding.content1.setText(refundDetail.getProcess().get(0).get(2));
                binding.time2.setText(refundDetail.getProcess().get(1).get(1));
                binding.content2.setText(refundDetail.getProcess().get(1).get(2));
                binding.line1.setBackgroundColor(getResources().getColor(R.color.text_color));
                break;
            case 2:
                s="退款未通过";
                binding.type.setTextColor(getResources().getColor(R.color.red));
                binding.result.setText("退款未通过");
                binding.result.setTextColor(getResources().getColor(R.color.text_color));
                drawable = getResources().getDrawable(R.mipmap.icon_choice_selected);
                drawable.setBounds(0, 0, 50, 50);
                binding.result.setCompoundDrawables(drawable, null, null, null);
                binding.time1.setText(refundDetail.getProcess().get(0).get(1));
                binding.content1.setText(refundDetail.getProcess().get(0).get(2));
                binding.time2.setText(refundDetail.getProcess().get(1).get(1));
                binding.content2.setText(refundDetail.getProcess().get(1).get(2));
                binding.line1.setBackgroundColor(getResources().getColor(R.color.text_color));
                break;
            default:
                break;
        }
        binding.type.setText(s);
    }
}
