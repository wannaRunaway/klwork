package com.kulun.energynet.mine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kulun.energynet.R
import com.kulun.energynet.databinding.ActivityReserveBinding
import com.kulun.energynet.databinding.AdapterReserverBinding
import com.kulun.energynet.main.BaseActivity
import com.kulun.energynet.model.Reserver
import com.kulun.energynet.network.InternetWorkManager
import com.kulun.energynet.network.MyObserver
import com.kulun.energynet.network.RxHelper
import com.kulun.energynet.utils.Utils

class AppointmentKtActivity : BaseActivity() {
    private lateinit var binding: ActivityReserveBinding
    private lateinit var adapter: MyAdapter
    private var list = ArrayList<Reserver>()
    override fun initView(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reserve)
        binding.header.left.setOnClickListener { finish() }
        binding.header.title.setText("我的预约")
        binding.smartRefresh.setOnRefreshListener {
            loadData()
        }
        binding.smartRefresh.autoRefresh()
        adapter = MyAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        binding.recyclerView.adapter = adapter
    }

    private fun loadData() {
        InternetWorkManager.getRequest().appointmentList()
            .compose(RxHelper.observableIOMain(this))
            .subscribe(object : MyObserver<List<Reserver>?>() {

                override fun onFail(code: Int, message: String) {
                    Utils.finishRefresh(binding.smartRefresh)
                }

                override fun onError() {
                    Utils.finishRefresh(binding.smartRefresh)
                }

                override fun onClearToken() {
                    Utils.toLogin(this@AppointmentKtActivity)
                }

                override fun onSuccess(data: List<Reserver>?) {
                    Utils.finishRefresh(binding.smartRefresh)
                    if (data == null) {
                        binding.image.visibility = View.VISIBLE
                        return
                    }
                    list.clear()
                    list.addAll(data)
                    adapter.notifyDataSetChanged()
                    binding.image.visibility = if (list.size > 0) View.GONE else View.VISIBLE
                }
            })
    }

    inner class MyAdapter : RecyclerView.Adapter<MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_reserver, null)
            var adapterBinding = AdapterReserverBinding.bind(view)
            return MyViewHolder(adapterBinding)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val contentBean = list[position]
            var status = ""
            when (contentBean.status) {
                0 -> {
                    status = "预约中"
                    colorReserverOrExchangeBatteryDone(holder)
                }
                1 -> {
                    status = "取消预约"
                    colorNotDone(holder)
                }
                2 -> {
                    status = "预约超时"
                    colorNotDone(holder)
                }
                3 -> {
                    status = "换电完成"
                    colorReserverOrExchangeBatteryDone(holder)
                }
                else -> {
                }
            }
            holder.adapterbinding.tvStatus.setText(status)
            holder.adapterbinding.tvStation.setText(contentBean.site)
            holder.adapterbinding.carplate.setText("车牌号：" + contentBean.plate)
            holder.adapterbinding.tvTime.setText("预约时间：" + contentBean.time)
        }

        private fun colorReserverOrExchangeBatteryDone(holder: MyViewHolder) {
            holder.adapterbinding.tvStatus.setTextColor(ContextCompat.getColor(applicationContext,R.color.black))
            holder.adapterbinding.tvTime.setTextColor(ContextCompat.getColor(applicationContext,R.color.black))
            holder.adapterbinding.tvStation.setTextColor(ContextCompat.getColor(applicationContext,R.color.black))
            holder.adapterbinding.carplate.setTextColor(ContextCompat.getColor(applicationContext,R.color.black))
        }

        private fun colorNotDone(holder: MyViewHolder) {
            holder.adapterbinding.tvStatus.setTextColor(ContextCompat.getColor(applicationContext,R.color.reserverunfinish))
            holder.adapterbinding.tvTime.setTextColor(ContextCompat.getColor(applicationContext,R.color.reserverunfinish))
            holder.adapterbinding.tvStation.setTextColor(ContextCompat.getColor(applicationContext,R.color.reserverunfinish))
            holder.adapterbinding.carplate.setTextColor(ContextCompat.getColor(applicationContext,R.color.reserverunfinish))
        }

        override fun getItemCount(): Int = list.size
    }

    class MyViewHolder(adapterbinding: AdapterReserverBinding) :
        RecyclerView.ViewHolder(adapterbinding.root) {
        var adapterbinding = adapterbinding;
    }
}