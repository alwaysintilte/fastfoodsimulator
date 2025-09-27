package com.example.fastfoodsimulator.WebSocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    private final WebSocketService webSocketService;

    public WebSocketController(WebSocketService webSocketService){
        this.webSocketService=webSocketService;
    }

    @MessageMapping("/send")
    public void handleMessage(String message) {
        webSocketService.sendNotification(message);
    }
}
