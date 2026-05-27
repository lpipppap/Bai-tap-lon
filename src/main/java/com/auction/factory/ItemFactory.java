package com.auction.factory;
import com.auction.model.*;

import java.time.LocalDateTime;

public class ItemFactory {
    public static Item createItem(String type, String name, double startPrice, LocalDateTime startTime, LocalDateTime endTime, Seller seller, String description, String image) {
        switch (type.toLowerCase()) {
            case "electronics" :
                return new Electronics(name, startPrice, startTime, endTime, seller, description, image);
            case "art":
                return new Art(name, startPrice, startTime, endTime, seller, description, image);
            case "vehicle":
                return new Vehicle(name, startPrice, startTime, endTime, seller, description, image);
            default:
                try {
                    throw new IllegalAccessException("Illegal item's type: " + type);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
        }
    }
}
