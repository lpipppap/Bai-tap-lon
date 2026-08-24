package com.auction.controller;

import com.auction.auction.Auction;
import com.auction.auction.AuctionState;
import com.auction.network.client.AuctionClient;
import com.auction.util.SceneUtil;
import com.auction.util.Timer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller cho panel chi tiết auction (bên phải AuctionMenu).
 *
 * Real-time update:
 *   - Đăng ký làm ServerEventListener trong setDetailsView().
 *   - Nhận NEW_PRICE → cập nhật currentPrice nếu đúng auctionId.
 *   - Nhận AUCTION_ENDED → cập nhật trạng thái và khoá nút Join.
 *   - Tự huỷ đăng ký qua dispose() — được gọi bởi AuctionMenuController
 *     trước khi load chi tiết phiên mới.
 */
public class AuctionDetailsController implements AuctionClient.ServerEventListener {
    @FXML private Label name;
    @FXML private Label type;
    @FXML private  Label currentPrice;
    @FXML private Label timeLeft;
    @FXML private ImageView image;
    private Auction auction;

    public void setDetailsView(Auction auction) {
        // Nếu đang xem phiên khác → huỷ listener cũ trước
        dispose();

        this.auction = auction;
        name.setText("Product name: " + auction.getItem().getName());
        type.setText("Type: " + auction.getItem().getClass().getSimpleName());
        currentPrice.setText("Current price: " + auction.getItem().getCurrentPrice());
        image.setImage(new Image(auction.getItem().getImage(), true));
        Timer.timer(timeLeft, auction.getItem().getEndTime());

        // Đăng ký nhận real-time update
        AuctionClient.getInstance().addListener(this);
    }

    /** Huỷ đăng ký khi panel bị thay thế bởi chi tiết phiên khác. */
    public void dispose() {
        AuctionClient.getInstance().removeListener(this);
    }

    @FXML
    private void joinAuctionAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/auction/view/PlaceBid.fxml"));
            Parent placeBidRoot = loader.load();

            PlaceBidController placeBidController = loader.getController();

            placeBidController.setData(this.auction);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(placeBidRoot);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            System.out.println("✗ Lỗi không chuyển được sang màn hình PlaceBid!");
            e.printStackTrace();
        }
    }

    @Override
    public void onNewPrice(int auctionId, double newPrice, String bidderId) {
        if (auction == null || auction.getId() != auctionId) return;

        auction.getItem().setCurrentPrice(newPrice);
        currentPrice.setText("Current price: " + newPrice);
    }

    @Override
    public void onAuctionEnded(int auctionId) {
        if (auction == null || auction.getId() != auctionId) return;

        auction.setState(AuctionState.FINISHED);
    }
}