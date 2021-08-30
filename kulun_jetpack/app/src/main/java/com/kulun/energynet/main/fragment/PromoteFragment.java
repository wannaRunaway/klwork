package com.kulun.energynet.main.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.kulun.energynet.R;
import com.kulun.energynet.databinding.FragmentPromoteBinding;
import com.kulun.energynet.model.User;
import com.kulun.energynet.network.API;
import com.kulun.energynet.popup.SharePopup;
import com.kulun.energynet.utils.Utils;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.trello.rxlifecycle4.components.support.RxFragment;

public class PromoteFragment extends RxFragment implements Shareinterface {
    private FragmentPromoteBinding binding;
    private static final int THUMB_SIZE = 150;
    private IWXAPI iwxapi;
    private SharePopup sharePopup;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_promote, null);
        iwxapi = WXAPIFactory.createWXAPI(getContext(), API.wxappid);
        binding = DataBindingUtil.bind(view);
        binding.webview.getSettings().setUseWideViewPort(true);
        binding.webview.getSettings().setLoadWithOverviewMode(true);
        binding.webview.loadUrl(API.weburl);
        sharePopup = new SharePopup(getContext(), this);
        binding.share.setOnClickListener(v -> {
            sharePopup.showPopupWindow();
        });
        return view;
    }

    private void share(boolean iscircle) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = API.weburl;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "微型换电宝—低成本、高回报、专业售后服务";
        msg.description = "库仑能网";
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.icon);
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
        bmp.recycle();
        msg.thumbData = Utils.bmpToByteArray(thumbBmp, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        if (iscircle){
            req.scene = mTargetScenecircle;
        }else {
            req.scene = mTargetSceneothers;
        }
        iwxapi.sendReq(req);
    }

    public void onresume() {//promotefragment show
    }

    private int mTargetScenecircle = SendMessageToWX.Req.WXSceneTimeline;
    private int mTargetSceneothers = SendMessageToWX.Req.WXSceneSession;
    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    @Override
    public void sharetocircle() {
        share(true);
    }

    @Override
    public void sharetoothers() {
        share(false);
    }
}
