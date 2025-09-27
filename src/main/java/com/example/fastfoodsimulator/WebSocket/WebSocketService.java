package com.example.fastfoodsimulator.WebSocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {
    private final SimpMessagingTemplate template;

    public WebSocketService(SimpMessagingTemplate template){
        this.template=template;
    }
    public void sendNotification(String message) {
        template.convertAndSend("/frontend/notifications", message);
    }
}
