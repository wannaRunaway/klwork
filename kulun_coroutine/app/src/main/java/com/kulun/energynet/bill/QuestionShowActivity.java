package com.kulun.energynet.bill;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.kulun.energynet.R;
import com.kulun.energynet.customizeView.ViewUtils;
import com.kulun.energynet.databinding.ActivityShowActivityBinding;
import com.kulun.energynet.model.Bill;
import com.kulun.energynet.model.BilldetailModel;
import com.kulun.energynet.model.QuestionshowModel;
import com.kulun.energynet.model.RefundDetail;
import com.kulun.energynet.network.InternetWorkManager;
import com.kulun.energynet.network.MyObserver;
import com.kulun.energynet.network.RxHelper;
import com.kulun.energynet.requestbody.QuestionshowRequest;
import com.kulun.energynet.utils.Mygson;
import com.kulun.energynet.utils.Utils;

public class QuestionShowActivity extends AppCompatActivity {
    private ActivityShowActivityBinding binding;
    private Bill bill;
    private String site;
    private int exId;
    private RefundDetail refundDetail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_activity);
        binding.header.title.setText("问题反馈");
        binding.header.left.setOnClickListener(v -> finish());
        bill = (Bill) getIntent().getSerializableExtra("bill");
        site = getIntent().getStringExtra("site");
        exId = getIntent().getIntExtra("exId", 0);
        binding.station.setText(site + "");
        binding.number.setText("订单号：" + getIntent().getStringExtra("orderNo"));
        refundDetail = (RefundDetail) getIntent().getSerializableExtra("data");
        binding.check.setOnClickListener(v -> {
            Intent intent = new Intent(this, RefundDetailActivity.class);
            intent.putExtra("data", refundDetail);
            startActivity(intent);
        });
        QuestionshowRequest questionshowRequest = new QuestionshowRequest();
        questionshowRequest.setExId(exId);
        InternetWorkManager.getRequest().questionshow(Utils.body(Mygson.getInstance().toJson(questionshowRequest)))
                .compose(RxHelper.observableIOMain(this))
                .subscribe(new MyObserver<QuestionshowModel>() {
                    @Override
                    public void onSuccess(QuestionshowModel data) {
                        if (data.getContent() != null) {
                            binding.question.setText("问题：" + data.getContent());
                            binding.imageview.setImageResource(R.mipmap.sign_processing);
                        }
                        if (data.getHandleContent() != null) {
                            binding.fankui.setText("反馈:" + data.getHandleContent());
                        }
                        if (data.getStatus() != 0) {
                            binding.imageview.setImageResource(R.mipmap.sign_success);
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
                        Utils.toLogin(QuestionShowActivity.this);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        InternetWorkManager.getRequest().billDetail(bill.getBid(), bill.getcType())
                .compose(RxHelper.observableIOMain(this))
                .subscribe(new MyObserver<BilldetailModel>() {
                    @Override
                    public void onSuccess(BilldetailModel data) {
                        refundDetail = data.getRefundDetail();
                        if (refundDetail.getId() != 0) {//有退款记录
                            binding.check.setVisibility(View.VISIBLE);
                        } else {
                            binding.check.setVisibility(View.GONE);
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
                        Utils.toLogin(QuestionShowActivity.this);
                    }
                });
    }
}
