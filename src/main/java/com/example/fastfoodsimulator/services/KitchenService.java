package com.example.fastfoodsimulator.services;

import com.example.fastfoodsimulator.WebSocket.WebSocketService;
import com.example.fastfoodsimulator.models.Cook;
import com.example.fastfoodsimulator.models.OrderTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class KitchenService {
    private List<Cook> cooks = new ArrayList<>();
    private ExecutorService executorService;
    @Autowired
    private WaiterService waiterService;
    @Autowired
    private WebSocketService webSocketService;
    private final BlockingQueue<OrderTicket> readyQueue = new LinkedBlockingQueue<>();
    private Integer interval;
    private Integer cookCount = 1;
    private boolean running;
    public KitchenService(){}
    public void setInterval(Integer interval){
        this.interval = interval;
    }
    public void setCookCount(Integer cookCount){
        this.cookCount = cookCount;
    }
    public BlockingQueue<OrderTicket> getReadyQueue() {
        return readyQueue;
    }
    public void clearReadyQueue() {
        readyQueue.clear();
    }
    public void stop(){
        running = false;
        executorService.shutdownNow();
        clearReadyQueue();
    }
    public void start(){
        running = true;
        executorService = Executors.newFixedThreadPool(cookCount);
        cooks.clear();
        for (int i = 0; i < cookCount; i++) {
            Cook cook = new Cook();
            cook.setInterval(interval);
            cooks.add(cook);
            executorService.submit(() -> runCook(cook));
        }
    }
    public void runCook(Cook cook){
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
