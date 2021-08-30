package com.kulun.energynet.utils;
import com.google.gson.Gson;

/**
 * created by xuedi on 2018/10/17
 * gson解析
 */
public class Mygson {
    private volatile static Gson gson;
    public static Gson getInstance(){
        if (null == gson){
            synchronized (Mygson.class){
                if (null == gson){
                    gson = new Gson();
                }
            }
        }
        return gson;
    }
}
