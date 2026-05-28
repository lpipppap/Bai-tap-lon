package com.auction.controller;

import com.auction.auction.Auction;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AuctionDetailsController {
    @FXML private Label name;
    @FXML private Label type;
    @FXML private  Label currentPrice;
    @FXML private Label timeLeft;

    public void setDetailsView(Auction auction) {
        name.setText("Product name: " + auction.getItem().getName());
        type.setText("Type: " + auction.getItem().getClass().getSimpleName());
        currentPrice.setText("Current price: " + auction.getItem().getCurrentPrice());
        timeLeft.setText("");
    }
}