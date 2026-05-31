package com.auction.controller;


import com.auction.dao.UserDAO;
import com.auction.util.SceneUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

public class ManagerUsersController {
    @FXML private TextField id;
    @FXML private Label warning;

    @FXML
    private void backToMenu(ActionEvent event) {
        SceneUtil.changeScene(event, "/com/auction/view/AuctionMenu.fxml");
    }

    @FXML
    private void deleteUser() {
        try {
            int userID = Integer.parseInt(id.getText());

            if (userID == 6) throw new Exception("You can't delete admin");

            if (UserDAO.getInstance().deleteUser(userID)) {
                warning.setTextFill(Color.web("#02fdba"));
                warning.setText("Delete user successfully");
            } else throw new Exception("Enter a valid id");

        } catch (NumberFormatException e) {
            warning.setTextFill(Color.RED);
            warning.setText("Enter a valid id");
        } catch (Exception e) {
            warning.setTextFill(Color.RED);
            warning.setText(e.getMessage());
        }
    }
}
