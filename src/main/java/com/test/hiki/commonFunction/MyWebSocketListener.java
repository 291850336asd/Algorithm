package com.test.hiki.commonFunction;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;

public class MyWebSocketListener implements WebSocketListener {

    public static Session m_session=null;
    
    public static MyWebSocketListener myWebSocketListener;
    @PostConstruct
    public void init() {
        myWebSocketListener = this;
    }
    
    @Override
    public void onWebSocketConnect(Session session) {
        //System.out.println("onWebSocketConnect->"+session.getRemoteAddress());
        m_session=session;
    }
    
    //send String
    @Override
    public void onWebSocketText(String message) {
        System.out.println("onWebSocketText"+message);
    }
    
    //send byte[]
    @Override
    public void onWebSocketBinary(byte[] payload, int offset, int len) {
        System.out.println("onWebSocketBinary");
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        System.out.println("Error->" + cause.getMessage());
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        //System.out.println("onWebSocketClose");
        m_session=null;
    }
    
    public static void sendString(String message) {
        try {
            m_session.getRemote().sendString(message);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
