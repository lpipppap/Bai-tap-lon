package com.auction.dao;

import com.auction.auction.Auction;
import com.auction.model.Item;
import com.auction.util.DBConnection;

import java.sql.*;

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
}
