package com.auction.manager;

import com.auction.auction.Auction;
import com.auction.auction.AuctionState;
import com.auction.dao.AuctionDAO;
import com.auction.dao.BidDAO;
import com.auction.factory.ItemFactory;
import com.auction.model.BidTransaction;
import com.auction.model.Bidder;
import com.auction.model.Seller;
import com.auction.model.User;
import com.auction.util.SessionManager;

import java.time.LocalDateTime;
import java.util.concurrent.locks.ReentrantLock;

public class AuctionManager {
    private static AuctionManager instance;
    private final ReentrantLock lock = new ReentrantLock();

    private AuctionManager() {
        System.out.println("Auction Manager created");
    }

    public static AuctionManager getInstance() {
        if (instance == null) {
            instance = new AuctionManager();
        } return instance;
    }

    public boolean createAuction(String type, String name, String startPrice, LocalDateTime startTime, LocalDateTime endTime, Seller seller, String description, String image) {
        //CHƯA CHỌN KIỂU SẢN PHẨM
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Please select product type");
        }

        //CHƯA ĐIỀN TÊN
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Please enter product name");
        }

        //GIÁ KHỞI ĐIỂM KHÔNG HỢP LỆ
        Double startingPrice = 0.0;
        try {
            startingPrice = Double.parseDouble(startPrice);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Please enter a valid number");
        }
        if (startingPrice <= 0) throw new IllegalArgumentException("Price must be greater than 0");

        //CHƯA NHẬP NGÀY GIỜ
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Please select dates");
        }

        //NGÀY BẮT ĐẦU KHÔNG HỢP LỆ
        if (startTime.isBefore(LocalDateTime.now().minusSeconds(10))) {
            throw new IllegalArgumentException("Invalid start date");
        }

        //NGÀY KẾT THÚC KHÔNG HỢP LỆ
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("Invalid end date");
        }

        //LỖI NGƯỜI BÁN
        if (seller == null || seller.getId() <= 0) {
            throw new IllegalArgumentException("An error had occur");
        }

        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Please select product image");
        }

        Auction auction = new Auction(ItemFactory.createItem(type, name, startingPrice, startTime, endTime, seller, description, image));

        return AuctionDAO.getInstance().saveAuction(auction);
    }

    public boolean checkBid(Auction auction, double amount) {
        User currentUser = SessionManager.getCurrentUser();

        lock.lock();
        try {
            if ( auction.getState() != AuctionState.RUNNING || auction.getState() == AuctionState.FINISHED || currentUser == null || amount <= auction.getItem().getCurrentPrice()) {
                return false;
            }

            BidTransaction newBid = new BidTransaction(auction.getId(), (Bidder) currentUser, amount, LocalDateTime.now());

            if (BidDAO.getInstance().saveBid(newBid)) {

                auction.getItem().setCurrentPrice(amount);


                auction.setWinningBid(newBid);

                auction.notifyObservers(amount,(Bidder) currentUser);

                return true;
            }

            return false;
        } finally {
            lock.unlock();
        }
    }
}
