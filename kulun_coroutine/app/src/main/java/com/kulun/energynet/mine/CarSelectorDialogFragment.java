package com.kulun.energynet.mine;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kulun.energynet.R;
import com.kulun.energynet.databinding.FragmentCarSelectDialogBinding;
import com.kulun.energynet.model.Car;
import com.kulun.energynet.model.ResponseModel;
import com.kulun.energynet.network.InternetWorkManager;
import com.kulun.energynet.network.MyObserver;
import com.kulun.energynet.network.RxHelper;
import com.kulun.energynet.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * created by xuedi on 2019/1/22
 */
public class CarSelectorDialogFragment extends DialogFragment implements View.OnClickListener {
    private FragmentCarSelectDialogBinding binding;
    private List<Car> list = new ArrayList<>();
    private CarSelectorAdapter adapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_NoActionBar_Fullscreen);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_car_select_dialog, container);
        binding = FragmentCarSelectDialogBinding.bind(view);
        binding.header.left.setOnClickListener(this);
//        binding.header.right.setOnClickListener(this);
//        binding.header.right.setText("确定");
        binding.header.title.setText("我的车辆");
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new CarSelectorAdapter(getActivity(), list);
        binding.recyclerView.setAdapter(adapter);
        loadCar();
        return view;
    }

    private void loadCar() {//车辆列表
        InternetWorkManager.getRequest().carlist()
                .compose(RxHelper.observableIOMain(getActivity()))
                .subscribe(new MyObserver<List<Car>>() {
                    @Override
                    public void onSuccess(List<Car> data) {
                        if (data!=null && data.size()>0){
                            list.clear();
                            list.addAll(data);
                            adapter.notifyDataSetChanged();
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

                    }
                });
//        new MyRequest().myRequest(API.getmycarlist, false, null, false, getActivity(), null, null, new Response() {
//            @Override
//            public void response(JsonObject json, JsonArray jsonArray,boolean isNull) {
//                list.clear();
//                list.addAll(Mygson.getInstance().fromJson(jsonArray, new TypeToken<List<Car>>() {
//                }.getType()));
//                adapter.notifyDataSetChanged();
//            }
//        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left:
                dismissAllowingStateLoss();
                break;
            default:
                break;
        }
    }

    private class CarSelectorAdapter extends RecyclerView.Adapter<CarSelectorAdapter.CarSelectorViewHolder> {
        private Context context;
        private List<Car> bindCars;

        public CarSelectorAdapter(Context context, List<Car> bindCars) {
            this.context = context;
            this.bindCars = bindCars;
        }

        @Override
        public CarSelectorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_carident, null);
            CarSelectorViewHolder holder = new CarSelectorViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(CarSelectorViewHolder holder, final int position) {
            final Car bindCar = bindCars.get(position);
            holder.re_contain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((PackageDetailActivity) getActivity()).click(bindCar.getPlate_number(), bindCar.getId());
                    dismissAllowingStateLoss();
                }
            });
            holder.car_num.setText(bindCar.getPlate_number());
        }

        @Override
        public int getItemCount() {
            return bindCars.size();
        }

        class CarSelectorViewHolder extends RecyclerView.ViewHolder {
            private TextView car_num;
            private ImageView img_selected;
            private RelativeLayout re_contain;

            public CarSelectorViewHolder(View itemView) {
                super(itemView);
                car_num = itemView.findViewById(R.id.car_num);
                img_selected = itemView.findViewById(R.id.img_selected);
                re_contain = itemView.findViewById(R.id.re_contain);
            }
        }
    }
}
