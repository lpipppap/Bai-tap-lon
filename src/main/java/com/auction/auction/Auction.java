package com.auction.auction;

import com.auction.model.*;
import com.auction.observer.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Auction {
    private int id;
    private Item item;
    private BidTransaction winningBid;
    private AuctionState state;

    private List<BidObserver> observers = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();

    public Auction(Item item) {
        this.item = item;
        this.state = AuctionState.OPEN;
    }
    //LOGIC CHUYỂN TRẠNG THÁI
    public void startAuction() {
        if (state == AuctionState.OPEN) {
            state = AuctionState.RUNNING;
            System.out.println("Auction started");
        }
    }

    public void finishAuction() {
        if (state == AuctionState.RUNNING) {
            state = AuctionState.FINISHED;
            System.out.println("Auction closed");
        }
    }

    //OBSERVER PATTERN
    public void addObserver(BidObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(double newPrice, String bidderName) {
        for (BidObserver observer : observers) {
            observer.onNewBid(newPrice, bidderName);
        }
    }

    //XỬ LÝ ĐẶT GIÁ ĐỒNG THỜI (CONCURRENCY)
    public boolean placeBid(String bidderName, double bidAmount) {
        if (state != AuctionState.RUNNING) {
            System.out.println("Auction is not running");
            return false;
        }
        lock.lock();
        try {
            if (bidAmount > item.getCurrentPrice()) {
                item.setCurrentPrice(bidAmount);
//                winningBid = new BidTransaction(bidderName, bidAmount);       lỗi, tạm thời disable

                notifyObservers(bidAmount, bidderName);
                return true;
            }
            System.out.println("Bid must be higher than current price");
            return false;
        } finally {
            lock.unlock(); // Luôn nhả khóa để tránh deadlock
        }
    }

    public Item getItem() {
        return this.item;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setState(AuctionState state) {
        this.state = state;
    }

    public void setWinningBid(BidTransaction winningBid) {
        this.winningBid = winningBid;
    }

    public AuctionState getState() {
        return state;
    }
}
