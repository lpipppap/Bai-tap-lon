package com.auction.manager;

import com.auction.factory.UserFactory;
import com.auction.model.User;
import com.auction.dao.*;

public class UserManager {
    private static UserManager instance;

    private UserManager() {
        System.out.println("The auction management system has been initiated.");
    }

    public static UserManager getInstance() {
        if (instance == null)
            instance = new UserManager();
        return instance;
    }

    //ĐĂNG KÝ USER
    public boolean registerUser(String username, String password, String cafe, String email, String role) {
        //CHƯA CHỌN ROLE
        if (role == null || role.isEmpty()) {
            throw new IllegalArgumentException("Please select 1 role");
        }

        //USERNAME TRỐNG
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Please enter username");
        }

        //TRÙNG USERNAME
        if (UserDAO.getInstance().userExists(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

        //EMAIL KHÔNG HỢP LỆ
        if (email == null || email.trim().isEmpty() || !email.endsWith("@gmail.com") || email.trim().length() <= 10) {
            throw new IllegalArgumentException("Invalid email");
        }

        //TRÙNG EMAIL
        if (UserDAO.getInstance().emailExists(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        //MẬT KHẨU KHÔNG HỢP LỆ
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be longer than 8 characters");
        }
        if (!password.matches(".*[A-Z].*") || !password.matches(".*[a-z].*") || !password.matches(".*[0-9].*")) {
            throw new IllegalArgumentException("Password must contains uppercase, lowercase letters and numbers");
        }
        if (!password.equals(cafe)) {
            throw new IllegalArgumentException("Unmatched password");
        }

        User newUser = UserFactory.createUser(username, password, email, role);

        return UserDAO.getInstance().saveUser(newUser);
    }

    //ĐĂNG NHẬP USER
    public User loginUser(String email, String password, String role) {
        //CHƯA CHỌN ROLE
        if (role == null || role.isEmpty()) {
            throw new IllegalArgumentException("Please select 1 role");
        }
        //EMAIL TRỐNG
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Please enter email");
        }
        //PASSWORD TRỐNG
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Please enter password");
        }

        User user = UserDAO.getInstance().getUserByEmailAndPasswordAndRole(email, password, role);

        if (user == null) {
            throw new IllegalArgumentException("Wrong email or password or role");
        }

        return user;
    }
}