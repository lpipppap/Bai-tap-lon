package com.auction.dao;

import com.auction.auction.Auction;
import com.auction.auction.AuctionState;
import com.auction.factory.ItemFactory;
import com.auction.model.*;
import com.auction.util.DBConnection;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuctionDAO {
    private static AuctionDAO instance;

    private AuctionDAO() {}

    public static AuctionDAO getInstance() {
        if (instance == null) {
            instance = new AuctionDAO();
        } return instance;
    }

    public boolean saveAuction(Auction auction) {
        Item item = auction.getItem();
        String itemSql = "INSERT INTO items (name, description, item_type, start_price, current_price, seller_id, start_time, end_time, image) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String auctionSql = "INSERT INTO auctions (item_id, status) VALUES (?, 'OPEN')";

        try (Connection connection = DBConnection.getConnection();
            PreparedStatement itemPs = connection.prepareStatement(itemSql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            itemPs.setString(1, item.getName());
            itemPs.setString(2, item.getDescription());
            itemPs.setString(3, item.getClass().getSimpleName());
            itemPs.setDouble(4, item.getStartPrice());
            itemPs.setDouble(5, item.getCurrentPrice());
            itemPs.setInt(6, item.getSeller().getId());
            itemPs.setTimestamp(7, java.sql.Timestamp.valueOf(item.getStartTime()));
            itemPs.setTimestamp(8, java.sql.Timestamp.valueOf(item.getEndTime()));
            itemPs.setString(9, item.getImage());

            int itemRowInserted = itemPs.executeUpdate();

            int itemId = 0;
            if (itemRowInserted > 0) {
                try (ResultSet resultSet = itemPs.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        itemId = resultSet.getInt(1);
                        item.setId(itemId);
                    }
                }
            }

            try (PreparedStatement auctionPs = connection.prepareStatement(auctionSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                auctionPs.setInt(1, itemId);

                int auctionRowInserted = auctionPs.executeUpdate();

                int auctionId = 0;
                if (auctionRowInserted > 0) {
                    try (ResultSet resultSet = auctionPs.getGeneratedKeys()) {
                        if (resultSet.next()) {
                            auctionId = resultSet.getInt(1);
                            auction.setId(auctionId);
                            return true;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error creating auction" + e.getLocalizedMessage());
            e.printStackTrace();
        } return false;
    }

    public boolean updateStatus(int auctionId, String status) {
        String sql = "UPDATE auctions SET status = ? WHERE auction_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, auctionId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Error updating auction status: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        return false;
    }

    public java.util.List<Auction> getAllAuctions() {
        java.util.List<Auction> list = new java.util.ArrayList<>();

        String sql = "SELECT a.auction_id, a.status, a.winner_id, u2.username AS winner_name, " +
                "i.item_id, i.name, i.description, i.item_type, i.start_price, i.current_price, " +
                "i.seller_id, u1.username AS seller_name, i.start_time, i.end_time, i.image " +
                "FROM auctions a " +
                "JOIN items i ON a.item_id = i.item_id " +
                "JOIN users u1 ON i.seller_id = u1.user_id " +
                "LEFT JOIN users u2 ON a.winner_id = u2.user_id";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int sellerId = rs.getInt("seller_id");
                String sellerName = rs.getString("seller_name");

                Seller seller = new Seller(sellerName, "", "");
                seller.setId(sellerId);

                Item item = ItemFactory.createItem(
                        rs.getString("item_type"),
                        rs.getString("name"),
                        rs.getDouble("start_price"),
                        rs.getTimestamp("start_time").toLocalDateTime(),
                        rs.getTimestamp("end_time").toLocalDateTime(),
                        seller,
                        rs.getString("description"),
                        rs.getString("image")
                );
                item.setId(rs.getInt("item_id"));
                item.setCurrentPrice(rs.getDouble("current_price"));

                Auction auction = new Auction(item);
                auction.setId(rs.getInt("auction_id"));

                String dbStatus = rs.getString("status");
                if (dbStatus != null) {
                    auction.setState(com.auction.auction.AuctionState.valueOf(dbStatus.toUpperCase().trim()));
                }

                int winnerId = rs.getInt("winner_id");
                if (!rs.wasNull()) {
                    String winnerName = rs.getString("winner_name");

                    Bidder winnerUser = new Bidder(winnerName, "", "");
                    winnerUser.setId(winnerId);

                    BidTransaction winBid = new BidTransaction(
                            auction.getId(),
                            winnerUser,
                            rs.getDouble("current_price"),
                            rs.getTimestamp("end_time").toLocalDateTime()
                    );

                    auction.setWinningBid(winBid);
                } else {
                    auction.setWinningBid(null);
                }

                list.add(auction);
            }
        } catch (SQLException e) {
            System.out.println("Error getting all auctions: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        return list;
    }

    public Auction getAuctionById(int auctionId) {
        String sql = "SELECT a.auction_id, a.status, a.winner_id, u2.username AS winner_name, " +
                "i.item_id, i.name, i.description, i.item_type, i.start_price, i.current_price, " +
                "i.seller_id, u1.username AS seller_name, i.start_time, i.end_time, i.image " +
                "FROM auctions a " +
                "JOIN items i ON a.item_id = i.item_id " +
                "JOIN users u1 ON i.seller_id = u1.user_id " +
                "LEFT JOIN users u2 ON a.winner_id = u2.user_id " +
                "WHERE a.auction_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, auctionId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int sellerId = rs.getInt("seller_id");
                    String sellerName = rs.getString("seller_name");

                    Seller seller = new Seller(sellerName, "", "");
                    seller.setId(sellerId);

                    Item item = ItemFactory.createItem(
                            rs.getString("item_type"),
                            rs.getString("name"),
                            rs.getDouble("start_price"),
                            rs.getTimestamp("start_time").toLocalDateTime(),
                            rs.getTimestamp("end_time").toLocalDateTime(),
                            seller,
                            rs.getString("description"),
                            rs.getString("image")
                    );
                    item.setId(rs.getInt("item_id"));
                    item.setCurrentPrice(rs.getDouble("current_price"));

                    Auction auction = new Auction(item);

                    auction.setId(rs.getInt("auction_id"));

                    String dbStatus = rs.getString("status");
                    if (dbStatus != null) {
                        auction.setState(com.auction.auction.AuctionState.valueOf(dbStatus.toUpperCase().trim()));
                    }

                    LocalDateTime now = LocalDateTime.now();
                    if (auction.getState() == AuctionState.OPEN
                            && now.isAfter(item.getStartTime())
                            && now.isBefore(item.getEndTime())) {
                        auction.startAuction(); // OPEN → RUNNING
                    } else if (now.isAfter(item.getEndTime())) {
                        // Đảm bảo FINISHED nếu đã hết giờ
                        if (auction.getState() != AuctionState.FINISHED) {
                            auction.startAuction();
                            auction.finishAuction();
                        }
                    }
                    int winnerId = rs.getInt("winner_id");
                    if (!rs.wasNull()) {
                        String winnerName = rs.getString("winner_name");
                        Bidder winnerUser = new Bidder(winnerName, "", "");
                        winnerUser.setId(winnerId);

                        BidTransaction winBid = new BidTransaction(
                                auction.getId(),
                                winnerUser,
                                rs.getDouble("current_price"),
                                rs.getTimestamp("end_time").toLocalDateTime()
                        );
                        auction.setWinningBid(winBid);
                    } else {
                        auction.setWinningBid(null);
                    }

                    return auction;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting auction by id: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateWinner(int auctionId, int winnerId) {
        String sql = "UPDATE auctions SET winner_id = ? WHERE auction_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, winnerId);
            ps.setInt(2, auctionId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Error updating winner: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        return false;
    }

    public double getCurrentPrice(int auctionId) {
        String sql = "SELECT current_price FROM items " +
                "WHERE item_id = (SELECT item_id FROM auctions WHERE auction_id = ?)";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, auctionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getDouble("current_price");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}
