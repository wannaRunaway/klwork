package com.kulun.energynet.bill;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kulun.energynet.R;
import com.kulun.energynet.customizeView.ToastUtils;
import com.kulun.energynet.model.Bill;
import com.kulun.energynet.model.ResponseModel;
import com.kulun.energynet.model.User;
import com.kulun.energynet.network.InternetWorkManager;
import com.kulun.energynet.network.MyObserver;
import com.kulun.energynet.network.RxHelper;
import com.kulun.energynet.requestbody.StatuploadModel;
import com.kulun.energynet.network.API;
import com.kulun.energynet.utils.JsonSplice;
import com.kulun.energynet.utils.Mygson;
import com.kulun.energynet.utils.Utils;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class EvaluateActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mBack;
//    private TextView mCommit;
    private TextView mSerialNo;
    private TextView mStation;
    private TextView mStar1;
    private TextView mStar2;
    private TextView mStar3;
    private TextView mStar4;
    private TextView mStar5;
    private TextView mStarText;
    private TextView mTag1;
    private TextView mTag2;
    private TextView mTag3;
    private TextView mTag4;

    private String serialNum;
    private String stationName;
    private List<TextView> list = new ArrayList<TextView>();

    private String exchangeRecordId;
    private Integer starsNumber = 0;
    private String content = "";
    private String tagSerial = "";
    private String tagContent = "";
    private String site;
    private int siteid;
    private Bill bill;
    private int exId;
    private String orderNo;
    private TextView commit;
    private long lastBackTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.evaluate);
        Intent intent = getIntent();
        exchangeRecordId = intent.getStringExtra("exchangeRecordId");
        serialNum = intent.getStringExtra("serialNum");
        stationName = intent.getStringExtra("stationName");
        site = intent.getStringExtra("site");
        siteid = intent.getIntExtra("siteid", 0);
        bill = (Bill) intent.getSerializableExtra("bill");
        exId = getIntent().getIntExtra("exId", 0);
        orderNo = getIntent().getStringExtra("orderNo");
        bindView();
    }

    public void bindView() {
        mBack = (ImageView) this.findViewById(R.id.left);
//        mCommit = (TextView) this.findViewById(R.id.evaluate_commit);
        mSerialNo = (TextView) this.findViewById(R.id.evaluate_serial_no);
        mStation = (TextView) this.findViewById(R.id.evaluate_station);
        mStar1 = (TextView) this.findViewById(R.id.evaluate_star1);
        mStar2 = (TextView) this.findViewById(R.id.evaluate_star2);
        mStar3 = (TextView) this.findViewById(R.id.evaluate_star3);
        mStar4 = (TextView) this.findViewById(R.id.evaluate_star4);
        mStar5 = (TextView) this.findViewById(R.id.evaluate_star5);
        mStarText = (TextView) this.findViewById(R.id.evaluate_star_text);
        mTag1 = (TextView) this.findViewById(R.id.evaluate_tag1);
        mTag2 = (TextView) this.findViewById(R.id.evaluate_tag2);
        mTag3 = (TextView) this.findViewById(R.id.evaluate_tag3);
        mTag4 = (TextView) this.findViewById(R.id.evaluate_tag4);
        commit = findViewById(R.id.finish);
        commit.setOnClickListener(this);

        mSerialNo.setText("订单号：" + orderNo);
        mStation.setText(site);

        mBack.setOnClickListener(this);
//        mCommit.setOnClickListener(this);
        mStar1.setOnClickListener(this);
        mStar2.setOnClickListener(this);
        mStar3.setOnClickListener(this);
        mStar4.setOnClickListener(this);
        mStar5.setOnClickListener(this);
        mTag1.setOnClickListener(this);
        mTag2.setOnClickListener(this);
        mTag3.setOnClickListener(this);
        mTag4.setOnClickListener(this);

        list.add(mStar1);
        list.add(mStar2);
        list.add(mStar3);
        list.add(mStar4);
        list.add(mStar5);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left:
                finish();
                break;

            case R.id.evaluate_star1:
                starSelected(1);
                break;

            case R.id.evaluate_star2:
                starSelected(2);
                break;

            case R.id.evaluate_star3:
                starSelected(3);
                break;

            case R.id.evaluate_star4:
                starSelected(4);
                break;

            case R.id.evaluate_star5:
                starSelected(5);
                break;

            case  R.id.evaluate_tag1:
                tagSelected(mTag1);
                break;

            case  R.id.evaluate_tag2:
                tagSelected(mTag2);
                break;

            case  R.id.evaluate_tag3:
                tagSelected(mTag3);
                break;

            case  R.id.evaluate_tag4:
                tagSelected(mTag4);
                break;

            case R.id.finish:
                if (System.currentTimeMillis() - lastBackTime > 10 * 1000) {
                    commit();
                    lastBackTime = System.currentTimeMillis();
                } else { //如果两次按下的时间差小于2秒，则退出程序
                    ToastUtils.showShort("按键过快，请10秒后再点击哦", this);
                }
                break;
            default:
                break;

        }
    }

    private void commit(){
        tagSerial = "";
        tagContent =  "";
        if(starsNumber == 0) {
//                    ToastUtil.showToast(mContext, "请选择评分");
            Utils.snackbar(EvaluateActivity.this, "请选择评分");
            return;
        }
        if(mTag1.isSelected()) {
            tagSerial = tagSerial + 1;
            tagContent = tagContent + mTag1.getText();
        }
        if(mTag2.isSelected()) {
            tagSerial = tagSerial + "," + 2;
            tagContent = tagContent + "," + mTag2.getText();
        }
        if(mTag3.isSelected()) {
            tagSerial = tagSerial + "," + 3;
            tagContent = tagContent + "," + mTag3.getText();
        }
        if(mTag4.isSelected()) {
            tagSerial = tagSerial + "," + 4;
            tagContent = tagContent + "," + mTag4.getText();
        }
        if(tagSerial.startsWith(",")) {
            tagSerial = tagSerial.substring(1);
            tagContent = tagContent.substring(1);
        }
        //{
        //    "bid": 2697877,
        //    "siteId": 67,
        //    "starNumber": 5,
        //    "tagList": "1,2",
        //    "tagContent": "站内环境整洁,员工服务热情",
        //    "content": "非常好"
        //}
        if (content.equals("")){
            Utils.snackbar(EvaluateActivity.this, "请选择星星");
            return;
        }
        if (starsNumber == 0){
            Utils.snackbar(EvaluateActivity.this, "请选择星星");
            return;
        }
//                String json = JsonSplice.leftparent+JsonSplice.yin+"exId"+JsonSplice.yinandmao+exId+JsonSplice.dou+
//                        JsonSplice.yin+"siteId"+JsonSplice.yinandmao+siteid+JsonSplice.dou+
//                        JsonSplice.yin+"starNumber"+JsonSplice.yinandmao+starsNumber+JsonSplice.dou+
//                        JsonSplice.yin+"content"+JsonSplice.yinandmao+JsonSplice.yin+content+JsonSplice.yin+JsonSplice.rightparent;
//                new MyRequest().spliceJson(API.commentCommit, true, json, EvaluateActivity.this, null, null, new Response() {
//                    @Override
//                    public void response(JsonObject json, JsonArray jsonArray, boolean isNull) {
//                        Utils.snackbar(EvaluateActivity.this, "评价上传成功");
//                        finish();
//                    }
//                });
        StatuploadModel statuploadModel = new StatuploadModel();
        statuploadModel.setExId(exId);
        statuploadModel.setSiteId(siteid);
        statuploadModel.setStarNumber(starsNumber);
        statuploadModel.setContent(content);
        InternetWorkManager.getRequest().startUpload(Utils.body(Mygson.getInstance().toJson(statuploadModel)))
                .compose(RxHelper.observableIOMain(this))
                .subscribe(new MyObserver<User>() {
                    @Override
                    public void onSuccess(User data) {
                        Utils.snackbar(EvaluateActivity.this, "评价上传成功");
                        finish();
                    }

                    @Override
                    public void onFail(int code, String message) {

                    }

                    @Override
                    public void onError() {

                    }

                    @Override
                    public void onClearToken() {
                        Utils.toLogin(EvaluateActivity.this);
                    }
                });
    }

    public void starSelected(Integer grade) {
        if(grade <= 2) {
            tagAllClear();
        }
        for(int i=0;i<grade;i++) {
            list.get(i).setBackgroundResource(R.mipmap.star);
        }
        for(int i=grade;i<list.size();i++) {
            list.get(i).setBackgroundResource(R.mipmap.star2);
        }
        switch (grade) {
            case 1:
                starsNumber = 1;
                mStarText.setText("不满意，很失望");
                content = "不满意，很失望";
                break;
            case 2:
                starsNumber = 2;
                mStarText.setText("不满意，有点失望");
                content = "不满意，有点失望";
                break;
            case 3:
                starsNumber = 3;
                mStarText.setText("一般");
                content = "一般";
                break;
            case 4:
                starsNumber = 4;
                mStarText.setText("满意");
                content = "满意";
                break;
            case 5:
                starsNumber = 5;
                mStarText.setText("很满意");
                content = "很满意";
                break;
            default:
                break;
        }
    }

    public void tagSelected(TextView view) {
        if(starsNumber == 0) {
//            ToastUtil.showToast(mContext, "请先选择星级");
            Utils.snackbar(EvaluateActivity.this, "请先选择星级");
            return;
        } else if(starsNumber <= 2) {
//            ToastUtil.showToast(mContext, "两星以下不能选择标签");
            Utils.snackbar(EvaluateActivity.this, "两星以下不能选择标签");
            return;
        } else {
            if(view.isSelected()) {
                view.setSelected(false);
                view.setTextColor(Color.parseColor("#8A8A8A"));
            } else {
                view.setSelected(true);
                view.setTextColor(Color.parseColor("#FFD700"));
            }
        }
    }

    public void tagAllClear() {
        mTag1.setSelected(false);
        mTag2.setSelected(false);
        mTag3.setSelected(false);
        mTag4.setSelected(false);
        mTag1.setTextColor(Color.parseColor("#8A8A8A"));
        mTag2.setTextColor(Color.parseColor("#8A8A8A"));
        mTag3.setTextColor(Color.parseColor("#8A8A8A"));
        mTag4.setTextColor(Color.parseColor("#8A8A8A"));
    }
}
