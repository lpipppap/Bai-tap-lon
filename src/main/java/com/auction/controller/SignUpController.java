package com.auction.controller;

import com.auction.manager.UserManager;
import com.auction.model.User;
import com.auction.util.SceneUtil;
import com.auction.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class SignUpController {
    @FXML private ToggleGroup role;
    @FXML private ToggleButton bidder;
    @FXML private ToggleButton seller;
    @FXML private TextField username;
    @FXML private TextField email;
    @FXML private TextField pwShown;
    @FXML private PasswordField pwHidden;
    @FXML private PasswordField manhattanCafe;
    @FXML private Button lever;
    @FXML private Label warning;

    @FXML
    private void initialize() {
        pwShown.textProperty().bindBidirectional(pwHidden.textProperty());

        pwShown.setVisible(false);
        pwShown.setManaged(false);


        bidder.setSelected(true);
        seller.setSelected(false);
        bidder.setStyle("-fx-background-color: #292b29;");
        seller.setStyle("-fx-background-color: #000000;");
    }

    @FXML
    private void togglePassword(ActionEvent event) {
        if (pwHidden.isVisible()) {
            pwHidden.setVisible(false);
            pwHidden.setManaged(false);

            pwShown.setVisible(true);
            pwShown.setManaged(true);

            lever.setText("Hide");
        } else {
            pwHidden.setVisible(true);
            pwHidden.setManaged(true);

            pwShown.setVisible(false);
            pwShown.setManaged(false);

            lever.setText("Show");
        }
    }

    @FXML
    private void bidderButton() {
        bidder.setSelected(true);
        seller.setSelected(false);
        bidder.setStyle("-fx-background-color: #292b29;");
        seller.setStyle("-fx-background-color: #000000;");
    }

    @FXML
    private void sellerButton() {
        seller.setSelected(true);
        bidder.setSelected(false);
        seller.setStyle("-fx-background-color: #292b29;");
        bidder.setStyle("-fx-background-color: #000000;");
    }

    @FXML
    private void backToStart(ActionEvent event) {
        SceneUtil.changeScene(event, "/com/auction/view/Start.fxml");
    }

    @FXML
    private void loginAction(ActionEvent event) {
        SceneUtil.changeScene(event, "/com/auction/view/Login.fxml");
    }

    @FXML
    private void signUpAction(ActionEvent event) {
        String un = username.getText();
        String em = email.getText();
        String pw = pwHidden.getText();
        String cf = manhattanCafe.getText();
        ToggleButton selectedButton = (ToggleButton) role.getSelectedToggle();
        String ro = selectedButton.getText();

        try {
            UserManager.getInstance().registerUser(un, pw, cf, em, ro);
            User user = UserManager.getInstance().loginUser(em, pw, ro);
            SessionManager.setCurrentUser(user);
            SceneUtil.changeScene(event, "/com/auction/view/AuctionMenu.fxml");
        } catch (IllegalArgumentException e) {
            warning.setText(e.getMessage());
        } catch (Exception e) {
            warning.setText("Error");
        }
    }
}
