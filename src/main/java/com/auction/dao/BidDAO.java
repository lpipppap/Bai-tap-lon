package com.auction.dao;

import com.auction.model.BidTransaction;
import com.auction.model.Bidder;
import com.auction.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BidDAO {
    private static BidDAO instance;

    private BidDAO() {}

    public static BidDAO getInstance() {
        if (instance == null) {
            instance = new BidDAO();
        } return instance;
    }

    /**
     * SAVE BID
     */
    public boolean saveBid(BidTransaction bid) {

        String sql = "INSERT INTO bids (auction_id, bidder_id, bid_amount, bid_time) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, bid.getAuctionId());
            stmt.setInt(2, bid.getBidder().getId());
            stmt.setDouble(3, bid.getBidAmount());
            stmt.setTimestamp(4, Timestamp.valueOf(bid.getBidTime()));

            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();

                if (generatedKeys.next()) {
                    bid.setBidId(generatedKeys.getInt(1));
                    System.out.println("✓ Bid saved with ID: " + bid.getBidId());
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("✗ Error saving bid: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * GET ALL BIDS OF AN AUCTION
     */
    public List<BidTransaction> getBidsByAuctionId(int auctionId) {
        List<BidTransaction> bids = new ArrayList<>();

        String sql = "SELECT * FROM bids WHERE auction_id = ? ORDER BY bid_amount DESC";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, auctionId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int bidId = rs.getInt("bid_id");
                int bidderId = rs.getInt("bidder_id");
                double amount = rs.getDouble("bid_amount");
                LocalDateTime bidTime = rs.getTimestamp("bid_time").toLocalDateTime();

                // lấy bidder từ UserDAO
                Bidder bidder = (Bidder) UserDAO.getInstance().getUserById(bidderId);

                BidTransaction bid = new BidTransaction(auctionId, bidder, amount, bidTime);
                bid.setBidId(bidId);
                bids.add(bid);
            }
        } catch (SQLException e) {
            System.out.println("✗ Error getting bids");
            e.printStackTrace();
        }
        return bids;
    }
    /**
     * GET HIGHEST BID
     */
    public BidTransaction getHighestBid(int auctionId) {
        String sql = "SELECT * FROM bids WHERE auction_id = ? ORDER BY bid_amount DESC LIMIT 1";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, auctionId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int bidId = rs.getInt("bid_id");
                int bidderId = rs.getInt("bidder_id");
                double amount = rs.getDouble("bid_amount");
                LocalDateTime bidTime = rs.getTimestamp("bid_time").toLocalDateTime();
                Bidder bidder = (Bidder) UserDAO.getInstance().getUserById(bidderId);

                BidTransaction highestBid = new BidTransaction(auctionId, bidder, amount, bidTime);

                highestBid.setBidId(bidId);

                return highestBid;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}