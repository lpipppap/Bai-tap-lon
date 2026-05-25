package com.auction.manager;

public class AuctionManager {
    private static AuctionManager instance;

    private AuctionManager() {
        System.out.println("Auction Manager created");
    }

    public AuctionManager getInstance() {
        if (instance == null) {
            instance = new AuctionManager();
        } return instance;
    }
}
