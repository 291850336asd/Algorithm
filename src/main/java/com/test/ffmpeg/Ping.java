package com.test.ffmpeg;

import java.net.InetAddress;

public class Ping {

    public static void main(String[] args) throws Exception {
        System.out.println(ping("192.168.5.26"));
    }

    public  static  boolean ping(String ipAddress)  throws Exception {
        int  timeOut =  3000 ;   // 超时应该在3钞以上
        boolean status = InetAddress.getByName(ipAddress).isReachable(timeOut);      //  当返回值是true时，说明host是可用的，false则不可。
        return status;
    }
}
