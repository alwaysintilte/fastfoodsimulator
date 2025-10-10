package com.example.fastfoodsimulator.models;

import com.example.fastfoodsimulator.services.WaiterService;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

import static com.example.fastfoodsimulator.randomGenerators.DishGenerator.getRandomDish;

public class Waiter {
    private Integer counter;
    private Integer interval;
    public Waiter(){}
    public CompletableFuture<OrderTicket> takeOrderAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(interval);
                return takeOrder();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
    public void setCounter(Integer counter){
        this.counter = counter;
    }
    public void setInterval(Integer interval){
        this.interval = interval;
    }
    public OrderTicket takeOrder(){
        Order order = createOrder();
        return new OrderTicket(order);
    }
    public Order createOrder(){
        Order order = new Order(this.counter, getRandomDish());
        this.counter = this.counter + 1;
        return order;
    }
}
