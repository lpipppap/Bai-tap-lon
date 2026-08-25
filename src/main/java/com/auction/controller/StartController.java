package com.auction.controller;

import javafx.fxml.FXML;
import com.auction.util.SceneUtil;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class StartController {
    @FXML ImageView imageView;

    @FXML
    public void initialize() {
        imageView.setImage(new Image("https://res.cloudinary.com/kurylrtx/image/upload/v1787569816/IMG_6809_uj8czc.jpg", true));
    }

    @FXML
    private void loginAction(ActionEvent event) {
        SceneUtil.changeScene(event, "/com/auction/view/Login.fxml");
    }

    @FXML
    private void signUpAction(ActionEvent event) {
        SceneUtil.changeScene(event, "/com/auction/view/SignUp.fxml");
    }
}
