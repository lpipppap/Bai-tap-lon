package com.auction.controller;

import com.auction.auction.Auction;
import com.auction.model.Bidder;
import com.auction.model.User;
import com.auction.network.client.AuctionClient;
import com.auction.util.SceneUtil;
import com.auction.util.SessionManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

public class PlaceBidController implements AuctionClient.ServerEventListener {

    @FXML private Label    name;
    @FXML private Label    startPrice;
    @FXML private Label    currentPrice;
    @FXML private Label    clock;
    @FXML private Label    description;
    @FXML private TextField enterPrice;
    @FXML private Button   placeBid;
    @FXML private Label    warning;

    private Auction auction;

    // Khởi tạo

    @FXML
    private void initialize() {
        User user = SessionManager.getCurrentUser();
        if (!(user instanceof Bidder)) {
            placeBid.setDisable(true);
        }

        // Đăng ký controller này làm listener để nhận sự kiện từ server
        AuctionClient.getInstance().addListener(this);
    }

    @FXML
    private void backToMenu(ActionEvent event) {
        // Huỷ đăng ký listener khi rời màn hình
        AuctionClient.getInstance().removeListener(this);
        SceneUtil.changeScene(event, "/com/auction/view/AuctionMenu.fxml");
    }

    public void setData(Auction auction) {
        this.auction = auction;
        name.setText("Product name: "  + auction.getItem().getName());
        startPrice.setText("Start price: " + auction.getItem().getStartPrice());
        currentPrice.setText("Highest price: " + auction.getItem().getCurrentPrice());
        clock.setText("");
        description.setText(auction.getItem().getDescription());
    }

    // Đặt giá — gửi lệnh BID qua AuctionClient → TCP Socket → Server

    @FXML
    private void placeBidAction() {
        String priceText = enterPrice.getText();

        // Validate số cục bộ trước khi gửi lên server
        double bidAmount;
        try {
            bidAmount = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            warning.setText("Please enter a valid number");
            return;
        }

        if (bidAmount <= auction.getItem().getCurrentPrice()) {
            warning.setText("Price must be higher than the current price");
            return;
        }

        User currentUser = SessionManager.getCurrentUser();
        if (!(currentUser instanceof Bidder)) {
            warning.setText("Only bidders can place bids");
            return;
        }

        // Gửi lệnh BID lên server qua AuctionClient (socket TCP)
        // Luồng: AuctionClient → TCP → ClientHandler → AuctionManager
        //        → BidDAO → DB → AuctionServer.broadcast() → callback về đây

        AuctionClient.getInstance().sendBid(
                auction.getId(),
                currentUser.getId(),
                bidAmount
        );

        // Hiển thị trạng thái "đang chờ" trong khi chờ phản hồi từ server
        warning.setTextFill(Color.web("#aaaaaa"));
        warning.setText("Sending bid...");
    }


    // Callback nhận sự kiện từ server (chạy trên JavaFX Application Thread)

    /**
     * Server broadcast NEW_PRICE → AuctionClient gọi callback này.
     * Cập nhật currentPrice trên giao diện người dùng.
     */
    @Override
    public void onNewPrice(int auctionId, double newPrice, String bidderId) {
        // Chỉ cập nhật nếu sự kiện thuộc phiên đang xem
        if (auction == null || auction.getId() != auctionId) return;

        // Cập nhật model in-memory
        auction.getItem().setCurrentPrice(newPrice);

        // Cập nhật UI label
        currentPrice.setText("Highest price: " + newPrice);

        // Phân biệt: giá của mình hay của người khác
        String currentUserId = String.valueOf(SessionManager.getCurrentUser().getId());
        if (currentUserId.equals(bidderId)) {
            warning.setTextFill(Color.web("#02fdba"));
            warning.setText("Bid placed successfully!");
        } else {
            warning.setTextFill(Color.web("#FFA500"));
            warning.setText("New bid from another user: " + newPrice);
        }

        // Xoá ô nhập giá sau khi đặt thành công
        enterPrice.clear();
    }

    /**
     * Server trả về lỗi (giá không hợp lệ, phiên đóng...).
     */
    @Override
    public void onError(String message) {
        warning.setTextFill(Color.web("#ff4444"));
        warning.setText(message);
    }

    @Override
    public void onAuctionEnded(int auctionId){
        if (auction == null || auction.getId() != auctionId) return;
        placeBid.setDisable(true);
        warning.setTextFill(Color.web("#ff4444"));
        warning.setText("This auction has ended.");
    };
}