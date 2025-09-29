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
    public void customerCreation(String message) {
        template.convertAndSend("/frontend/customercreation", message);
    }
    public void orderStartCreation(String message) {
        template.convertAndSend("/frontend/startcreation", message);
    }
    public void orderEndCreation(String message) {
        template.convertAndSend("/frontend/endcreation", message);
    }
    public void orderStartCooking(String message) {
        template.convertAndSend("/frontend/startcooking", message);
    }
    public void orderEndCooking(String message) {
        template.convertAndSend("/frontend/endcooking", message);
    }
    public void orderServing(String message) {
        template.convertAndSend("/frontend/serving", message);
    }
    public void kitchenError(String message) {
        template.convertAndSend("/frontend/kitchen", message);
    }
    public void waiterError(String message) {
        template.convertAndSend("/frontend/waiter", message);
    }
}
