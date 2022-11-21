package com.test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class A {
    static List<Socket> clientSocket = new CopyOnWriteArrayList<>();
    public static void main(String[] args) {
        ServerSocket server = null;
        while (true){
            try {
                if(server == null){
                    server = new ServerSocket(8888);
                }
                Socket socket = server.accept();
                clientSocket.add(socket);


                send();
                new Thread(()->{
                    try {
                        InputStream is = socket.getInputStream();
                        int line = 0;
                        //接收收到的数据
                        byte[] buf = null;
                        while (true){
                            if(is.available() > 0){
                                buf = new byte[is.available()];
                                while ((line = is.read(buf)) != -1 ) {
                                    //将字节数组转换成十六进制的字符串
                                    System.out.println(new String(buf, "utf-8"));
                                    Thread.yield();
                                }
                            }
                            Thread.yield();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }).start();
            } catch (IOException e) {
                try {
                    if (server != null){
                        server.close();
                    }
                    Thread.sleep(1000);
                }catch (Exception e1){
                } finally {
                    server = null;
                }
            }
        }
    }

    private static void send(){
        Iterator<Socket> iterator = clientSocket.iterator();
        System.out.println(clientSocket.size());
        while (iterator.hasNext()){
            Socket client = iterator.next();
            if(client != null && client.isConnected() && !client.isOutputShutdown()){
                try {
                    BufferedWriter writer;
                    String msg;
                    writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                    msg = "AT+STACH0=1,30\r\n";
                    //发送数据
                    writer.write(msg);
                    writer.flush();
                }catch (Exception e){
                    try {
                        if(client!= null){
                            client.shutdownOutput();
                            client.close();
                        }
                        clientSocket.remove(client);
                    }catch (Exception e1){
                       e.printStackTrace();
                    }
                }
            } else {
                try {
                    if(client!= null){
                        client.shutdownOutput();
                        client.close();
                    }
                    clientSocket.remove(client);
                }catch (Exception e){

                }
            }
        }
    }
}
