package com.auction.controller;

import com.auction.auction.Auction;
import com.auction.manager.AuctionManager;
import com.auction.model.Bidder;
import com.auction.model.User;
import com.auction.util.SceneUtil;
import com.auction.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

public class PlaceBidController {
    @FXML private Label name;
    @FXML private Label startPrice;
    @FXML private Label currentPrice;
    @FXML private Label clock;
    @FXML private Label description;
    @FXML private TextField enterPrice;
    @FXML private Button placeBid;
    @FXML private Label warning;

    private Auction auctionnn;

    @FXML
    private void initialize() {
        User user = SessionManager.getCurrentUser();
        if (!(user instanceof Bidder)) {
            placeBid.setDisable(true);
        }
    }

    @FXML
    private void backToMenu(ActionEvent event) {
        SceneUtil.changeScene(event, "/com/auction/view/AuctionMenu.fxml");
    }

    public void setData(Auction auction) {
        this.auctionnn = auction;
        name.setText("Product name: " + auction.getItem().getName());
        startPrice.setText("Start price: " + auction.getItem().getStartPrice());
        currentPrice.setText("Highest price: " + auction.getItem().getCurrentPrice());
        clock.setText("");
        description.setText(auction.getItem().getDescription());
    }

    @FXML
    private void placeBidAction() {
        String price = enterPrice.getText();

        try {
            if (AuctionManager.getInstance().checkBid(auctionnn, price)) {
                warning.setTextFill(Color.web("#02fdba"));
                warning.setText("Bid is placed successfully!");
            } else throw new Exception();
        } catch (IllegalArgumentException e) {
            warning.setText(e.getMessage());
        } catch (Exception e) {
            warning.setText("Error");
        }
    }

}
