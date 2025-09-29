package com.example.fastfoodsimulator.WebSocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    private final WebSocketService webSocketService;
    public WebSocketController(WebSocketService webSocketService){
        this.webSocketService=webSocketService;
    }
    public void customerCreation(String message) {
        webSocketService.customerCreation(message);
    }

    public void orderStartCreation(String message) {
        webSocketService.orderStartCreation(message);
    }

    public void orderEndCreation(String message) {
        webSocketService.orderEndCreation(message);
    }

    public void orderStartCooking(String message) {
        webSocketService.orderStartCooking(message);
    }

    public void orderEndCooking(String message) {
        webSocketService.orderEndCooking(message);
    }

    public void orderServing(String message) {
        webSocketService.orderServing(message);
    }

    public void kitchenError(String message) {
        webSocketService.kitchenError(message);
    }

    public void waiterError(String message) {
        webSocketService.waiterError(message);
    }
}
