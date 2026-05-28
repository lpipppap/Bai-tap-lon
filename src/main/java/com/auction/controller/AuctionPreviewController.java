package com.auction.controller;

import com.auction.auction.Auction;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.input.MouseEvent;

public class AuctionPreviewController {
    @FXML private Label name;
    @FXML private Label status;
    @FXML private Label price;
    @FXML private ImageView image;

    private Auction auction;
    private AuctionMenuController auctionMenuController;

    @FXML
    private void chooseAuction(MouseEvent event) {
        if (auctionMenuController != null) {
            auctionMenuController.showAuctionDetails(auction);
        }
    }

    public void setAuctionPreview(Auction auction, AuctionMenuController auctionMenuController) {
        name.setText("Name: " + auction.getItem().getName());
        status.setText("Status: " + auction.getState().toString());
        price.setText("Current price: " + auction.getItem().getCurrentPrice());
        image.setImage(new Image("file:path/to/images/" + auction.getItem().getImage()));

        this.auction = auction;
        this.auctionMenuController = auctionMenuController;
    }
}
