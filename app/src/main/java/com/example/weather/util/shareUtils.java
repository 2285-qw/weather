package com.example.weather.util;

import android.content.Context;
import android.content.SharedPreferences;

/*项目名：   fanyi
 *包名：     com.example.translatehuihaoda.utils
 *文件名：   shareUtils
 *创建者：   CEH
 *创建时间： 2020/11/20 10:51
 *描述：     SharedPreferences封装
 */
public class shareUtils {
    public static final String NAME="config";

    //键 值
    public static void putString(Context mContext, String key, String value){
        SharedPreferences sp=mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        sp.edit().putString(key,value).commit();
    }
    //键 默认值
    public static String getString(Context mContext, String key, String devalue){
        SharedPreferences sp=mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getString(key,devalue);
    }
    //键 值
    public static void putInt(Context mContext, String key, int value){
        SharedPreferences sp=mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        sp.edit().putInt(key,value).commit();
    }
    //键 默认值
    public static int getInt(Context mContext, String key, int devalue){
        SharedPreferences sp=mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getInt(key,devalue);
    }
    //键 值
    public static void putBoolean(Context mContext, String key, Boolean value){
        SharedPreferences sp=mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        sp.edit().putBoolean(key,value).commit();
    }
    //键 默认值
    public static Boolean getBoolean(Context mContext, String key, boolean devalue){
        SharedPreferences sp=mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key,devalue);
    }
    //删除 单个
    public static void deleShare(Context mContext, String key){
        SharedPreferences sp=mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        sp.edit().remove(key).commit();
    }
    //删除 全部
    public static void deleAll(Context mContext){
        SharedPreferences sp=mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        sp.edit().clear().commit();
    }


}
