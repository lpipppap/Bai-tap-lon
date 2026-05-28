package com.auction.controller;

import com.auction.auction.Auction;
import com.auction.auction.AuctionState;
import com.auction.dao.AuctionDAO;
import com.auction.model.Admin;
import com.auction.model.Seller;
import com.auction.model.User;
import com.auction.util.SceneUtil;
import com.auction.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AuctionMenuController {
    @FXML private GridPane gridPane;
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
        try {
            displayAuctions(user);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("sorry");
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

    public void displayPreparation(List<Auction> auctionList) throws IOException {
        int col = 0;
        int row = 0;

        for (Auction auction : auctionList) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/auction/view/AuctionPreview.fxml"));
            AnchorPane card = loader.load();

            AuctionPreviewController controller = loader.getController();
            controller.setAuctionPreview(auction);

            gridPane.add(card, col, row);

            col++;
            if (col == 4) {
                col = 0;
                row++;
            }
        }
    }

    private void displayAuctions(User user) throws IOException {
        List<Auction> allAuctions = AuctionDAO.getInstance().getAllAuctions();
        List<Auction> filtered = new ArrayList<>(
        );

        if (user instanceof Seller) {
            int sellerId = user.getId();
            for (Auction auction : allAuctions) {
                if (auction.getItem().getSeller().getId() == sellerId) {
                    filtered.add(auction);
                }
            }
        } else if (user instanceof Admin) {
            filtered = allAuctions;
        } else {
            for (Auction auction : allAuctions) {
                if (auction.getState() == AuctionState.RUNNING || auction.getState() == AuctionState.OPEN || auction.getState() == AuctionState.FINISHED) {
                    filtered.add(auction);
                }
            }
        }

        displayPreparation(filtered);
    }
}
