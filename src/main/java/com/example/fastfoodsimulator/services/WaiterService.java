package com.example.fastfoodsimulator.services;

import com.example.fastfoodsimulator.WebSocket.WebSocketService;
import com.example.fastfoodsimulator.models.Customer;
import com.example.fastfoodsimulator.models.OrderTicket;
import com.example.fastfoodsimulator.models.Waiter;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class WaiterService implements Runnable {
    private Waiter waiter;
    @Autowired
    private CustomerGenerator customerGenerator;
    @Autowired
    private WebSocketService webSocketService;
    private Integer orderCounter = 0;
    private final BlockingQueue<OrderTicket> orderQueue = new LinkedBlockingQueue<>();
    private Integer interval;
    private boolean running;
    public WaiterService(){
        this.interval = 3000;
    }
    public BlockingQueue<OrderTicket> getOrderQueue() {
        return orderQueue;
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

    @PostConstruct
    public void init() {
        this.waiter = new Waiter(interval, orderCounter);
    }
    @Override
    public void run(){
        while (running) {
            try {
                Customer customer = customerGenerator.getCustomerQueue().take();
                customer.setOrderTicket(waiter.takeOrder());
                webSocketService.orderStartCreation(customer.getName()+" начал делать заказ: "+customer.getOrderTicket().getOrder().getOrderItem()+". Айди заказа: "+customer.getOrderTicket().getOrder().getOrderId());
                waiter.completeOrder();
                webSocketService.orderEndCreation(customer.getName()+" закончил делать заказ: "+customer.getOrderTicket().getOrder().getOrderItem()+". Заказ начат. Айди заказа: "+customer.getOrderTicket().getOrder().getOrderId());
                orderQueue.put(customer.getOrderTicket());
                customer.getOrderTicket().getCustomerPromise().thenAccept(voidResult -> {
                    webSocketService.orderServing(customer.getName()+" получил заказ: "+customer.getOrderTicket().getOrder().getOrderItem()+". Заказ зваершен. Айди заказа: "+customer.getOrderTicket().getOrder().getOrderId());
                    System.out.println(customer.getName()+" получил заказ: "+customer.getOrderTicket().getOrder().getOrderItem()+". Заказ зваершен. Айди заказа: "+customer.getOrderTicket().getOrder().getOrderId());
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(e.getMessage());
            }
        }
    }
}
