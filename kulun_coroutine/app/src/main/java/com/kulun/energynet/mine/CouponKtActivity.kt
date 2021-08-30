package com.kulun.energynet.mine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kulun.energynet.R
import com.kulun.energynet.databinding.ActivityCouponBinding
import com.kulun.energynet.databinding.AdapterCouponBinding
import com.kulun.energynet.main.BaseActivity
import com.kulun.energynet.model.Coupon
import com.kulun.energynet.network.InternetWorkManager
import com.kulun.energynet.network.MyObserver
import com.kulun.energynet.network.RxHelper
import com.kulun.energynet.requestbody.CouponRequest
import com.kulun.energynet.utils.DateUtils
import com.kulun.energynet.utils.Mygson
import com.kulun.energynet.utils.Utils

class CouponKtActivity : BaseActivity() {
    private lateinit var binding: ActivityCouponBinding
    private lateinit var adapter: MyAdapter
    private var couponList: ArrayList<Coupon> = ArrayList<Coupon>()
    private var pageNo: Int = 0

    override fun initView(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_coupon)
        binding.header.left.setOnClickListener { finish() }
        binding.header.title.setText("优惠券")
        binding.smartRefresh.setOnRefreshListener {
            pageNo = 0
            loadData(true)
        }
        binding.smartRefresh.setOnLoadMoreListener {
            pageNo = pageNo + 1
            loadData(false)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        adapter = MyAdapter(couponList)
        binding.recyclerView.adapter = adapter
        binding.smartRefresh.autoRefresh()
    }

    private fun loadData(isrefresh: Boolean) {
        var conponRequest = CouponRequest()
        conponRequest.page = pageNo
        InternetWorkManager.getRequest().couponList(Utils.body(Mygson.getInstance().toJson(conponRequest)))
                .compose(RxHelper.observableIOMain(this@CouponKtActivity))
                .subscribe(object : MyObserver<List<Coupon>>() {
                    override fun onSuccess(data: List<Coupon>?) {
                        Utils.finishRefresh(binding.smartRefresh)
                        if (data == null) {
                            binding.image.visibility = if (couponList.size > 0) View.GONE else View.VISIBLE
                            return
                        }
                        if (data != null) {
                            if (isrefresh) {
                                couponList.clear()
                            }
                            couponList.addAll(data)
                            binding.image.visibility = if (couponList.size > 0) View.GONE else View.VISIBLE
                            adapter.notifyDataSetChanged()
                        }
                    }

                    override fun onFail(code: Int, message: String?) {
                        Utils.finishRefresh(binding.smartRefresh)
                        if (code == -1 && couponList.size==0){
                            binding.image.visibility = View.VISIBLE
                        }
                    }

                    override fun onError() {
                        Utils.finishRefresh(binding.smartRefresh)
                    }

                    override fun onClearToken() {
                        Utils.toLogin(this@CouponKtActivity)
                    }
                })
    }
}

private class MyAdapter(couponList: ArrayList<Coupon>) : RecyclerView.Adapter<MyViewHolder>() {
    private var couponList: ArrayList<Coupon> = couponList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.adapter_coupon, null)
        val adapterbinding: AdapterCouponBinding = DataBindingUtil.bind(view)!!
        return MyViewHolder(adapterbinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        /*
        * Coupon data = list.get(position);
            holder.name.setText(data.getCouponName());
            holder.money.setText(data.getAmount() + "元");
            holder.text.setText("· 剩余" + (data.getAmount() - data.getUsed()) + "元未使用");
            holder.time.setText("· " + DateUtils.timeToDate(data.getBeginDate()) + "至" + DateUtils.timeToDate(data.getExpireDate()));
        * */
        var data: Coupon = couponList.get(position)
        holder.adapterbinding.tvName.setText(data.couponName)
        holder.adapterbinding.tvMoney.setText(data.amount.toString()+"元")
        holder.adapterbinding.text.setText("· 剩余" + (data.getAmount() - data.getUsed()) + "元未使用")
        holder.adapterbinding.tvTime.setText("· " + DateUtils.timeToDate(data.getBeginDate()) + "至" + DateUtils.timeToDate(data.getExpireDate()))
    }

    override fun getItemCount(): Int {
        return couponList.size
    }

}

private class MyViewHolder(adapterbinding: AdapterCouponBinding) : RecyclerView.ViewHolder(adapterbinding.root) {
//    fun MyViewHolder(adapterbinding: AdapterCouponBinding) {
//        setBinding(adapterbinding)
//    }

    var adapterbinding: AdapterCouponBinding = adapterbinding
//    fun setBinding(binding: AdapterCouponBinding) {
//        this.adapterbinding = binding
//    }
//
}
