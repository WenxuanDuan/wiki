package com.wenxuanduan.wiki.websocket;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;

@Component
@ServerEndpoint("/ws/{token}")
public class WebSocketServer {
    private static final Logger LOG = LoggerFactory.getLogger(WebSocketServer.class);

    /**
     * one token per client
     */
    private String token = "";

    private static HashMap<String, Session> map = new HashMap<>();

    /**
     * connected successfully
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        map.put(token, session);
        this.token = token;
        LOG.info("New connection：token：{}，session id：{}，Current connections：{}", token, session.getId(), map.size());
    }

    /**
     * connection closed
     */
    @OnClose
    public void onClose(Session session) {
        map.remove(this.token);
        LOG.info("Connection closed，token：{}，session id：{}！Current connections：{}", this.token, session.getId(), map.size());
    }

    /**
     * receive message
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        LOG.info("Received message：{}，content：{}", token, message);
    }

    /**
     * connection error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        LOG.error("ERROR", error);
    }

    /**
     * send group message
     */
    public void sendInfo(String message) {
        for (String token : map.keySet()) {
            Session session = map.get(token);
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                LOG.error("Send message failed：{}，content：{}", token, message);
            }
            LOG.info("send message：{}，content：{}", token, message);
        }
    }

}