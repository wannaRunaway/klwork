package com.kulun.energynet.bill;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kulun.energynet.R;
import com.kulun.energynet.customizeView.ViewUtils;
import com.kulun.energynet.databinding.ActivityBillConsumeBinding;
import com.kulun.energynet.model.Bill;
import com.kulun.energynet.model.BillDetail;
import com.kulun.energynet.model.BilldetailModel;
import com.kulun.energynet.model.ResponseModel;
import com.kulun.energynet.network.InternetWorkManager;
import com.kulun.energynet.network.MyObserver;
import com.kulun.energynet.network.RxHelper;
import com.kulun.energynet.requestparams.MyRequest;
import com.kulun.energynet.requestparams.Response;
import com.kulun.energynet.network.API;
import com.kulun.energynet.utils.Utils;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class BillConsumeActivity extends AppCompatActivity implements View.OnClickListener {//消费账单详情
    private ActivityBillConsumeBinding binding;
//    private List<BillDetail> list;
    private Bill bill;
//    private boolean questioned, commented;
//    private int siteid;
//    private String site;
//    private int exId;
//    private String orderNo;
    private BilldetailModel billdetailModel;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_bill_consume);
        binding.header.title.setText("账单详情");
        binding.header.left.setOnClickListener(v -> finish());
        binding.zifeiyiwen.setOnClickListener(this);
        binding.pingjia.setOnClickListener(this);
//        list = (List<BillDetail>) getIntent().getSerializableExtra("data");
        bill = (Bill) getIntent().getSerializableExtra("bill");
//        siteid = getIntent().getIntExtra("siteid", 0);
//        orderNo = getIntent().getStringExtra("orderNo");
//        site = getIntent().getStringExtra("site");
//        exId = getIntent().getIntExtra("exId", 0);
//        questioned = getIntent().getBooleanExtra(API.questioned, false);
//        commented = getIntent().getBooleanExtra(API.commented, false);
        binding.money.setText(bill.getChange_balance()+"元");
    }

    @Override
    protected void onResume() {
        super.onResume();
        InternetWorkManager.getRequest().billDetail(bill.getBid(), bill.getcType())
                .compose(RxHelper.observableIOMain(this))
                .subscribe(new MyObserver<BilldetailModel>() {
                    @Override
                    public void onSuccess(BilldetailModel data) {
                        billdetailModel = data;
                        ViewUtils.addView(billdetailModel.getDetail(),BillConsumeActivity.this,binding.line);
                    }

                    @Override
                    public void onFail(int code, String message) {

                    }

                    @Override
                    public void onError() {

                    }

                    @Override
                    public void onClearToken() {
                        Utils.toLogin(BillConsumeActivity.this);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.zifeiyiwen:
                zifeiyiwen();
                break;
            case R.id.pingjia:
                pingjia();
                break;
            default:
                break;
        }
    }

    private void pingjia() {//评价
        if (!billdetailModel.isCommented()) {
            Intent intent = new Intent(this, EvaluateActivity.class);
            intent.putExtra("siteid",billdetailModel.getSiteId());
            intent.putExtra("bill", bill);
            intent.putExtra("exId", billdetailModel.getExId());
            intent.putExtra("site", billdetailModel.getSite());
            intent.putExtra("orderNo", billdetailModel.getOrderNo());
            startActivity(intent);
//            finish();
        }else {
            Utils.snackbar(BillConsumeActivity.this, "您已经评价过了");
        }
    }

    private void zifeiyiwen() {//资费质疑
        if (!billdetailModel.isQuestioned()) {//疑问
            Intent intent = new Intent(this, QuestionActivity.class);
            intent.putExtra("bill", bill);
            intent.putExtra("siteid",billdetailModel.getSiteId());
            intent.putExtra("exId", billdetailModel.getExId());
            intent.putExtra("site", billdetailModel.getSite());
            intent.putExtra("orderNo", billdetailModel.getOrderNo());
            startActivity(intent);
//            finish();
        }else {
            Intent intent = new Intent(this, QuestionShowActivity.class);
            intent.putExtra("bill",bill);
            intent.putExtra("site",billdetailModel.getSite());
            intent.putExtra("exId", billdetailModel.getExId());
            intent.putExtra("orderNo", billdetailModel.getOrderNo());
            intent.putExtra("data", billdetailModel.getRefundDetail());
            startActivity(intent);
        }
    }
}
