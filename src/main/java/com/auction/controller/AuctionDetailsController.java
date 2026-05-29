package com.auction.controller;

import com.auction.auction.Auction;
import com.auction.util.SceneUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class AuctionDetailsController {
    @FXML private Label name;
    @FXML private Label type;
    @FXML private  Label currentPrice;
    @FXML private Label timeLeft;
    private Auction auctionn;

    public void setDetailsView(Auction auction) {
        this.auctionn = auction;
        name.setText("Product name: " + auction.getItem().getName());
        type.setText("Type: " + auction.getItem().getClass().getSimpleName());
        currentPrice.setText("Current price: " + auction.getItem().getCurrentPrice());
        timeLeft.setText("");
    }
    @FXML
    private void joinAuctionAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/auction/view/PlaceBid.fxml"));
            Parent placeBidRoot = loader.load();

            PlaceBidController placeBidController = loader.getController();

            placeBidController.setData(this.auctionn);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(placeBidRoot);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            System.out.println("✗ Lỗi không chuyển được sang màn hình PlaceBid!");
            e.printStackTrace();
        }
    }
}