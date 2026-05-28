package com.auction.model;

import java.time.LocalDateTime;

public class BidTransaction {
    private int bidId;
    private int auctionId;
    private Bidder bidder;
    private double bidAmount;
    private LocalDateTime bidTime;

    public BidTransaction(int auctionId, Bidder bidder, double bidAmount, LocalDateTime bidTime) {
        this.auctionId = auctionId;
        this.bidder = bidder;
        this.bidAmount = bidAmount;
        this.bidTime = bidTime;
    }

    public int getBidId() { return bidId; }
    public void setBidId(int bidId) { this.bidId = bidId; }

    public int getAuctionId() { return auctionId; }
    public void setAuctionId(int auctionId) { this.auctionId = auctionId; }

    public User getBidder() { return bidder; }
    public void setBidder(Bidder bidder) { this.bidder = bidder; }

    public double getBidAmount() { return bidAmount; }
    public void setBidAmount(double bidAmount) { this.bidAmount = bidAmount; }

    public LocalDateTime getBidTime() { return bidTime; }
    public void setBidTime(LocalDateTime bidTime) { this.bidTime = bidTime; }
}
