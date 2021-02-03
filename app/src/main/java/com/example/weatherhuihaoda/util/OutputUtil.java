package com.example.weatherhuihaoda.util;

import android.content.Context;

import com.example.weatherhuihaoda.entity.TodayWeather;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import static com.baidu.mapapi.BMapManager.getContext;

/**
 * Time:         2021/1/21
 * Author:       C
 * Description:  OutputUtil
 * on:
 */

public class OutputUtil {

    //将map集合存到本地
    public static boolean writeObjectIntoLocal(Context context, String fileName, Map<String,TodayWeather> bean) {
        try {
            // 通过openFileOutput方法得到一个输出流，方法参数为创建的文件名（不能有斜杠），操作模式
            @SuppressWarnings("deprecation")
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(bean);//写入
            fos.close();//关闭输入流
            oos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            //Toast.makeText(WebviewTencentActivity.this, "出现异常2",Toast.LENGTH_LONG).show();
            return false;
        }
    }

    //读取本地map集合
    public static Map readObjectFromLocal(Context context,String fielName){
        Map bean;
        try {
            FileInputStream fis = context.openFileInput(fielName);//获得输入流
            ObjectInputStream ois = new ObjectInputStream(fis);
            bean = (Map) ois.readObject();
            fis.close();
            ois.close();
            return bean;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            //Toast.makeText(ShareTencentActivity.this,"出现异常6",Toast.LENGTH_LONG).show();//弹出Toast消息
            e.printStackTrace();
            return null;
        }
    }

    // 判断文件是否存在
    public static boolean fileIsExists(String strFile) {
        try {
            File file = getContext().getFileStreamPath(strFile);
                if (file.exists()) {
                    return true;
                }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
