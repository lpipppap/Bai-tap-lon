package com.auction.model;

import java.time.LocalDateTime;

public class Vehicle extends Item {
    public Vehicle(String name, double startPrice, LocalDateTime startTime, LocalDateTime endTime, Seller seller, String description, String image) {
        super(name, startPrice, startTime, endTime, seller, description, image);
    }
    @Override
    public void getInfo() {
        System.out.println("[Vehicle] | Name: " + name + " | ID: " + id + " | Starting price: " + startPrice);
    }
}