package com.kulun.energynet.network;

import com.kulun.energynet.model.ResponseModel;
import com.kulun.energynet.model.User;
import com.kulun.energynet.utils.SharePref;
import com.kulun.energynet.utils.Utils;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public abstract class MyObserver<T> implements Observer<ResponseModel<T>> {
    @Override
    public void onSubscribe(@NonNull Disposable d) {
        Utils.log("myobserver onSubscribe");
    }

    @Override
    public void onNext(@NonNull ResponseModel<T> tResponseModel) {
        if (tResponseModel.getCode() == 0) {
            onSuccess(tResponseModel.getData());
        } else {
            if (!tResponseModel.getMessage().equals("未查询到优惠券")) {
                Utils.snackbar(tResponseModel.getMessage());
            }
            onFail(tResponseModel.getCode(), tResponseModel.getMessage());
            if (tResponseModel.getCode() == 5 || tResponseModel.getCode() == 4) {//4是过期，5是token不对，其他的错误暂时都是-1
                onClearToken();
            }
        }
    }

    @Override
    public void onError(@NonNull Throwable e) {
        Utils.snackbar(API.net_error);
        onError();
        Utils.log("myobserver onError");
    }

    @Override
    public void onComplete() {
        Utils.log("myobserver onComplete");
    }

    public abstract void onSuccess(T data);

    public abstract void onFail(int code, String message);

    public abstract void onError();

    public abstract void onClearToken();
}
