package com.test.socketdeal_single;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TestSocket {
    public static void main(String[] args) {
        int success = 0;
        for (int i = 0; i < 100; i++) {
            long time1 = System.currentTimeMillis();
            String host = "192.168.5.226";
            int port = 10001;
            String msg=getBytes3("11 04 0000 0002");
            String send = send(msg, host, port);
            if(send != null && send.length() > 0){
                System.out.println(++success + "----" + (System.currentTimeMillis() - time1) + "----" + send);
            }

        }
    }
    public static String getBytes3(String crc){
        byte[] bytes=new byte[6];
        bytes[0]=(byte) Integer.parseInt(crc.substring(0,2),16);
        bytes[1]=(byte) Integer.parseInt(crc.substring(3,5),16);
        bytes[2]=(byte) Integer.parseInt(crc.substring(6,8),16);
        bytes[3]=(byte) Integer.parseInt(crc.substring(8,10),16);
        bytes[4]=(byte) Integer.parseInt(crc.substring(11,13),16);
        bytes[5]=(byte) Integer.parseInt(crc.substring(13),16);

        String c4=getCRC(bytes);
        if(c4.length()<4){
            while (c4.length()<4){
                c4="0"+c4;
            }
        }

        return crc+" "+c4.substring(2)+c4.substring(0,2);
    }
    public static String send(String msg, String host,int port) {
        String strReturn = null;
        Socket clientSocket = new Socket();
        InputStream is = null;
        OutputStream os = null;
        PrintWriter pw = null;
        try {
            //1.建立客户端socket连接，指定服务器位置及端口

            clientSocket.connect(new InetSocketAddress(host, port), 2000);
            //将十六进制的字符串转换成字节数组
            byte[] cmdInfo = hexStrToBinaryStr(msg);
            //2.得到socket读写流

            os = clientSocket.getOutputStream();
            pw = new PrintWriter(os);
            //输入流
            is = clientSocket.getInputStream();
            //3.利用流按照一定的操作，对socket进行读写操作
            os.write(cmdInfo);
            os.flush();
            //接收服务器的响应
            int count = 0;
            while (is.available() < 9 && count< 50 ){
                count++;
                Thread.sleep(100);
            }
            byte[] buf = new byte[is.available()];
            //接收收到的数据
            Long ms=System.currentTimeMillis();
            while ((is.read(buf)) == -1 && System.currentTimeMillis()-ms <1600) {
                //将字节数组转换成十六进制的字符串
                Thread.sleep(500);
            }
            strReturn = BinaryToHexString(buf);
            //4.关闭资源

            pw.close();
            os.close();
        } catch (Exception e) {
            System.out.println(e.getMessage()+"||Socket连接超时。。。");
        }finally {
            if(pw != null){
                pw.close();
            }
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(os != null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(clientSocket != null){
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return strReturn;
    }
    public static byte[] hexStrToBinaryStr(String hexString) {
        if (null == hexString || hexString.trim().length() == 0) {
            return null;
        }
        hexString = hexString.replaceAll(" ", "");
        int len = hexString.length();
        int index = 0;
        byte[] bytes = new byte[len / 2];
        while (index < len) {
            String sub = hexString.substring(index, index + 2);
            bytes[index / 2] = (byte) Integer.parseInt(sub, 16);
            index += 2;
        }
        return bytes;
    }

    public static String getBytes2(String crc){
        byte[] bytes=new byte[crc.length()/2];
        for(int i=0;i<crc.length()/2;i++){
            bytes[i]=(byte) Integer.parseInt(crc.substring(i*2,i*2+2),16);
        }
        String c4=getCRC(bytes);
        if(c4.length()<4){
            while (c4.length()<4){
                c4="0"+c4;
            }
        }

        return crc+c4.substring(2)+c4.substring(0,2);
    }
    public static String BinaryToHexString(byte[] bytes) {
        String hexStr = "0123456789ABCDEF";
        String result = "";
        String hex = "";
        for (byte b : bytes) {
            hex = String.valueOf(hexStr.charAt((b & 0xF0) >> 4));
            hex += String.valueOf(hexStr.charAt(b & 0x0F));
            result += hex + " ";
        }
        return result;
    }
    public static String getCRC(byte[] bytes) {
        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;

        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        return Integer.toHexString(CRC);
    }

}
