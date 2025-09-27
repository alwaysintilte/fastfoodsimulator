package com.example.fastfoodsimulator.services;

import com.example.fastfoodsimulator.models.Order;
import com.example.fastfoodsimulator.models.OrderTicket;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.example.fastfoodsimulator.randomGenerators.DishGenerator.getRandomDish;

@Service
public class OrderTaker {
    private Integer orderCounter = 0;
    private final BlockingQueue<OrderTicket> orderQueue = new LinkedBlockingQueue<>();
    public OrderTicket takeOrder(){
        Order order = createOrder();
        OrderTicket orderTicket = new OrderTicket(order);
        try {
            orderQueue.put(orderTicket);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(e.getMessage());
        }
        return orderTicket;
    }
    public Order createOrder(){
        Order order = new Order(orderCounter, getRandomDish());
        orderCounter = orderCounter + 1;
        return order;
    }

    public BlockingQueue<OrderTicket> getOrderQueue() {
        return orderQueue;
    }
}
