package com.kulun.energynet.main;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.kulun.energynet.R;
import com.kulun.energynet.databinding.ActivityMainBinding;
import com.kulun.energynet.main.fragment.ListFragment;
import com.kulun.energynet.main.fragment.MainFragment;
import com.kulun.energynet.main.fragment.MineFragment;
import com.kulun.energynet.main.fragment.PromoteFragment;
import com.kulun.energynet.model.User;
import com.kulun.energynet.network.API;
import com.kulun.energynet.utils.Utils;
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity;

public class MainActivity extends RxAppCompatActivity implements View.OnClickListener {
    private ActivityMainBinding binding;
    private Fragment mainFragment, listFragment, promotefragment, minefragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initFragment();
        onclick();
//        ImmersionBar.with(this).init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //activity onresume fragment show
        if (mainFragment != null && !mainFragment.isHidden()) {
            ((MainFragment) mainFragment).onresume();
        }
        if (listFragment != null && !listFragment.isHidden()) {
            ((ListFragment) listFragment).onresume();
        }
        if (promotefragment != null && !promotefragment.isHidden()) {
            ((PromoteFragment) promotefragment).onresume();
        }
        if (minefragment != null && !minefragment.isHidden()) {
            ((MineFragment) minefragment).onresume();
        }
    }

    private void onclick() {
        binding.l1.setOnClickListener(this);
        binding.l2.setOnClickListener(this);
        binding.l3.setOnClickListener(this);
        binding.l4.setOnClickListener(this);
    }

    private void initFragment() {//初始化activity
        if (mainFragment == null) {
            mainFragment = new MainFragment();
        }
        if (promotefragment == null) {
            promotefragment = new PromoteFragment();
        }
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, mainFragment)
                .add(R.id.content, promotefragment)
                .show(mainFragment)
                .hide(promotefragment)
                .commitAllowingStateLoss();
        binding.mainImage.setImageResource(R.mipmap.main_selected);
        binding.mainText.setTextColor(getResources().getColor(R.color.text_color));
    }

    public void mainScan() {
        listtoMainInit();
        ((MainFragment) mainFragment).scan();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.l1:
                clickMainFragment();
                break;
            case R.id.l2:
                clickListFragment();
                break;
            case R.id.l3:
                clickPromoteFragment();
                break;
            case R.id.l4:
                clickMineFragment();
                break;
            default:
                break;
        }
    }

    private void listtoMainInit() {
        if (mainFragment == null) {
            mainFragment = new MainFragment();
        }
        if (mainFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().hide(listFragment).show(mainFragment).commitAllowingStateLoss();
        } else {
            getSupportFragmentManager().beginTransaction().add(R.id.content, mainFragment).hide(listFragment).show(mainFragment).commitAllowingStateLoss();
        }
        binding.mainImage.setImageResource(R.mipmap.main_selected);
        binding.mainText.setTextColor(getResources().getColor(R.color.text_color));
        binding.listImage.setImageResource(R.mipmap.list);
        binding.listText.setTextColor(getResources().getColor(R.color.black));
    }

    private void clickMainFragment() {
        hideAll();
        if (mainFragment == null) {
            mainFragment = new MainFragment();
        }
        if (mainFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().show(mainFragment).commitAllowingStateLoss();
        } else {
            getSupportFragmentManager().beginTransaction().add(R.id.content, mainFragment).show(mainFragment).commitAllowingStateLoss();
        }
        getSupportFragmentManager().beginTransaction().show(mainFragment).commitAllowingStateLoss();
        binding.mainImage.setImageResource(R.mipmap.main_selected);
        binding.mainText.setTextColor(getResources().getColor(R.color.text_color));
    }

    private void clickListFragment() {
        hideAll();
        if (listFragment == null) {
            listFragment = new ListFragment();
        }
        if (listFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().show(listFragment).commitAllowingStateLoss();
        } else {
            getSupportFragmentManager().beginTransaction().add(R.id.content, listFragment).show(listFragment).commitAllowingStateLoss();
        }
        binding.listImage.setImageResource(R.mipmap.list_selected);
        binding.listText.setTextColor(getResources().getColor(R.color.text_color));
    }

    private void clickPromoteFragment() {
        hideAll();
        if (promotefragment == null) {
            promotefragment = new PromoteFragment();
        }
        if (promotefragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().show(promotefragment).commitAllowingStateLoss();
        } else {
            getSupportFragmentManager().beginTransaction().add(R.id.content, promotefragment).show(promotefragment).commitAllowingStateLoss();
        }
        binding.promoteImage.setImageResource(R.mipmap.promote_selected);
        binding.promoteText.setTextColor(getResources().getColor(R.color.text_color));
    }

    private void clickMineFragment() {
        hideAll();
        if (minefragment == null) {
            minefragment = new MineFragment();
        }
        if (minefragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().show(minefragment).commitAllowingStateLoss();
        } else {
            getSupportFragmentManager().beginTransaction().add(R.id.content, minefragment).show(minefragment).commitAllowingStateLoss();
        }
        binding.mineImage.setImageResource(R.mipmap.mine_selected);
        binding.mineText.setTextColor(getResources().getColor(R.color.text_color));
    }

    public void listomainFragmentClickAdapterReserver() {//listadapter click reserver
        listtoMainInit();
        if (mainFragment != null) {
            ((MainFragment) mainFragment).getcurrentAppint();
        }
    }

    private void hideAll() {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment.isAdded() && !fragment.isHidden()) {
                getSupportFragmentManager().beginTransaction().hide(fragment).commitAllowingStateLoss();
            }
        }
        clearTextAndcolor();
    }

    private void clearTextAndcolor() {
        binding.mainImage.setImageResource(R.mipmap.main);
        binding.listImage.setImageResource(R.mipmap.list);
        binding.promoteImage.setImageResource(R.mipmap.promote);
        binding.mineImage.setImageResource(R.mipmap.mine);
        binding.mainText.setTextColor(getResources().getColor(R.color.black));
        binding.listText.setTextColor(getResources().getColor(R.color.black));
        binding.promoteText.setTextColor(getResources().getColor(R.color.black));
        binding.mineText.setTextColor(getResources().getColor(R.color.black));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
