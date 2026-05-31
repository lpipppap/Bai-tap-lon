package com.auction.factory;

import com.auction.model.Admin;
import com.auction.model.Bidder;
import com.auction.model.Seller;
import com.auction.model.User;

public class UserFactory {
    public static User createUser(String username, String password, String email, String role) {
        switch (role) {
            case "Bidder" : return new Bidder(username, password, email);
            case  "Seller" : return new Seller(username, password, email);
            case "Admin" : return new Admin(username, password, email);
            default: throw new IllegalArgumentException("Invalid user type");
        }
    }
}
