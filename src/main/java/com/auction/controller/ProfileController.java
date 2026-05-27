package com.auction.controller;

import com.auction.model.User;
import com.auction.util.SceneUtil;
import com.auction.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ProfileController {
    @FXML private Label id;
    @FXML private Label name;
    @FXML private Label email;

    User user = SessionManager.getCurrentUser();

    @FXML
    private void initialize() {
        id.setText(user.getClass().getSimpleName() + ": " + user.getId());
        name.setText("Name: " + user.getUsername());
        email.setText("Email: " + user.getEmail());
    }
    @FXML
    private void auctionMenuAction(ActionEvent event) {
        SceneUtil.changeScene(event, "/com/auction/view/AuctionMenu.fxml");
    }

    @FXML
    private void profileAction(ActionEvent event) {
        SceneUtil.changeScene(event, "/com/auction/view/Profile.fxml");
    }

    @FXML
    private void logOutAction(ActionEvent event) {
        SessionManager.logout();
        SceneUtil.changeScene(event, "/com/auction/view/Start.fxml");
    }
}
