package com.yxjme.zinengaddress;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import androidx.core.app.ActivityCompat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CallPhoneUtil {


    /**
     * 调用拨号功能
     * @param phone 电话号码
     */
    public static void call(Context context, String phone) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        context.startActivity(intent);
    }


    /**
     * 调用拨号界面
     *
     * @param phone 电话号码
     */
    private void call1(Context context, String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phone));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }



    /**
     * 查询符合的手机号码
     * @param str
     */
    public static String checkCellphone(String str){
        if (TextUtils.isEmpty( checkNumber(str)))return null ;
        String phone = null;
        // 将给定的正则表达式编译到模式中
        Pattern pattern = Pattern.compile("((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(16([0-9]))|(18[0,5-9]))\\d{8}");
        // 创建匹配给定输入与此模式的匹配器。
        Matcher matcher = pattern.matcher(checkNumber(str));
        //查找字符串中是否有符合的子字符串
        while(matcher.find()){
            //查找到符合的即输出
            phone = matcher.group();
        }
        return phone ;
    }


    /**
     * 找出 字符串中的所有数字   判断连续的数字长度为11
     *
     * @param str
     * @return
     */
    public  static String checkNumber(String str){
        Pattern pattern = Pattern.compile("[^0-9]");
        String[] s = pattern.split(str);
        String num = null;
        if (s!=null && s.length>0){
            for(String val:s){
                if (val.length()==11){
                    num = val ;
                    break;
                }
            }
            return num ;
         }
        return null ;
    }
}
