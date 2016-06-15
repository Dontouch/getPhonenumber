package com.example.dontouch.getphonenumber.util;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


/**
 * Created by Dontouch on 16/6/15.
 */
public  class getPhoneLocation {

        /**
         * 测试手机号码是来自哪个城市的，利用淘宝的API
         * @param mobileNumber 手机号码
         * @return
         * @throws MalformedURLException
         */
        public static String calcMobileCity(String mobileNumber) throws MalformedURLException{
            String jsonString = null;
            JSONArray array = null;
            JSONObject jsonObject = null;
            String urlString = "http://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel=" + mobileNumber;
            StringBuffer sb = new StringBuffer();
            BufferedReader buffer;
            URL url = new URL(urlString);
            try{
                InputStream in = url.openStream();

                // 解决乱码问题
                buffer = new BufferedReader(new InputStreamReader(in,"gb2312"));
                String line = null;
                while((line = buffer.readLine()) != null){
                    sb.append(line);
                }
                in.close();
                buffer.close();
                // System.out.println(sb.toString());
                jsonString = sb.toString();
                // 替换掉“__GetZoneResult_ = ”，让它能转换为JSONArray对象
                jsonString = jsonString.replaceAll("^[__]\\w{14}+[_ = ]+", "[");
                // System.out.println(jsonString+"]");
                String jsonString2 = jsonString + "]";
                // 把STRING转化为json对象

                array = JSONArray.fromObject(jsonString2);

                // 获取JSONArray的JSONObject对象，便于读取array里的键值对
                jsonObject = array.getJSONObject(0);

            }catch(Exception e){
                e.printStackTrace();
            }
            return jsonObject.getString("province");
        }

        /**
         * 计算多个号码的归属地
         * @param mobileNumbers 号码列表
         * @return
         * @throws MalformedURLException
         */
        public static JSONObject calcMobilesCities(List<String> mobileNumbers) throws MalformedURLException{
            JSONObject jsonNumberCity = new JSONObject();
            for(String mobileNumber : mobileNumbers){
                jsonNumberCity.put(mobileNumber, calcMobileCity(mobileNumber));            ;
            }
            return jsonNumberCity;
        }
}





