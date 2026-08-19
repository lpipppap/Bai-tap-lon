package com.auction.controller;

import com.auction.auction.Auction;
import com.auction.auction.AuctionState;
import com.auction.network.client.AuctionClient;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class AuctionPreviewController implements AuctionClient.ServerEventListener {
    @FXML private Label name;
    @FXML private Label status;
    @FXML private Label price;
    @FXML private ImageView image;

    private Auction auction;
    private AuctionMenuController auctionMenuController;

    @FXML
    private void chooseAuction(MouseEvent event) {
        if (auctionMenuController != null) {
            auctionMenuController.showAuctionDetails(auction);
        }
    }

    public void setAuctionPreview(Auction auction, AuctionMenuController auctionMenuController) {
        this.auction = auction;
        this.auctionMenuController = auctionMenuController;

        name.setText("Name: " + auction.getItem().getName());
        status.setText("Status: " + auction.getState().toString());
        price.setText("Current price: " + auction.getItem().getCurrentPrice());
        image.setImage(new Image(auction.getItem().getImage(), true));

        // Đăng ký nhận real-time update từ server
        AuctionClient.getInstance().addListener(this);
    }

    /**
     * Gọi khi AuctionMenuController bị dispose / người dùng rời màn hình menu,
     * để tránh memory leak và callback vô dụng.
     */
    public void dispose() {
        AuctionClient.getInstance().removeListener(this);
    }

    @Override
    public void onNewPrice(int auctionId, double newPrice, String bidderId) {
        // Chỉ cập nhật thẻ thuộc phiên nhận được sự kiện
        if (auction == null || auction.getId() != auctionId) return;

        auction.getItem().setCurrentPrice(newPrice);
        price.setText("Current price: " + newPrice);
    }

    @Override
    public void onAuctionEnded(int auctionId) {
        if (auction == null || auction.getId() != auctionId) return;

        auction.setState(AuctionState.FINISHED);
        status.setText("Status: " + AuctionState.FINISHED);
    }

}
