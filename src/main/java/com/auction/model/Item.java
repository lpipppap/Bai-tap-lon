package com.auction.model;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class Item extends Entity {
    protected String name;
    protected double startPrice;
    protected double currentPrice;
    protected LocalDateTime startTime;
    protected LocalDateTime endTime;
    protected Seller seller;
    protected String description;
    protected String image;

    public Item(String name, double startPrice, LocalDateTime startTime, LocalDateTime endTime, Seller seller, String description, String image) {
        super();
        this.name = name;
        this.startPrice = startPrice;
        this.startTime = startTime;
        this.endTime = endTime;
        this.seller = seller;
        this.description = description;
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStartPrice(double startPrice) {
        this.startPrice = startPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public double getStartPrice() {
        return startPrice;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public Seller getSeller() {
        return seller;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    abstract public void getInfo();
}
