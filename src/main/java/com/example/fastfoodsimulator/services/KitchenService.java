package com.example.fastfoodsimulator.services;

import com.example.fastfoodsimulator.WebSocket.WebSocketService;
import com.example.fastfoodsimulator.models.Cook;
import com.example.fastfoodsimulator.models.OrderTicket;
import com.example.fastfoodsimulator.models.Waiter;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class KitchenService implements Runnable {
    private Cook cook;
    @Autowired
    private WaiterService waiterService;
    @Autowired
    private WebSocketService webSocketService;
    private final BlockingQueue<OrderTicket> readyQueue = new LinkedBlockingQueue<>();
    private Integer interval;
    private boolean running;
    public KitchenService(){}
    public void setInterval(Integer interval){
        this.interval = interval;
    }
    public BlockingQueue<OrderTicket> getReadyQueue() {
        return readyQueue;
    }
    public void clearReadyQueue() {
        readyQueue.clear();
    }
    public void stop(){
        clearReadyQueue();
        running = false;

    }
    public void start(){
        running = true;
        cook.setInterval(interval);
    }
    @PostConstruct
    public void init() {
        this.cook = new Cook();
    }
    @Override
    public void run(){
        while (running) {
            try{
                OrderTicket orderTicket = waiterService.getOrderQueue().take().getOrderTicket();
                webSocketService.orderStartCooking("Повар начал готовить: "+orderTicket.getOrder().getOrderItem()+". Айди заказа: "+orderTicket.getOrder().getOrderId());
                cook.cookOrder();
                webSocketService.orderEndCooking("Повар закончил готовить: "+orderTicket.getOrder().getOrderItem()+". Айди заказа: "+orderTicket.getOrder().getOrderId());
                readyQueue.put(orderTicket);
                orderTicket.getServerPromise().complete(null);
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
                System.out.println(e.getMessage());
            }
        }
    }
}
