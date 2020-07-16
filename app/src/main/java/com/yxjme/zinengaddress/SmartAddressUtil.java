package com.yxjme.zinengaddress;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;



public abstract class SmartAddressUtil implements TextWatcher {

    private static String  fileName = "area.json" ;
    private List<AddressBean> allDataList=new ArrayList<>();
    private List<Province> list =new ArrayList<>();
    private String result;
    private Context context;


    public SmartAddressUtil(Context context, EditText editText){
        this.context=context;
        if (editText!=null){
            editText.addTextChangedListener(this);
        }
        init();
    }


    /**
     * 初始化数据源
     */
    private void init() {
        allDataList.clear();
        list.clear();
        list .addAll(getAddressList(context)) ;
        if (list!=null&&list.size()>0){
            for (Province p:list){
                for (City c:p.getSubdb()){
                    for (Area a:c.getSubdb()){
                        allDataList.add(new AddressBean("","",p.getName(),c.getName(),a.getName()));
                    }
                }
            }
        }
    }





    /**
     * 获取地址列表
     *
     * @param context
     * @return
     */
    public static List<Province>  getAddressList(Context context) {
        AssetManager assets = context.getAssets();
        InputStream is = null;
        try {
            is = assets.open(fileName);
            byte[] buf = new byte[is.available()];
            is.read(buf);
            String json = new String(buf, "UTF-8");
            Result r = new Gson().fromJson(json, Result.class);
            Log.v("=====mListProvince=", "json = "+json);
            List<Province> list = r.getResult();
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }


    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        result = s.toString();
        if (!TextUtils.isEmpty(result)){
            initCheckAddress(result);
        }
    }



    @Override
    public void afterTextChanged(Editable s) {


    }


    /**
     * 获取  切除手机号码的字符串
     * @param val
     */
    public String getMobile(String val){
        return CallPhoneUtil.checkNumber(val);
    }


    /**
     * 获取  切除手机号码的字符串
     * @param val
     */
    public String getNoMobileStr(String val){
        mobile=getMobile(val);
        if (!TextUtils.isEmpty(mobile)){
            StringBuffer stringBuffer=new StringBuffer();
            int p = result.indexOf(mobile);
            if (p>0){
                String r1 =  result.substring(0,p);
                String r2 =  result.substring(p+11,val.length());
                stringBuffer.append(TextUtils.isEmpty(r1) ? "":r1);
                stringBuffer.append(TextUtils.isEmpty(r1) ? "":r2);
            }
            return stringBuffer.toString();
        }
        return val ;
    }





    /*开始只能拆分省/市/区*/
    public void initCheckAddress(String val){
        /*只有名字和地址*/
        String addressAndName = getNoMobileStr(val);
        checkAddress(addressAndName);
    }



    /**
     * 获取地区
     *
     * @param addressAndName
     */
    private String p = "";
    private List<City> listC = null;
    private String c = "";
    private List<Area> listA = null;
    private String a = "" ;
    private String mobile  = "";

    public void reset(){
        p = "";
        listC = null;
        c = "";
        listA = null;
        a = "" ;
    }



    private void checkAddress(final String addressAndName) {
        reset();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (Province bean:list) {
                        String province = bean.getName();
                        if (addressAndName.contains(compareToStr(province))) {
                            p = province ;
                            listC = bean.getSubdb();
                            break;
                        }
                    }


                    if (TextUtils.isEmpty(p)){
                        for (Province bean:list) {
                            for (City city : bean.getSubdb()) {
                                String cityName = city.getName();
                                if (addressAndName.contains(compareToStr(cityName))) {
                                    c = cityName ;
                                    listA = city.getSubdb();
                                    break;
                                }
                            }
                        }
                    }else {
                        for (City city:listC) {
                            String cityName = city.getName();
                            if (addressAndName.contains(compareToStr(cityName))) {
                                c = cityName ;
                                listA = city.getSubdb();
                                break;
                            }
                        }
                    }


                    if (TextUtils.isEmpty(c)){
                        for (Province bean:list) {
                            for (City city:bean.getSubdb()) {
                                for (Area area:city.getSubdb()) {
                                    String cityName = area.getName();
                                    if (addressAndName.contains(compareToStr(cityName))) {
                                        a = cityName ;
                                        break;
                                    }
                                }
                            }
                        }
                    }else {
                        for (Area area:listA) {
                            String areaName= area.getName();
                            if (addressAndName.contains(compareToStr(areaName))) {
                                a = areaName ;
                                break;
                            }
                        }
                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }




                final AddressBean addressBean = new AddressBean();
                addressBean.setName(getName(addressAndName));

                /**
                 * 如果省份为空但是地区不为空
                 *
                 * 反向找出省市
                 */
                if (TextUtils.isEmpty(p)&&!TextUtils.isEmpty(a)){
                    for (AddressBean addressBean1:allDataList){
                        if (a.equals(addressBean1.getA())){
                            p = addressBean1.getP();
                            c=addressBean1.getC();
                            break;
                        }
                    }
                }


                /**
                 * 如果省份和地区为空 但是市区不为空
                 *
                 * 反向找出省市
                 */
                if (TextUtils.isEmpty(p) && TextUtils.isEmpty(a)&&!TextUtils.isEmpty(c)){
                    for (AddressBean addressBean2:allDataList){
                        if (c.equals(addressBean2.getC())){
                            p = addressBean2.getP();
                            break;
                        }
                    }
                }

                addressBean.setP(p);
                addressBean.setC(c);
                addressBean.setA(a);
                addressBean.setMobile(mobile);
                addressBean.setAddress(initAddress(addressAndName));


                /*回调*/
                onAddressResult(addressBean);
            }
        }).start();
    }


    /**
     * 获取详细地址
     * @param addressAndName
     * @return
     */
    private String initAddress(String addressAndName) {
        String address = "" ;
        if (!TextUtils.isEmpty(a)){
           address = iniAreaDetail(addressAndName);
        }else {
            if (!TextUtils.isEmpty(c)){
                address = iniCityDetail(addressAndName);
            }else {
                if (!TextUtils.isEmpty(p)){
                    address = iniProvinceDetail(addressAndName);
                }
            }
        }
        return address ;
    }


    /**
     * 如果没有省和城市
     * 获取
     * @param addressAndName
     * @return
     */
    private String iniProvinceDetail(String addressAndName) {
        String address = null;
        if (addressAndName.contains(p)){
            address =  addressAndName.substring(addressAndName.indexOf(c)+c.length(),addressAndName.length());
        }else {
            if (p.length()>2){
                for (int i = 2 ; i < p.length();i++){
                    if (!addressAndName.contains(p.substring(0,i+1))){
                        String temp = p.substring(0,i);
                        int position = addressAndName.indexOf(temp);
                        address =  addressAndName.substring(position+temp.length(),addressAndName.length());
                        break;
                    }
                }
            }
        }
        return address;
    }



    /**
     * 如果没有地区  获取市后的详细地址
     *
     * @param addressAndName
     * @return
     */
    private String iniCityDetail(String addressAndName) {
        String address = null;
        if (addressAndName.contains(c)){
            address =  addressAndName.substring(addressAndName.indexOf(c)+c.length(),addressAndName.length());
        }else {
            if (c.length()>2){
                for (int i = 2 ; i < c.length();i++){
                    if (!addressAndName.contains(c.substring(0,i+1))){
                        String temp = c.substring(0,i);
                        int position = addressAndName.indexOf(temp);
                        address =  addressAndName.substring(position+temp.length(),addressAndName.length());
                        break;
                    }
                }
            }
        }
        return address  ;
    }



    /**
     * 获取地区之后的详情地址
     * @param addressAndName
     */
    private String iniAreaDetail(String addressAndName) {
        String address = "" ;
        if (addressAndName.contains(a)){
            address =  addressAndName.substring(addressAndName.indexOf(a)+a.length(),addressAndName.length());
        }else {
            if (a.length()>2){
                for (int i = 2 ; i < a.length();i++){
                    if (!addressAndName.contains(a.substring(0,i+1))){
                        String temp = a.substring(0,i);
                        int position = addressAndName.indexOf(temp);
                        address =  addressAndName.substring(position+temp.length(),addressAndName.length());
                        break;
                    }
                }
            }
        }
        return address ;
    }



    /**
     * 拆分名字
     *
     * @param addressAndName
     */
    private String getName(String addressAndName) {
        String name = null;

        /*先判断省是否为空*/
        if (!TextUtils.isEmpty(p)){
            int position = addressAndName.indexOf(compareToStr(p));
            if (position>0){
                name = addressAndName.substring(0,position);
            }
        }


        if (TextUtils.isEmpty(p) && !TextUtils.isEmpty(c)){
            int position = addressAndName.indexOf(compareToStr(c));
            if (position>0){
                name = addressAndName.substring(0,position);
            }
        }


        if (TextUtils.isEmpty(p) && TextUtils.isEmpty(c) && !TextUtils.isEmpty(a)){
            int position = addressAndName.indexOf(compareToStr(a));
            if (position>0){
                name = addressAndName.substring(0,position);
            }
        }

        StringBuffer stringBuffer=new StringBuffer();
        stringBuffer.append("\n");
        stringBuffer.append("p="+p);
        stringBuffer.append("\n");
        stringBuffer.append("c="+c);
        stringBuffer.append("\n");
        stringBuffer.append("a="+a);
        return name;
    }






    /**
     * 获取比较的字符
     *
     * @param val
     * @return
     */
    public String compareToStr(String val){

        String result = "";
        if (!TextUtils.isEmpty(val)){
            result = val.substring(0,2) ;
        }
        return result;
    }




    public abstract void onAddressResult(AddressBean addressBean);


    public class AddressBean{

        String Mobile;
        String Name;
        String p;
        String c;
        String a;
        String address;

        public AddressBean(String mobile, String name, String p, String c, String a) {
            Mobile = mobile;
            Name = name;
            this.p = p;
            this.c = c;
            this.a = a;
        }

        public AddressBean(){}

        public void setAddress(String address) {
            this.address = address;
        }

        public String getAddress() {
            return address;
        }

        public String getMobile() {
            return Mobile;
        }

        public void setMobile(String mobile) {
            Mobile = mobile;
        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public String getP() {
            return p;
        }

        public void setP(String p) {
            this.p = p;
        }

        public String getC() {
            return c;
        }

        public void setC(String c) {
            this.c = c;
        }

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }
    }
}
