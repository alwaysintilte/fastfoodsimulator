package com.example.fastfoodsimulator.services;

import com.example.fastfoodsimulator.WebSocket.WebSocketService;
import com.example.fastfoodsimulator.models.Customer;
import com.example.fastfoodsimulator.models.Waiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
public class WaiterService {
    private List<Waiter> waiters = new ArrayList<>();
    private ExecutorService executorService;
    @Autowired
    private CustomerGenerator customerGenerator;
    @Autowired
    private WebSocketService webSocketService;
    private Integer orderCounter = 0;
    private final BlockingQueue<Customer> orderQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<Customer> waitingQueue = new LinkedBlockingQueue<>();
    private Integer interval;
    private Integer waiterCount = 1;
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
    public void setWaiterCount(Integer waiterCount){
        this.waiterCount = waiterCount;
    }
    public void stop() {
        running = false;
        executorService.shutdownNow();
        clearOrderQueue();
        clearWaitingQueue();
    }
    public void start() {
        running=true;
        orderCounter=0;
        executorService = Executors.newFixedThreadPool(waiterCount);
        waiters.clear();
        for (int i = 0; i < waiterCount; i++) {
            Waiter waiter = new Waiter();
            waiter.setCounter(orderCounter);
            waiter.setInterval(interval);
            waiters.add(waiter);
            executorService.submit(() -> runWaiter(waiter));
        }
    }
    public void runWaiter(Waiter waiter){
        while (running) {
            try {
                Customer customer = customerGenerator.getCustomerQueue().take();
                customer.setOrderTicket(waiter.takeOrder());
                webSocketService.orderStartCreation(customer.getName()+" начал делать заказ: "+customer.getOrderTicket().getOrder().getOrderItem()+". Айди заказа: "+customer.getOrderTicket().getOrder().getOrderId());
                waiter.processOrder();
                webSocketService.orderEndCreation(customer.getName()+" закончил делать заказ: "+customer.getOrderTicket().getOrder().getOrderItem()+". Заказ начат. Айди заказа: "+customer.getOrderTicket().getOrder().getOrderId());
                orderQueue.put(customer);
                waitingQueue.put(customer);
                customer.getOrderTicket().getCustomerPromise().thenAccept(voidResult -> {
                    webSocketService.orderServing(customer.getName()+" получил заказ: "+customer.getOrderTicket().getOrder().getOrderItem()+". Заказ зваершен. Айди заказа: "+customer.getOrderTicket().getOrder().getOrderId());
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(e.getMessage());
                break;
            }
        }
    }
}
