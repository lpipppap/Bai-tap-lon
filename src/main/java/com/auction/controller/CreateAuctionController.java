package com.auction.controller;

import com.auction.manager.AuctionManager;
import com.auction.model.Seller;
import com.auction.model.User;
import com.auction.util.SceneUtil;
import com.auction.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class CreateAuctionController {
    @FXML private ToggleGroup type;
    @FXML private ToggleButton art;
    @FXML private ToggleButton electronics;
    @FXML private ToggleButton vehicle;
    @FXML private TextField name;
    @FXML private TextField startPrice;
    @FXML private DatePicker startDate;
    @FXML private DatePicker endDate;
    @FXML private Spinner<Integer> startHour;
    @FXML private Spinner<Integer> startMinute;
    @FXML private Spinner<Integer> endHour;
    @FXML private Spinner<Integer> endMinute;
    @FXML private TextArea description;
    @FXML private ImageView imageView;
    @FXML private Button setImage;
    @FXML private Button createButton;
    @FXML private Label warning;
    private File selectedImageFile;
    private String imageFileName;

    @FXML
    private void initialize() {

        art.setSelected(true);
        electronics.setSelected(false);
        vehicle.setSelected(false);

        art.setStyle("-fx-background-color: #02fdba;");
        art.setTextFill(Color.BLACK);
        electronics.setStyle("-fx-background-color: #000000;");
        electronics.setTextFill(Color.web("#02fdba"));
        vehicle.setStyle("-fx-background-color: #000000;");
        vehicle.setTextFill(Color.web("#02fdba"));

        startHour.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0));
        endHour.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0));
        startMinute.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
        endMinute.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
    }

    @FXML
    private void auctionMenuAction(ActionEvent event) {
        SceneUtil.changeScene(event, "/com/auction/view/AuctionMenu.fxml");
    }

    @FXML
    private void artButton() {
        art.setSelected(true);
        electronics.setSelected(false);
        vehicle.setSelected(false);

        art.setStyle("-fx-background-color: #02fdba;");
        art.setTextFill(Color.BLACK);
        electronics.setStyle("-fx-background-color: #000000;");
        electronics.setTextFill(Color.web("#02fdba"));
        vehicle.setStyle("-fx-background-color: #000000;");
        vehicle.setTextFill(Color.web("#02fdba"));
    }

    @FXML
    private void electronicButton() {
        art.setSelected(false);
        electronics.setSelected(true);
        vehicle.setSelected(false);

        electronics.setStyle("-fx-background-color: #02fdba;");
        electronics.setTextFill(Color.BLACK);
        art.setStyle("-fx-background-color: #000000;");
        art.setTextFill(Color.web("#02fdba"));
        vehicle.setStyle("-fx-background-color: #000000;");
        vehicle.setTextFill(Color.web("#02fdba"));
    }

    @FXML
    private void vehicleButton() {
        art.setSelected(false);
        electronics.setSelected(false);
        vehicle.setSelected(true);

        vehicle.setStyle("-fx-background-color: #02fdba;");
        vehicle.setTextFill(Color.BLACK);
        electronics.setStyle("-fx-background-color: #000000;");
        electronics.setTextFill(Color.web("#02fdba"));
        art.setStyle("-fx-background-color: #000000;");
        art.setTextFill(Color.web("#02fdba"));
    }

    @FXML
    private void selectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select product image");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(imageView.getScene().getWindow());

        if (file != null) {
            this.selectedImageFile = file;
            this.imageFileName = file.getName();

            Image image = new Image(file.toURI().toString());
            imageView.setImage(image);
        }
    }

    @FXML
    private void createAuctionAction() {
        ToggleButton selectedButton = (ToggleButton) type.getSelectedToggle();
        String ty = selectedButton.getText();
        String na = name.getText();
        String sp = startPrice.getText();

        LocalDateTime st = null;
        LocalDateTime et = null;
        if (startDate.getValue() != null && endDate.getValue() != null) {
            st = LocalDateTime.of(startDate.getValue(), LocalTime.of(startHour.getValue(), startMinute.getValue()));
            et = LocalDateTime.of(endDate.getValue(), LocalTime.of(endHour.getValue(), endMinute.getValue()));
        }

        String de = description.getText();
        String im = imageFileName;

        try {
            if (AuctionManager.getInstance().createAuction(ty, na, sp, st, et, (Seller) SessionManager.getCurrentUser(), de, im)) {
                warning.setTextFill(Color.web("#02fdba"));
                warning.setText("Auction created successfully!");
            } else throw new Exception();
        } catch (IllegalArgumentException e) {
            warning.setTextFill(Color.RED);
            warning.setText(e.getMessage());
        } catch (Exception e) {
            warning.setText("Error");
        }
    }
}
