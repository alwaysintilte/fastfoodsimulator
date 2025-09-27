package com.example.fastfoodsimulator.services;

import com.example.fastfoodsimulator.models.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class CustomerGenerator implements Runnable {
    @Autowired
    private OrderTaker orderTaker;
    private final BlockingQueue<Customer> customerQueue = new LinkedBlockingQueue<>();
    private Integer interval;
    private boolean running;
    public CustomerGenerator(){
        this.interval = 2000;
    }
    public void setInterval(Integer interval){
        this.interval = interval;
    }
    public void stop() {
        running=false;
    }
    public void start() {
        running=true;
    }
    @Override
    public void run(){
        while (running) {
            try {
                Customer customer = new Customer();
                customerQueue.put(customer);
                customer.setOrderTicket(orderTaker.takeOrder());
                System.out.println(customer.getName()+" "+customer.getOrderTicket().getOrder().getOrderItem()+" начат");
                customer.getOrderTicket().getCustomerPromise().thenAccept(voidResult -> {
                    System.out.println(customer.getName()+" "+customer.getOrderTicket().getOrder().getOrderItem()+" завершен");
                });

                Thread.sleep(interval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(e.getMessage());
            }
        }
    }
}
