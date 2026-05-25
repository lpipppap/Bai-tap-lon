package com.auction.model;

import java.time.LocalDateTime;

public class Art extends Item {
    public Art(String name, double startPrice, LocalDateTime startTime, LocalDateTime endTime, Seller seller, String description, String image) {
        super(name, startPrice, startTime, endTime, seller, description, image);
    }
    @Override
    public void getInfo() {
        System.out.println("[Art] | Name: " + name + " | ID: " + id + " | Starting price: " + startPrice);
    }
}

