package com.test;

import cn.hutool.core.date.CalendarUtil;
import com.alibaba.fastjson.JSON;
import sun.reflect.Reflection;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TestA {
    public static int b= 1;
    public static class A{
        public static final String hello = "sdsdsdsd";
        public static final   A a = new A();
        private byte[] aa;

        public byte[] getAa() {
            return aa;
        }

        public void setAa(byte[] aa) {
            this.aa = aa;
        }

        static {
            System.out.println("bbbbbbbbb");
        }
    }

    public static int test(){
        return b;
    }


    public static void main(String[] args) throws ClassNotFoundException {
//        Calendar instance = Calendar.getInstance();
//        // 操作时间
//        DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        instance.setTime(new Date(1679702209 * 1000L));
//        instance.set(Calendar.HOUR_OF_DAY, instance.get(Calendar.HOUR_OF_DAY) - 8);
//        String format = format1.format(instance.getTime());
//        System.out.println(format);

//        System.out.println(A.hello);
//        test();
//        Class.forName(A.class.getName());
//        A a = JSON.parseObject("{\"aa\":\"MTIzNDU2AAA4Ywh4fwAAAAAAAAAAAAAAAAAAAAAAAAA=\"}", A.class);
//        A a = JSON.parseObject("{\"aa\":\"MDQyOTE4AA4YAH4fwAAAAAAAAAAAAAAAAAAAAEAAAA=\"}", A.class);
        A a = JSON.parseObject("{\"aa\":\"MDIxNzkzAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=\"}", A.class);
//        A a = JSON.parseObject("{\"aa\":\"r0alcAARsk0ourjUuw90fA=\"}", A.class);
//        A a = JSON.parseObject("{\"aa\":\"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=\"}", A.class);
        System.out.println(b2ss(a.getAa()));
        System.out.println(b2c(a.getAa()));
//        HashMap<String, String> hashMap = new HashMap<>();
//        hashMap.put("ss","sdsd121");
//        System.out.println(JSON.toJSONString(hashMap));


//        String a = "ffmpeg  -rtsp_transport tcp -i \"rtsp://admin:1q2w3e4r@192.168.5.20:554/Streaming/tracks/601?starttime=20220427t170000z&endtime=20220427t170130z\" -c copy -f flv  \"rtmp://192.168.5.46/live/hsdsdsdw\"  31292      312500";
//
//        a= a.replaceAll("  ", " ")
//                .replaceAll("  ", " ")
//                .replaceAll("  ", " ")
//                .replaceAll("  ", " ");
//        a = a.substring(0, a.length()-1);
//        System.out.println(a);
//        DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date date1 = new Date(new Long(1659981061) * 1000);
//        Calendar c1 = Calendar.getInstance();
//        c1.setTime(date1);
//        c1.set(Calendar.HOUR_OF_DAY, c1.get(Calendar.HOUR_OF_DAY) - 8);
//        String backTime = format1.format(c1.getTime());
//
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        try {
//            Date endTime = simpleDateFormat.parse(backTime);
//            System.out.println(endTime);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date date = null;
//        try
//        {
//            date = format.parse("2022-07-18T09:35:30.000Z".replace("T"," ").substring(0,19));
//        } catch (ParseException e)
//        {
//            e.printStackTrace();
//        }
    }
    public static String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2) {
                sb.append(0);
            }
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    public static String b2c(byte b[]) {
        int len = 0;
        while (b.length>len&&b[len] != 0) {
            len++;
        }

        try {
            return new String(b, 0, len,"utf-8").trim();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static byte[] strToByteArray(String str) {
        if (str == null) {
            return null;
        }
        byte[] byteArray = new byte[0];
        try {
            byteArray = str.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return byteArray;
    }


    private static String b2ss(byte b[]) {
        int len = 0;
        while (b.length>len&&b[len] != 0)
            len++;

        try {
            return new String(b, 0, len,"gbk").trim();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }


}
