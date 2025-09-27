package com.example.fastfoodsimulator.models;

import static com.example.fastfoodsimulator.randomGenerators.NameGenerator.getRandomName;

public class Customer {
    private String name;
    private OrderTicket orderTicket;
    public Customer(){
        this.name = getRandomName();
    }

    public String getName() {
        return name;
    }

    public OrderTicket getOrderTicket() {
        return orderTicket;
    }

    public void setOrderTicket(OrderTicket orderTicket) {
        this.orderTicket = orderTicket;
    }
}
