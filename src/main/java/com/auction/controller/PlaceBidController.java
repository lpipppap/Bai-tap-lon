package com.auction.controller;

import com.auction.auction.Auction;
import com.auction.util.SceneUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class PlaceBidController {
    @FXML private Label name;
    @FXML private Label startPrice;
    @FXML private Label currentPrice;
    @FXML private Label clock;
    @FXML private Label description;
    @FXML private TextField enterPrice;
    @FXML private Button placeBid;
    @FXML private Label warning;

    @FXML
    private void backToMenu(ActionEvent event) {
        SceneUtil.changeScene(event, "/com/auction/view/AuctionMenu.fxml");
    }

    public void setData(Auction auction) {
        name.setText("Product name: " + auction.getItem().getName());
        startPrice.setText("Start price: " + auction.getItem().getStartPrice());
        currentPrice.setText("Highest price: " + auction.getItem().getCurrentPrice());
        clock.setText("");
        description.setText(auction.getItem().getDescription());
    }

    @FXML
    private void placeBidAction() {
    }

}
