package com.kulun.energynet.customizeView;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.kulun.energynet.main.MyApplication;

public class TextViewDinCondensedBold extends AppCompatTextView {

    Typeface tfDinConBold = Typeface.createFromAsset(MyApplication.getInstance().getAssets(), "fonts/DIN Condensed Bold.woff.ttf");

    public TextViewDinCondensedBold(Context context) {
        super(context);
        setTypeface(tfDinConBold);
    }

    public TextViewDinCondensedBold(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setTypeface(tfDinConBold);
    }

    public TextViewDinCondensedBold(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(tfDinConBold);
    }
}
