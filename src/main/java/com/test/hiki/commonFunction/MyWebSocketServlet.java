package com.test.hiki.commonFunction;

import javax.servlet.annotation.WebServlet;
import javax.websocket.server.ServerEndpoint;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

@ServerEndpoint("/websocket")
public class MyWebSocketServlet extends WebSocketServlet{
    @Override
    public void configure(WebSocketServletFactory factory) {
        //factory.getPolicy().setIdleTimeout(10000);
        factory.register(MyWebSocketListener.class);
    }
}
