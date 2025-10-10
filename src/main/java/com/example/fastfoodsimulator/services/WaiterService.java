package com.example.fastfoodsimulator.services;

import com.example.fastfoodsimulator.WebSocket.WebSocketService;
import com.example.fastfoodsimulator.models.Customer;
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
    private final BlockingQueue<Customer> orderQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<Customer> waitingQueue = new LinkedBlockingQueue<>();
    private Integer interval;
    private boolean running;
    public WaiterService(){}
    public BlockingQueue<Customer> getOrderQueue() {
        return orderQueue;
    }
    public BlockingQueue<Customer> getWaitingQueue() {
        return waitingQueue;
    }
    public void clearOrderQueue() {
        orderQueue.clear();
    }
    public void clearWaitingQueue() {
        waitingQueue.clear();
    }
    public void setInterval(Integer interval){
        this.interval = interval;
    }
    public void stop() {
        running = false;
        clearOrderQueue();
        clearWaitingQueue();
    }
    public void start() {
        running=true;
        orderCounter=0;
        waiter.setCounter(orderCounter);
        waiter.setInterval(interval);
    }

    @PostConstruct
    public void init() {
        this.waiter = new Waiter();
    }
    @Override
    public void run(){
        while (running) {
            try {
                Customer customer = customerGenerator.getCustomerQueue().take();
                customer.setOrderTicket(waiter.takeOrder());
                webSocketService.orderStartCreation(customer.getName()+" начал делать заказ: "+customer.getOrderTicket().getOrder().getOrderItem()+". Айди заказа: "+customer.getOrderTicket().getOrder().getOrderId());
                Thread.sleep(interval);
                webSocketService.orderEndCreation(customer.getName()+" закончил делать заказ: "+customer.getOrderTicket().getOrder().getOrderItem()+". Заказ начат. Айди заказа: "+customer.getOrderTicket().getOrder().getOrderId());
                orderQueue.put(customer);
                waitingQueue.put(customer);
                customer.getOrderTicket().getCustomerPromise().thenAccept(voidResult -> {
                    webSocketService.orderServing(customer.getName()+" получил заказ: "+customer.getOrderTicket().getOrder().getOrderItem()+". Заказ зваершен. Айди заказа: "+customer.getOrderTicket().getOrder().getOrderId());
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(e.getMessage());
            }
        }
    }
}
