package com.kulun.energynet.bill;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kulun.energynet.R;
import com.kulun.energynet.customizeView.AliOSS;
import com.kulun.energynet.customizeView.ToastUtils;
import com.kulun.energynet.databinding.QuestionBinding;
import com.kulun.energynet.mine.MyPhotoAdapter;
import com.kulun.energynet.model.Bill;
import com.kulun.energynet.model.OssTokenModel;
import com.kulun.energynet.model.ResponseModel;
import com.kulun.energynet.model.UseBind;
import com.kulun.energynet.model.User;
import com.kulun.energynet.network.InternetWorkManager;
import com.kulun.energynet.network.MyObserver;
import com.kulun.energynet.network.RxHelper;
import com.kulun.energynet.requestbody.QuestionUploadRequest;
import com.kulun.energynet.network.API;
import com.kulun.energynet.utils.JsonSplice;
import com.kulun.energynet.utils.Mygson;
import com.kulun.energynet.utils.SharePref;
import com.kulun.energynet.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import me.leefeng.promptlibrary.PromptDialog;
import me.nereo.multi_image_selector.MultiImageSelector;

public class QuestionActivity extends AppCompatActivity implements View.OnClickListener {

    //????????????????????????????????????
    private long lastBackTime = 0;
    private MyPhotoAdapter adapter;
    private static final int REQUEST_IMAGE = 2;
    protected static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    protected static final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 102;
//    private String bucketName = "";
    private ArrayList<String> mSelectPath = new ArrayList<String>();
    private QuestionBinding binding;
//    private PromptDialog promptDialog;
    private OSS alioss;
    private Bill bill;
    private int siteid;
    private int exId;
    private String orderNo,site;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.question);
        binding.header.left.setOnClickListener(v -> finish());
//        binding.header.right.setOnClickListener(this);
        binding.header.title.setText("????????????");
        binding.finish.setText("??????");
        binding.finish.setOnClickListener(v -> {
            /**
             * ?????????????????????????????????????????????????????????sso,????????????????????????
             */
            //??????????????????????????????????????????????????????????????????????????????2?????????????????????????????????
            String content = binding.etQuestion.getText().toString();
            if ("".equals(content) || content==null) {
                Utils.snackbar(QuestionActivity.this, "?????????????????????");
                return;
            }
            if (content.length()>150){
                Utils.snackbar(QuestionActivity.this, "??????????????????150??????");
                return;
            }
            if (System.currentTimeMillis() - lastBackTime > 10 * 1000) {
                commit();
                lastBackTime = System.currentTimeMillis();
            } else { //????????????????????????????????????2?????????????????????
                ToastUtils.showShort("??????????????????10??????????????????", this);
            }
        });
        exId = getIntent().getIntExtra("exId", 0);
        orderNo = getIntent().getStringExtra("orderNo");
        site = getIntent().getStringExtra("site");
        binding.number.setText(orderNo);
        binding.station.setText(site);
        bindView();
//        promptDialog = new PromptDialog(this);
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(QuestionActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 7);
        }
        bill = (Bill) getIntent().getSerializableExtra("bill");
        siteid = getIntent().getIntExtra("siteid", 0);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadOssToken();
            }
        },1000);
    }

    public void bindView() {
        adapter = new MyPhotoAdapter(QuestionActivity.this, mSelectPath);
        binding.gridview.setAdapter(adapter);
        binding.gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("view: " + view);
                if (i == mSelectPath.size()) {
                    pickImage();
                }
            }
        });
    }

    private void loadOssToken() {
        InternetWorkManager.getRequest().getalioss()
                .compose(RxHelper.observableIOMain(this))
                .subscribe(new MyObserver<OssTokenModel>() {
                    @Override
                    public void onSuccess(OssTokenModel data) {
                        SharePref.put(QuestionActivity.this, API.ak, data.getAccessKeyId());
                        SharePref.put(QuestionActivity.this, API.sk, data.getAccessKeySecret());
                        SharePref.put(QuestionActivity.this, API.myosstoken, data.getSecurityToken());
                        SharePref.put(QuestionActivity.this, API.expiration, data.getExpiration());
                        alioss = AliOSS.getOss(QuestionActivity.this);
                    }

                    @Override
                    public void onFail(int code, String message) {

                    }

                    @Override
                    public void onError() {

                    }

                    @Override
                    public void onClearToken() {
                        Utils.toLogin(QuestionActivity.this);
                    }
                });
//        new MyRequest().myRequest(API.osstoken, false, null, true, this, null, null, new Response() {
//            @Override
//            public void response(JsonObject json, JsonArray jsonArray, boolean isNull) {
//                String ak = json.get("AccessKeyId").getAsString();
//                String sk = json.get("AccessKeySecret").getAsString();
//                String token = json.get("SecurityToken").getAsString();
//                String expiration = json.get("Expiration").getAsString();
//                SharePref.put(QuestionActivity.this, API.ak, ak);
//                SharePref.put(QuestionActivity.this, API.sk, sk);
//                SharePref.put(QuestionActivity.this, API.myosstoken, token);
//                SharePref.put(QuestionActivity.this, API.expiration, expiration);
//                alioss = AliOSS.getOss(QuestionActivity.this);
//            }
//        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left:
                finish();
                break;
            default:
                break;
        }
    }
    private void commit(){
//        promptDialog.showLoading("????????????????????????");
        List<String> list = new ArrayList<>();
        if (mSelectPath.size() > 0) {
            recursiveUploadPhoto(Utils.getAccount(this), 0, list);
        } else {
            uploadQuestion(null);
        }
    }
    public byte[] decodeSampledBitmap(String pathName, int reqWidth, int reqHeight) {
        Bitmap bitmaps =  BitmapFactory.decodeFile(pathName);
        Log.d(Utils.TAG, bitmaps.getWidth()+"?????????????????????"+bitmaps.getHeight());
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap bitmap =  BitmapFactory.decodeFile(pathName, options);
        Log.d(Utils.TAG, bitmap.getWidth()+"??????????????????????????????"+bitmap.getHeight());
        return compressImage(bitmap);
    }
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
    /**
     * ?????????????????????
     */
    private void recursiveUploadPhoto(final String account, final int mark, List<String> list) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                String objectKey = account + "_" + System.currentTimeMillis() + ".jpg";
                PutObjectRequest put = new PutObjectRequest(Utils.getBucketName(QuestionActivity.this), objectKey, decodeSampledBitmap(mSelectPath.get(mark), 600, 400));
                list.add(objectKey);
                try {
                    PutObjectResult putResult = alioss.putObject(put);
                    Log.d(Utils.TAG, "UploadSuccess");
                    Log.d(Utils.TAG, putResult.getETag());
                    Log.d(Utils.TAG, putResult.getRequestId());
                    if (mark < mSelectPath.size() - 1) {
                        recursiveUploadPhoto(account, mark + 1, list);
                    } else {
                        uploadQuestion(list);
                    }
                } catch (ClientException e) {
                    e.printStackTrace();
                } catch (ServiceException serviceException) {
                }
            }
        });
    }

    /*
     * ????????????
     * */
    private byte[] compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//???????????????????????????100????????????????????????????????????????????????baos???
        int options = 70;
        int a = baos.toByteArray().length;
        while (baos.toByteArray().length / 1024 > 2048) { //?????????????????????????????????????????????100kb,??????????????????
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//????????????options%?????????????????????????????????baos???
            options -= 10;
            Log.d(Utils.TAG, "???????????????");
        }
        return baos.toByteArray();
    }

    /**
     * ?????????????????????????????????
     *
     * @param objectKey
     */
    private void uploadQuestion(final List<String> objectKey) {
        final String content = binding.etQuestion.getText().toString();
        if ("".equals(content) || null == content) {
            Utils.snackbar(this, "?????????????????????");
            return;
        }
        String picture = "";
        if (objectKey != null) {
            String ss = "";
            for (int i = 0; i < objectKey.size() - 1; i++) {
                ss = ss + objectKey.get(i) + ",";
            }
            picture = ss + objectKey.get(objectKey.size() - 1);
        } else {
            picture = "";
        }
        String mycontent = binding.etQuestion.getText().toString();
//        String json = JsonSplice.leftparent+JsonSplice.yin+"exId"+JsonSplice.yinandmao+exId+JsonSplice.dou+
//                JsonSplice.yin+"siteId"+JsonSplice.yinandmao+siteid+JsonSplice.dou+
//                JsonSplice.yin+"picture"+JsonSplice.yinandmao+JsonSplice.yin+picture+JsonSplice.yinanddou+
//                JsonSplice.yin+"content"+JsonSplice.yinandmao+JsonSplice.yin+mycontent+JsonSplice.yin+JsonSplice.rightparent;
//        new MyRequest().spliceJson(API.questionCommit, true, json, QuestionActivity.this, promptDialog, null, new Response() {
//            @Override
//            public void response(JsonObject json, JsonArray jsonArray, boolean isNull) {
//                Utils.snackbar(QuestionActivity.this, "????????????????????????");
//                finish();
//            }
//        });
        QuestionUploadRequest questionUploadRequest = new QuestionUploadRequest();
        questionUploadRequest.setExId(exId);
        questionUploadRequest.setSiteId(siteid);
        questionUploadRequest.setPicture(picture);
        questionUploadRequest.setContent(mycontent);
        InternetWorkManager.getRequest().questionUpload(Utils.body(Mygson.getInstance().toJson(questionUploadRequest)))
                .compose(RxHelper.observableIOMain(this))
                .subscribe(new MyObserver<User>() {
                    @Override
                    public void onSuccess(User data) {
                        Utils.snackbar(QuestionActivity.this, "????????????????????????");
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
                        Utils.toLogin(QuestionActivity.this);
                    }
                });
    }

    private void pickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.mis_permission_rationale),
                    REQUEST_STORAGE_READ_ACCESS_PERMISSION);
        } else {
            int maxNum = 4;
            MultiImageSelector selector = MultiImageSelector.create(QuestionActivity.this);
            selector.showCamera(true);
            selector.count(maxNum);
            selector.multi();
            selector.origin(mSelectPath);
            selector.start(QuestionActivity.this, REQUEST_IMAGE);
        }
    }

    private void requestPermission(final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.mis_permission_dialog_title)
                    .setMessage(rationale)
                    .setPositiveButton(R.string.mis_permission_dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(QuestionActivity.this, new String[]{permission}, requestCode);
                        }
                    })
                    .setNegativeButton(R.string.mis_permission_dialog_cancel, null)
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_STORAGE_READ_ACCESS_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                mSelectPath = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
                adapter = new MyPhotoAdapter(QuestionActivity.this, mSelectPath);
                binding.gridview.setAdapter(adapter);
                binding.gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (i == mSelectPath.size()) {
                            pickImage();
                        } else {
                            mSelectPath.remove(i);
                            adapter = new MyPhotoAdapter(QuestionActivity.this, mSelectPath);
                            binding.gridview.setAdapter(adapter);
                        }
                    }
                });

            }
        }
    }
}
