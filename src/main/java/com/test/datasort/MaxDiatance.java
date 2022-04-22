package com.test.datasort;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import javax.xml.soap.Node;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class MaxDiatance {


    public static class A{
        public byte[] gunserial= new byte[16];
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        JSONObject object = new JSONObject();
        object.put("gunserial","MTgwMDMwMDgAAAAAQAfnAQAAAACQhBAAAAAAAAAAAAA=");
        System.out.println(b2ss(object.getBytes("gunserial")));
        object.put("gunserial","MjAwMTE3MTIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=");
        System.out.println(b2ss(object.getBytes("gunserial")));
        object.put("gunserial","MTkwMDA0MDIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=");
        System.out.println(b2ss(object.getBytes("gunserial")));
        object.put("gunserial","MzAwMjg4OTkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=");
        System.out.println(b2ss(object.getBytes("gunserial")));
        object.put("gunserial","MDM4NzEyAAAAvw0AMMoaAwsAAAABAAMBAAAAAAAAAAA=");
        System.out.println(b2ss(object.getBytes("gunserial")));
        object.put("gunserial","Z7dwO0I7T2a/ua4GaFgIHQ==");
        System.out.println(bytesToHexString(object.getBytes("gunserial")));

    }

    public static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
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


//    public static Info process(Node x){
//        if(x == null){
//            return new Info(0, 0);
//        }
//    }


    public static class Info{
        public int maxDistance;
        public int height;

        public Info(int dis, int h){
            maxDistance = dis;
            height = h;
        }
    }

}
