package com.auction.model;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class Item extends Entity {
    protected String name;
    protected double startPrice;
    protected double currentPrice;
    protected LocalDateTime startTime;
    protected LocalDateTime endTime;

    public Item(String name, double startPrice) {
        super();
        this.name = name;
        this.startPrice = startPrice;
        this.currentPrice = startPrice;
        this.startTime = LocalDateTime.now();
        this.endTime = startTime.plusMinutes(5);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public double getStartPrice() {
        return startPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    abstract public void getInfo();
}
