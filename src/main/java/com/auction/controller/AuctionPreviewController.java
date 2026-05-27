package com.auction.controller;

import com.auction.auction.Auction;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class AuctionPreviewController {
    @FXML private Label name;
    @FXML private Label status;
    @FXML private Label price;
    @FXML private ImageView image;

    public void setAuctionData(Auction auction) {
        name.setText("Name: " + auction.getItem().getName());
        status.setText("Status: " + auction.getState().toString());
        price.setText("Current price: " + auction.getItem().getCurrentPrice());
        image.setImage(new Image("file:path/to/images/" + auction.getItem().getImage()));
    }
}
