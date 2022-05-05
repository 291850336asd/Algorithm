package com.test;

public class TestA {


    public static void main(String[] args) {
        String a = "ffmpeg  -rtsp_transport tcp -i \"rtsp://admin:1q2w3e4r@192.168.5.20:554/Streaming/tracks/601?starttime=20220427t170000z&endtime=20220427t170130z\" -c copy -f flv  \"rtmp://192.168.5.46/live/hsdsdsdw\"  31292      312500";

        a= a.replaceAll("  ", " ")
                .replaceAll("  ", " ")
                .replaceAll("  ", " ")
                .replaceAll("  ", " ");
        a = a.substring(0, a.length()-1);
        System.out.println(a);


    }
}
