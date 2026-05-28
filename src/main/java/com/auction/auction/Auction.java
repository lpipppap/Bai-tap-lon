package com.auction.auction;

import com.auction.model.*;
import com.auction.observer.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Auction {
    private int id;
    private Item item;
    private BidTransaction winningBid;
    private AuctionState state;
    private List<BidObserver> observers = new ArrayList<>();

    public Auction(Item item) {
        this.item = item;
        this.state = AuctionState.OPEN;
    }

    // State transitions
    public void startAuction() {
        if (state == AuctionState.OPEN) {
            state = AuctionState.RUNNING;
        }
    }

    public void finishAuction() {
        if (state == AuctionState.RUNNING) {
            state = AuctionState.FINISHED;
        }
    }

    // Observer pattern
    public void addObserver(BidObserver observer) {
        if (!observers.contains(observer)) {
        observers.add(observer);
        }
    }

    public void notifyObservers(double newPrice, String bidderName) {
        for (BidObserver observer : observers) {
            observer.onNewBid(newPrice, bidderName);
        }
    }

    // Getters
    public Item getItem() { return item; }
    public int getId() { return id; }
    public AuctionState getState() { return state; }
    public BidTransaction getWinningBid() { return winningBid; }
    public boolean isRunning() { return state == AuctionState.RUNNING; }
    public boolean isFinished() { return state == AuctionState.FINISHED; }
    public boolean isExpired() { return LocalDateTime.now().isAfter(item.getEndTime()); }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setState(AuctionState state) { this.state = state; }

    public void setWinningBid(BidTransaction winningBid) {
        this.winningBid = winningBid;
        this.item.setCurrentPrice(winningBid.getBidAmount());
    }
}
