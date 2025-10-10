package com.example.fastfoodsimulator.models;

public class Cook {
    private Integer interval;
    public Cook(){}
    public void cookOrder() {
        try {
            Thread.sleep(interval);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public void setInterval(Integer interval){
        this.interval = interval;
    }
}
