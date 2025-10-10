package com.example.fastfoodsimulator.models;

import static com.example.fastfoodsimulator.randomGenerators.DishGenerator.getRandomDish;

public class Waiter {
    private Integer counter;
    private Integer interval;
    public Waiter(){}
    public void processOrder() {
        try {
            Thread.sleep(interval);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
