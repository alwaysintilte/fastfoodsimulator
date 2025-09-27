package com.example.fastfoodsimulator.services;

import com.example.fastfoodsimulator.models.OrderTicket;
import com.example.fastfoodsimulator.services.OrderTaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class Kitchen implements Runnable {
    @Autowired
    private OrderTaker orderTaker;
    private final BlockingQueue<OrderTicket> readyQueue = new LinkedBlockingQueue<>();
    private Integer interval;
    private boolean running;
    public Kitchen(){
        this.interval = 2000;
    }
    public void setInterval(Integer interval){
        this.interval = interval;
    }
    public BlockingQueue<OrderTicket> getReadyQueue() {
        return readyQueue;
    }
    public void stop(){
        running = false;
    }
    public void start(){
        running = true;
    }
    @Override
    public void run(){
        while (running) {
            try{
                OrderTicket orderTicket = orderTaker.getOrderQueue().take();
                Thread.sleep(interval);
                readyQueue.put(orderTicket);
                orderTicket.getServerPromise().complete(null);
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
                System.out.println(e.getMessage());
            }
        }
    }
}
