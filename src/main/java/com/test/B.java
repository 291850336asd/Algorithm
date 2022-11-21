package com.test;

import java.io.*;
import java.net.Socket;

public class B {

    public static void main(String[] args) throws IOException, InterruptedException {
        Socket clientSocket = new Socket("127.0.0.1", 8888);


        //输入流
        InputStream inputStream = clientSocket.getInputStream();//得到一个输入流，用于接收服务器响应的数据

        String info = null;

        //3.利用流按照一定的操作，对socket进行读写操作
        //接收服务器的响应
        byte[] line = new byte[1024*1024];
        //接收收到的数据
        while (true){
            if ((inputStream.read(line) > -1) ) {
                //将字节数组转换成十六进制的字符串
                System.out.println(new String(line));
            }
            Thread.sleep(1000);
        }
        //4.关闭资源
    }
}
