package com.auction.observer;
import com.auction.model.Bidder;


public interface BidObserver {
    void onNewBid(double newPrice, Bidder bidder);
}
