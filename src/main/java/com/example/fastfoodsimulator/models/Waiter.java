package com.example.fastfoodsimulator.models;

import com.example.fastfoodsimulator.services.WaiterService;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.example.fastfoodsimulator.randomGenerators.DishGenerator.getRandomDish;

public class Waiter {
    private  Integer interval;
    private Integer counter;
    public Waiter(){}
    public Waiter(Integer interval, Integer counter){
        this.interval = interval;
        this.counter = counter;
    }
    public OrderTicket takeOrder(){
        Order order = createOrder();
        return new OrderTicket(order);
    }
    public void completeOrder(){
        try {
            Thread.sleep(interval);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(e.getMessage());
        }
    }
    public Order createOrder(){
        Order order = new Order(this.counter, getRandomDish());
        this.counter = this.counter + 1;
        return order;
    }
}
