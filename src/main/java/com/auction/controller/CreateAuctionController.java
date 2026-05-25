package com.auction.controller;

import com.auction.util.SceneUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CreateAuctionController {
    @FXML private ToggleGroup type;
    @FXML private ToggleButton art;
    @FXML private ToggleButton electronic;
    @FXML private ToggleButton vehicle;
    @FXML private TextField name;
    @FXML private TextField startPrice;
    @FXML private DatePicker startTime;
    @FXML private DatePicker endTime;
    @FXML private TextArea description;
    @FXML private Button setImage;
    @FXML private Button createButton;
    @FXML private Label warning;

    @FXML
    private void initialize() {
        art.setSelected(true);
        electronic.setSelected(false);
        vehicle.setSelected(false);

        art.setStyle("-fx-background-color: #292b29;");
        electronic.setStyle("-fx-background-color: #000000;");
        vehicle.setStyle("-fx-background-color: #000000;");
    }

    @FXML
    private void auctionMenuAction(ActionEvent event) {
        SceneUtil.changeScene(event, "/com/auction/view/AuctionMenu.fxml");
    }

    @FXML
    private void artButton() {
        art.setSelected(true);
        electronic.setSelected(false);
        vehicle.setSelected(false);

        art.setStyle("-fx-background-color: #292b29;");
        electronic.setStyle("-fx-background-color: #000000;");
        vehicle.setStyle("-fx-background-color: #000000;");
    }

    @FXML
    private void electronicButton() {
        art.setSelected(false);
        electronic.setSelected(true);
        vehicle.setSelected(false);

        electronic.setStyle("-fx-background-color: #292b29;");
        art.setStyle("-fx-background-color: #000000;");
        vehicle.setStyle("-fx-background-color: #000000;");
    }

    @FXML
    private void vehicleButton() {
        art.setSelected(false);
        electronic.setSelected(false);
        vehicle.setSelected(true);

        vehicle.setStyle("-fx-background-color: #292b29;");
        electronic.setStyle("-fx-background-color: #000000;");
        art.setStyle("-fx-background-color: #000000;");
    }

    @FXML
    private void createAuctionAction(ActionEvent event) {
        String na = name.getText();
        String sp = startPrice.getText();
    }
}
