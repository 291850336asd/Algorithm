package com.test;

import cn.hutool.core.date.CalendarUtil;
import com.alibaba.fastjson.JSON;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class TestA {


    public static void main(String[] args) {
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

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try
        {
            date = format.parse("2022-07-18T09:35:30.000Z".replace("T"," ").substring(0,19));
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
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
