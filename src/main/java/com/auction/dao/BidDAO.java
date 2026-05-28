package com.auction.dao;

import com.auction.model.BidTransaction;

public class BidDAO {
    private static BidDAO instance;

    private BidDAO() {}

    public static BidDAO getInstance() {
        if (instance == null) {
            instance = new BidDAO();
        } return instance;
    }

    public boolean saveBid(BidTransaction bidTransaction) {
        return true;
    }
}
