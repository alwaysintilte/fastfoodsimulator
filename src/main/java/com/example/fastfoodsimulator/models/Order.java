package com.example.fastfoodsimulator.models;

public class Order {
    private Integer orderId;

    private String orderItem;

    public Order() {}
    public Order(Integer orderId, String orderItem) {
        this.orderId = orderId;
        this.orderItem = orderItem;
    }

    public String getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(String orderItem) {
        this.orderItem = orderItem;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

}