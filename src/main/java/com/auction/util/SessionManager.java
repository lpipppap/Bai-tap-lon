package com.auction.util;

import com.auction.model.Bidder;
import com.auction.model.Seller;
import com.auction.model.User;

public class SessionManager {
    private static User currentUser;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void logout() {
        currentUser = null;
    }

    public static String getCurrentUserRole() {
        if (currentUser instanceof Bidder) {
            return "Bidder";
        } else if (currentUser instanceof Seller) {
            return "Seller";
        } return "Admin";
    }
}
