package com.kulun.energynet.mine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.kulun.energynet.R;
import com.kulun.energynet.model.Message;
import com.kulun.energynet.model.ResponseModel;
import com.kulun.energynet.network.InternetWorkManager;
import com.kulun.energynet.network.MyObserver;
import com.kulun.energynet.network.RxHelper;
import com.kulun.energynet.requestparams.MyRequest;
import com.kulun.energynet.requestparams.Response;
import com.kulun.energynet.network.API;
import com.kulun.energynet.utils.Mygson;
import com.kulun.energynet.utils.Utils;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MessageSystemFragment extends Fragment {
    private SmartRefreshLayout smartRefreshLayout;
    private RecyclerView recyclerView;
    private LinearLayout image;
    private int pageNo = 0;
    private Myadapter myadapter;
    private List<Message> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_system, null);
        smartRefreshLayout = view.findViewById(R.id.smart_refresh);
        recyclerView = view.findViewById(R.id.recyclerView);
        image = view.findViewById(R.id.image);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        myadapter = new Myadapter(list);
        recyclerView.setAdapter(myadapter);
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                pageNo = 0;
                loadData(true, pageNo);
            }
        });
        smartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                pageNo = pageNo + 1;
                loadData(false, pageNo);
            }
        });
        loadData(true, pageNo);
        return view;
    }

    private void loadData(boolean isRefresh, int pageNo) {
//        String json = "type=0&page="+pageNo+"&size=20";
//        new MyRequest().spliceJson(API.messagelist, false, json, getActivity(), null, smartRefreshLayout, new Response() {
//            @Override
//            public void response(JsonObject json, JsonArray jsonArray,boolean isNull) {
//                if (isNull){
//                    if (isRefresh) {
//                        list.clear();
//                    }
//                    image.setVisibility(list.size()==0?View.VISIBLE:View.GONE);
//                }
//                if (jsonArray != null) {
//                    if (isRefresh) {
//                        list.clear();
//                    }
//                    List<Message> mylist = Mygson.getInstance().fromJson(jsonArray, new TypeToken<List<Message>>() {
//                    }.getType());
//                    list.addAll(mylist);
//                    myadapter.notifyDataSetChanged();
//                    image.setVisibility(list.size()==0?View.VISIBLE:View.GONE);
//                }
//            }
//        });
        InternetWorkManager.getRequest().messageList(0, pageNo, 20)
                .compose(RxHelper.observableIOMain(getActivity()))
                .subscribe(new MyObserver<List<Message>>() {
                    @Override
                    public void onSuccess(List<Message> data) {
                        Utils.finishRefresh(smartRefreshLayout);
                        if (data == null) {
                            if (isRefresh) {
                                list.clear();
                            }
                            image.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);
                            return;
                        }
                        if (isRefresh) {
                            list.clear();
                        }
                        list.addAll(data);
                        myadapter.notifyDataSetChanged();
                        image.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);
                    }

                    @Override
                    public void onFail(int code, String message) {
                        Utils.finishRefresh(smartRefreshLayout);
                    }

                    @Override
                    public void onError() {
                        Utils.finishRefresh(smartRefreshLayout);
                    }

                    @Override
                    public void onClearToken() {
                        Utils.toLogin(getActivity());
                    }
                });
    }

    private class Myadapter extends RecyclerView.Adapter<MyViewHolder> {
        private List<Message> adapterlist;

        public Myadapter(List<Message> list) {
            adapterlist = list;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.adapter_message, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Message data = list.get(position);
            holder.name.setText(data.getContent());
            holder.time.setText(data.getCreate_time());
            if (data.isIs_top()) {
                holder.topIv.setVisibility(View.VISIBLE);
            } else {
                holder.topIv.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return adapterlist.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name, time;
        private ImageView topIv;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_name);
            time = itemView.findViewById(R.id.tv_time);
            topIv = itemView.findViewById(R.id.iv_top);
        }
    }
}
