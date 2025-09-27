package com.example.fastfoodsimulator.models;

import com.example.fastfoodsimulator.models.Order;

import java.util.concurrent.CompletableFuture;

public class OrderTicket {
    private Order order;
    private final CompletableFuture<Void> serverPromise = new CompletableFuture<>();
    private final CompletableFuture<Void> customerPromise = new CompletableFuture<>();

    public OrderTicket(Order order){
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }

    public CompletableFuture<Void> getServerPromise() {
        return serverPromise;
    }

    public CompletableFuture<Void> getCustomerPromise() {
        return customerPromise;
    }
}
