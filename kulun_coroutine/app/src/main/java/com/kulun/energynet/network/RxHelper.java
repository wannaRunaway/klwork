package com.kulun.energynet.network;

import android.app.Activity;
import android.content.Context;

import androidx.lifecycle.LifecycleOwner;

import com.trello.rxlifecycle4.android.ActivityEvent;
import com.trello.rxlifecycle4.components.RxActivity;
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity;
import com.trello.rxlifecycle4.components.support.RxFragmentActivity;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.ObservableTransformer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RxHelper {
    public static <T> ObservableTransformer<T,T> observableIOMain(Activity context){
        return upstream -> {
             Observable<T> observable = upstream.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
             return composeContext(context, observable);
        };
    }

    private static <T> ObservableSource<T> composeContext(Context context, Observable<T> observable) {
        if(context instanceof RxActivity) {
            return observable.compose(((RxActivity) context).bindUntilEvent(ActivityEvent.DESTROY));
        } else if(context instanceof RxFragmentActivity){
            return observable.compose(((RxFragmentActivity) context).bindUntilEvent(ActivityEvent.DESTROY));
        }else if(context instanceof RxAppCompatActivity){
            return observable.compose(((RxAppCompatActivity) context).bindUntilEvent(ActivityEvent.DESTROY));
        }else {
            return observable;
        }
    }
}
