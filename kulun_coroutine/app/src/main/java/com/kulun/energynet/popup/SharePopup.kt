package com.kulun.energynet.popup

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import androidx.databinding.DataBindingUtil
import com.kulun.energynet.R
import com.kulun.energynet.databinding.PopupShareBinding
import com.kulun.energynet.main.fragment.PromoteFragment
import com.kulun.energynet.main.fragment.Shareinterface
import razerdp.basepopup.BasePopupWindow

class SharePopup(context: Context?, inter: Shareinterface) : BasePopupWindow(context) {
    public lateinit var binding: PopupShareBinding
    private var myinterface = inter
    override fun onCreateContentView(): View {
        var view = createPopupById(R.layout.popup_share)
        binding = DataBindingUtil.bind(view)!!
        binding.cancel.setOnClickListener { dismiss() }
        binding.weiFriend.setOnClickListener {
            myinterface.sharetoothers()
            dismiss()
        }
        binding.weiCircle.setOnClickListener {
            myinterface.sharetocircle()
            dismiss()
        }
        setPopupGravity(Gravity.BOTTOM)
        return view
    }
}