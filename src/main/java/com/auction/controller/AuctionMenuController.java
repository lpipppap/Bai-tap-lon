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
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
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
    @FXML private Button manageUsers;
    @FXML private AnchorPane details;
    private final List<AuctionPreviewController> previewControllers = new ArrayList<>();
    private AuctionDetailsController currentDetailController;

    @FXML
    private void initialize() {
        User user = SessionManager.getCurrentUser();
        if (!(user instanceof Seller)) {
            createAuction.setVisible(false);
            createAuction.setDisable(true);
            createAuction.setManaged(false);
        } if (!(user instanceof Admin)) {
            manageUsers.setVisible(false);
            manageUsers.setDisable(true);
            manageUsers.setManaged(false);
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
        disposeAllPreviews();
        SceneUtil.changeScene(event, "/com/auction/view/Profile.fxml");
    }

    @FXML
    private void auctionMenuAction(ActionEvent event) {
        SceneUtil.changeScene(event, "/com/auction/view/AuctionMenu.fxml");
    }

    @FXML
    private void createAuctionAction(ActionEvent event) {
        disposeAllPreviews();
        SceneUtil.changeScene(event, "/com/auction/view/CreateAuction.fxml");
    }

    @FXML
    private void ManageUsersAction(ActionEvent event) {
        disposeAllPreviews();
        SceneUtil.changeScene(event, "/com/auction/view/ManageUser.fxml");
    }

    public void displayPreparation(List<Auction> auctionList) throws IOException {
        int col = 0;
        int row = 0;

        for (Auction auction : auctionList) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/auction/view/AuctionPreview.fxml"));
            AnchorPane card = loader.load();

            AuctionPreviewController controller = loader.getController();
            controller.setAuctionPreview(auction, this);

            // Ghi nhớ để dispose() sau
            previewControllers.add(controller);

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
        List<Auction> filtered = new ArrayList<>();

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
                if (auction.getState() == AuctionState.RUNNING || auction.getState() == AuctionState.OPEN) {
                    filtered.add(auction);
                }
            }
        }

        displayPreparation(filtered);
    }

    public void showAuctionDetails(Auction selectedAuction) {
        try {
            // Huỷ listener của panel chi tiết cũ trước khi load panel mới
            if (currentDetailController != null) {
                currentDetailController.dispose();
            }

            // 1. Load file FXML của màn hình Detail
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/auction/view/AuctionDetails.fxml"));
            AnchorPane detailNode = loader.load();

            // 2. Lấy Controller của màn hình Detail để bơm dữ liệu phòng qua
            AuctionDetailsController detailController = loader.getController();
            detailController.setDetailsView(selectedAuction);

            // 3. Xóa sạch những thứ cũ đang hiển thị ở ô bên phải (nếu có)
            details.getChildren().clear();

            // 4. Ép giao diện Detail tự động giãn vừa khít ô bên phải
            AnchorPane.setTopAnchor(detailNode, 0.0);
            AnchorPane.setBottomAnchor(detailNode, 0.0);
            AnchorPane.setLeftAnchor(detailNode, 0.0);
            AnchorPane.setRightAnchor(detailNode, 0.0);

            // 5. Nhét giao diện Detail vào ô bên phải!
            details.getChildren().add(detailNode);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Huỷ đăng ký tất cả AuctionPreviewController khỏi AuctionClient.
     * Phải gọi trước mỗi lần chuyển scene để tránh memory leak.
     */
    private void disposeAllPreviews() {
        for (AuctionPreviewController c : previewControllers) {
            c.dispose();
        }
        previewControllers.clear();
        if (currentDetailController != null) {
            currentDetailController.dispose();
            currentDetailController = null;
        }
    }
}
