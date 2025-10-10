package com.example.fastfoodsimulator.services;

import com.example.fastfoodsimulator.models.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class Server{
    private ExecutorService executorService;
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
        executorService.shutdownNow();
        clearCustomerQueue();
    }
    public void start(){
        running = true;
        executorService = Executors.newFixedThreadPool(1);
        executorService.submit(() -> runServer());
    }
    public void runServer(){
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
