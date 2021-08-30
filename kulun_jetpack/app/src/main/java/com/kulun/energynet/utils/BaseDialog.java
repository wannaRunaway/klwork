package com.kulun.energynet.utils;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import androidx.appcompat.app.AlertDialog;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.kulun.energynet.R;

import java.util.ArrayList;

import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;

/**
 * created by xuedi on 2019/8/16
 */
public class BaseDialog {
    public static void showDialog(Context context,String titleMessage,String contentMessage,String confirmMessage, View.OnClickListener confirmListener){
        AlertDialog alertDialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.mydialog);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_base, null);
        builder.setView(view);
        alertDialog = builder.create();
        TextView title = view.findViewById(R.id.tv_title);
        TextView cancel = view.findViewById(R.id.tv_cancel);
        TextView confirm = view.findViewById(R.id.tv_confrim);
        TextView message = view.findViewById(R.id.tv_message);
        title.setText(titleMessage);
        cancel.setText("取消");
        confirm.setText(confirmMessage);
        message.setText(contentMessage);
        AlertDialog finalAlertDialog = alertDialog;
        cancel.setOnClickListener(view1 -> {
            finalAlertDialog.dismiss();
        });
        AlertDialog finalAlertDialog1 = alertDialog;
        confirm.setOnClickListener(view1 -> {
            confirmListener.onClick(view1);
            finalAlertDialog1.dismiss();
        });
        alertDialog.show();
    }

    public static void showcodeDialog(Context context, String titleMessage, String contentMessage,String confirmMessage,View.OnClickListener confirmListener){
        AlertDialog alertDialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.mydialog);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_code, null);
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        TextView title = view.findViewById(R.id.tv_title);
        TextView confirm = view.findViewById(R.id.tv_confirm);
        TextView textView = view.findViewById(R.id.tv_message);
        title.setText(titleMessage);
        textView.setText(contentMessage);
        confirm.setText(confirmMessage);
        AlertDialog finalAlertDialog1 = alertDialog;
        confirm.setOnClickListener(view1 -> {
            confirmListener.onClick(view1);
            finalAlertDialog1.dismiss();
        });
        alertDialog.show();
    }

    public static void showcodeblueDialog(Context context, String titleMessage, String contentMessage,String bluelistMessage,String confirmMessage,View.OnClickListener confirmListener){
        AlertDialog alertDialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.mydialog);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_code_blue, null);
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        TextView title = view.findViewById(R.id.tv_title);
        TextView confirm = view.findViewById(R.id.tv_confirm);
        TextView textView = view.findViewById(R.id.tv_message);
        TextView blue = view.findViewById(R.id.tv_message_blue);
        title.setText(titleMessage);
        textView.setText(contentMessage);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(bluelistMessage);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.text_color)),0,bluelistMessage.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        char[] mychar = bluelistMessage.toCharArray();
        for (int i = 0; i < mychar.length; i++) {
            if (((Character)mychar[i]).equals('【')){
                spannableStringBuilder.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)),i,i+1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            }
            if (((Character)mychar[i]).equals('】')){
                spannableStringBuilder.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)),i,i+1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            }
        }
        blue.setText(spannableStringBuilder);
//        blue.setText(bluelistMessage);
        confirm.setText(confirmMessage);
        AlertDialog finalAlertDialog1 = alertDialog;
        confirm.setOnClickListener(view1 -> {
            confirmListener.onClick(view1);
            finalAlertDialog1.dismiss();
        });
        alertDialog.show();
    }

    public static void showQrDialog(Activity context, String carplate){
        AlertDialog alertDialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.mydialog);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_image, null);
        builder.setView(view);
        alertDialog = builder.create();
        ImageView imageView = view.findViewById(R.id.image);
        TextView textView = view.findViewById(R.id.carplate);
        textView.setText(carplate);
        if (carplate.equals("")){
            textView.setVisibility(View.GONE);
        }else {
            textView.setVisibility(View.VISIBLE);
        }
        createQRCodeWithLogo(imageView,context, true,Utils.getAccount(context)+"&"+carplate, 300);
        alertDialog.show();
    }

    public static void createQRCodeWithLogo(ImageView imageView, Context context,boolean isdialog, String accountNo, int size) {
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
//                Bitmap logoBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_car);
                if (isdialog) {
                    return QRCodeEncoder.syncEncodeQRCode(accountNo, size, context.getResources().getColor(R.color.text_color));
                }else {
                    return QRCodeEncoder.syncEncodeQRCode(accountNo, size, context.getResources().getColor(R.color.white), Color.TRANSPARENT, null);
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {
//                    Toast.makeText(context, "生成二维码失败", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
}
