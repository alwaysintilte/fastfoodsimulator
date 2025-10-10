package com.example.fastfoodsimulator.services;

import com.example.fastfoodsimulator.WebSocket.WebSocketService;
import com.example.fastfoodsimulator.models.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class CustomerGenerator{
    private ExecutorService executorService;
    @Autowired
    private WebSocketService webSocketService;
    private final BlockingQueue<Customer> customerQueue = new LinkedBlockingQueue<>();
    private Integer interval;
    private boolean running;
    public CustomerGenerator(){}
    public BlockingQueue<Customer> getCustomerQueue() {
        return customerQueue;
    }
    public void clearCustomerQueue() {
        customerQueue.clear();
    }
    public void setInterval(Integer interval){
        this.interval = interval;
    }
    public void stop() {
        running=false;
        executorService.shutdownNow();
        clearCustomerQueue();
    }
    public void start() {
        running=true;
        executorService = Executors.newFixedThreadPool(1);
        executorService.submit(() -> runGenerator());
    }
    public void runGenerator(){
        while (running) {
            try {
                Customer customer = new Customer();
                webSocketService.customerCreation(customer.getName()+" пришел сделать заказ");
                customerQueue.put(customer);
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(e.getMessage());
            }
        }
    }
}
