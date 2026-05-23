package com.auction.controller;

import javafx.fxml.FXML;
import com.auction.util.SceneUtil;
import javafx.event.ActionEvent;

public class StartController {
    @FXML
    private void loginAction(ActionEvent event) {
        SceneUtil.changeScene(event, "/com/auction/view/Login.fxml");
    }

    @FXML
    private void signUpAction(ActionEvent event) {
        SceneUtil.changeScene(event, "/com/auction/view/SignUp.fxml");
    }
}
