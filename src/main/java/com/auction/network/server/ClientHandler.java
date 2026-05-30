package com.auction.network.server;

import com.auction.dao.AuctionDAO;
import com.auction.manager.AuctionManager;
import com.auction.auction.Auction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            this.in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("Lỗi khởi tạo luồng I/O: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            String request;
            while ((request = in.readLine()) != null) {
                System.out.println("Server nhận lệnh: " + request);

                // Giao thức: phân tách bằng dấu ||
                String[] tokens = request.split("\\|\\|");
                String cmd = tokens[0];

                if ("BID".equals(cmd)) {
                    // Cú pháp từ client: BID||auctionId||userId||bidAmount
                    int    auctionId = Integer.parseInt(tokens[1]);
                    int    userId    = Integer.parseInt(tokens[2]);
                    double bidAmount = Double.parseDouble(tokens[3]);

                    //Lấy đối tượng Auction từ DAO theo auctionId
                    Auction auction = AuctionDAO.getInstance().getAuctionById(auctionId);

                    if (auction == null) {
                        sendEvent("ERROR||Phiên đấu giá không tồn tại!");
                        continue;
                    }

                   //Chuyển lệnh đến AuctionManager để xác thực và lưu
                    //         AuctionManager.checkBid() sẽ:
                    //           - Kiểm tra giá hợp lệ (> currentPrice)
                    //           - Gọi BidDAO.saveBid() → lưu xuống DB
                    //           - Cập nhật auction.setWinningBid()
                    //           - Gọi auction.notifyObservers()
                    boolean success = AuctionManager.getInstance().checkBid(auction, userId, bidAmount);

                    if (success) {

                        //Broadcast giá mới đến TẤT CẢ client đang kết nối
                        String serverEvent = "NEW_PRICE||" + auctionId + "||" + bidAmount + "||" + userId;
                        AuctionServer.broadcast(serverEvent);
                    } else {
                        // Chỉ trả lỗi riêng về client gửi sai giá
                        sendEvent("ERROR||Mức giá đặt không hợp lệ hoặc phiên đã đóng!");
                    }
                }
            }
        } catch (SocketException e) {
            System.out.println("Mất kết nối đột ngột với một Client (Connection reset).");
        } catch (IOException e) {
            System.err.println("Lỗi truyền tải dữ liệu: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    public void sendEvent(String event) {
        if (out != null) {
            out.println(event);
        }
    }

    public void sendgitsaEvent(String event) {
        sendEvent(event);
    }

    private void closeConnection() {
        try {
            AuctionServer.removeClient(this);
            if (in     != null) in.close();
            if (out    != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
            System.out.println("Đã dọn dẹp và ngắt kết nối an toàn với Client.");
        } catch (IOException e) {
            System.err.println("Lỗi đóng kết nối: " + e.getMessage());
        }
    }
}