package com.test;

import com.alibaba.fastjson.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Message {
    public static void main(String[] args) {
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("tel", "18600455182");
        jsonObject1.put("text", "skdskdwwoxjcjxoere");
        jsonObject1.put("type", 1);
        System.out.println(jsonObject1.toJSONString());
        setUdpSocket("127.0.0.1", 5101, jsonObject1.toJSONString());
    }

    public static String setUdpSocket(String ip, int port, String msg) {
        String responseMsg = "";
        DatagramSocket client = null;
        try {
            client = new DatagramSocket();
            byte[] sendBytes = msg.getBytes();
            byte[] byteArray = new byte[800];
            byte[] tmpByteArray = strToByteArray(msg);
            System.arraycopy(tmpByteArray, 0, byteArray, 0, tmpByteArray.length);
            InetAddress address = InetAddress.getByName(ip);
            DatagramPacket sendPacket = new DatagramPacket(byteArray, byteArray.length, address, port);

            try {
                client.send(sendPacket);
            } catch (Exception var15) {
                var15.printStackTrace();
            }

            byte[] responseBytes = new byte[1024];
            new DatagramPacket(responseBytes, responseBytes.length);
        } catch (Exception var16) {
            var16.printStackTrace();
        } finally {
            if (client != null) {
                client.close();
                client = null;
            }

        }

        return responseMsg;
    }

    public static byte[] strToByteArray(String str) {
        if (str == null) {
            return null;
        } else {
            byte[] byteArray = new byte[0];

            try {
                byteArray = str.getBytes("GBK");
            } catch (UnsupportedEncodingException var3) {
                var3.printStackTrace();
            }

            return byteArray;
        }
    }

}
