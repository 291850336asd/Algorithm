package com.test;

import com.alibaba.fastjson.JSON;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Test {

    public static void main(String[] args) {
//        aa();
//        System.out.println("----------------");
//        bb();
//        System.out.println(UUID.randomUUID().toString().replace("-",""));
//        List<String> allHistoryDeviceCmdStream = getAllHistoryDeviceCmdStream();
//        System.out.println(allHistoryDeviceCmdStream.size());
//
//        String s = "ffmpeg -rtsp_transport tcp -stream_loop -1 -i \"rtsp://admin:1q2w3e4r@192.168.5.20:554/Streaming/tracks/501?starttime=20220406t122045z&endtime=20220406t122130z\" -c:a copy -f flv \"rtmp://127.0.0.1/live/hisstream11111_dsds\" 42632";
//        String[] s1 = s.split(" ");
//        System.out.println(s1[s1.length-2].replaceAll("\"","").split("_")[1]);

//        AA a = new AA();
//        a.setAa("{id:\"你你好\"}".getBytes(StandardCharsets.ISO_8859_1));
//
//        System.out.println(JSON.toJSONString(a));
//        System.out.println(System.currentTimeMillis()/1000 + 120);
//        System.out.println(gg());


//        try {
//            Runtime.getRuntime().exec("D:\\ffmpeg-master-latest-win64-gpl\\nircmd\\nircmd.exe elevate ffmpeg -rtsp_transport tcp -i rtsp://admin:1q2w3e4r@192.168.5.20:554/Streaming/tracks/601?starttime=20220406t222222z -c:a copy -f flv rtmp://127.0.0.1/live/mystrea11");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        System.out.println(toBinary(10));
        System.out.println(toBinary(16));
//        byte[] rootType = new byte[18];
//        int itemRole =  Integer.parseInt("40") -1;
//        extracted(rootType, 1);
    }

    public static String toBinary(int num) {
        String str = "";
        while (num != 0) {
            str = (num&0xff) % 2 + str;
            num = (num&0xff)  / 2;
        }
        return str;
    }

    private static void extracted(byte[] rootType, int itemRole) {
        if (itemRole < 4) {
            for (int i = 0; i < 4; i++) {
                if (i == itemRole) {
                    rootType[0] |= (0x01 << i);
                }
            }
        } else {
            for (int i = 4; i < 42; i++) {
                if (i == itemRole) {
                    int index = ((i - 4) / 8) + 1;
                    int iBitNum = (i - 4) % 8;
                    rootType[index] |= (0x01 << iBitNum);
                }
            }
        }
    }


//    static public class AA {
//        private byte[] aa;
//
//        public byte[] getAa() {
//            return aa;
//        }
//
//        public void setAa(byte[] aa) {
//            this.aa = aa;
//        }
//    }



//
//    /**
//     * 获取所有正在运行的实时视频流
//     * @return
//     */
//    private static List<String> getAllHistoryDeviceCmdStream(){
//        try {
//            Process process = Runtime.getRuntime().exec("wmic process where caption=\"ffmpeg.exe\" get ProcessId, CommandLine");
//            int status = process.waitFor();
//            System.out.println(status);
//            InputStream in = process.getInputStream();
//            BufferedReader br = new BufferedReader(new InputStreamReader(in));
//            List<String> aaCmdStreams = new ArrayList<>();
//            String line = null;
//            do{
//                line = br.readLine();
//                if(null != line){
//                    if(line.contains("ffmpeg") && line.contains("live/hisstream")){
//                        String s = line.replaceAll("  ", " ")
//                                .replaceAll("  ", " ")
//                                .replaceAll("  ", " ")
//                                .replaceAll("  ", " ");
//                        aaCmdStreams.add(s.substring(0, s.length()-1));
//                    }
//                }
//            } while(line!=null);
//            return aaCmdStreams;
//        }catch (Exception e){
//        }
//        return null;
//    }
//
//    public static String b2s(byte b[]) {
//        int len = 0;
//        while (b.length>len&&b[len] != 0) {
//            len++;
//        }
//        try {
//            return new String(b ,"gb2312").trim();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        return "转码失败";
//    }
//
//
//    public static void bb(){
//        Lock lock =new ReentrantLock();
//
//        for (int i = 0; i < 5; i++) {
//            new Thread(()->{
//                if(lock.tryLock()){
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    System.out.println("---");
//                }else {
//                    System.out.println("1111");
//                }
//            }).start();
//        }
//    }
//
//
//    public static void aa(){
//        Lock lock =new ReentrantLock();
//
//        for (int i = 0; i < 5; i++) {
//            if(lock.tryLock()){
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                System.out.println("---");
//            }else {
//                System.out.println("1111");
//            }
//        }
//    }
}
