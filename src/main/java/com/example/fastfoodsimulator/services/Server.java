package com.example.fastfoodsimulator.services;

import com.example.fastfoodsimulator.models.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class Server implements Runnable {
    @Autowired
    private WaiterService waiterService;
    private final BlockingQueue<Customer> customerQueue = new LinkedBlockingQueue<>();
    public void clearCustomerQueue(){
        customerQueue.clear();
    }
    private boolean running;
    public Server(){}
    public void stop(){
        running = false;
        clearCustomerQueue();
    }
    public void start(){
        running = true;
    }
    @Override
    public void run(){
        while (running) {
            try{
                Customer customer = waiterService.getWaitingQueue().take();
                customerQueue.put(customer);
                customer.getOrderTicket().getServerPromise().thenAccept(voidResult -> {
                    try {
                        customerQueue.take();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    customer.getOrderTicket().getCustomerPromise().complete(null);
                });
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
                System.out.println(e.getMessage());
            }
        }
    }
}
