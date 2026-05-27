package com.auction.controller;

import com.auction.model.Seller;
import com.auction.model.User;
import com.auction.util.SceneUtil;
import com.auction.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class AuctionMenuController {
    @FXML private TextField searchBox;
    @FXML private Button createAuction;

    @FXML
    private void initialize() {
        User user = SessionManager.getCurrentUser();
        if (user instanceof Seller) {
            createAuction.setVisible(true);
            createAuction.setDisable(false);
            createAuction.setManaged(true);
        } else {
            createAuction.setVisible(false);
            createAuction.setDisable(true);
            createAuction.setManaged(false);
        }
    }

    @FXML
    private void profileAction(ActionEvent event) {
        SceneUtil.changeScene(event, "/com/auction/view/Profile.fxml");
    }

    @FXML
    private void auctionMenuAction(ActionEvent event) {
        SceneUtil.changeScene(event, "/com/auction/view/AuctionMenu.fxml");
    }

    @FXML
    private void createAuctionAction(ActionEvent event) {
        SceneUtil.changeScene(event, "/com/auction/view/CreateAuction.fxml");
    }
}
