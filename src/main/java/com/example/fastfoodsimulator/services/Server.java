package com.example.fastfoodsimulator.services;

import com.example.fastfoodsimulator.models.OrderTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Server implements Runnable {
    @Autowired
    private Kitchen kitchen;
    private boolean running;
    public Server(){}
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
                OrderTicket orderTicket = kitchen.getReadyQueue().take();
                orderTicket.getServerPromise().thenAccept(voidResult -> {
                    orderTicket.getCustomerPromise().complete(null);
                });
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
                System.out.println(e.getMessage());
            }
        }
    }
}
