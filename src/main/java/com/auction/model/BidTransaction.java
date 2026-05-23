package com.auction.model;

public class BidTransaction {
    private String bidderName;
    private double amount;

    public BidTransaction(String bidderName, double amount) {
        this.bidderName = bidderName;
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }
    public String getBidderName() {
        return bidderName;
    }
}
