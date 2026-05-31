package com.auction.network.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * AuctionClient — chạy phía CLIENT (JavaFX).
 *
 * Nhiệm vụ:
 *  1. Kết nối TCP đến AuctionServer (port 5000).
 *  2. Gửi lệnh BID||auctionId||userId||amount lên server.
 *  3. Lắng nghe phản hồi từ server trên một thread riêng,
 *     rồi gọi callback để cập nhật giao diện.
 *
 * Singleton — toàn bộ ứng dụng dùng chung một kết nối.
 */
public class AuctionClient {

    private static final String HOST = "localhost";
    private static final int    PORT = 5000;

    private static AuctionClient instance;

    private Socket       socket;
    private PrintWriter  out;
    private BufferedReader in;

    /** Callback nhận sự kiện từ server — PlaceBidController đăng ký vào đây */
    private ServerEventListener listener;

    // -------------------------------------------------------------------------
    // Singleton
    // -------------------------------------------------------------------------

    private AuctionClient() {}

    public static AuctionClient getInstance() {
        if (instance == null) {
            instance = new AuctionClient();
        }
        return instance;
    }

    // -------------------------------------------------------------------------
    // Kết nối / ngắt kết nối
    // -------------------------------------------------------------------------

    /**
     * Mở kết nối TCP đến server và khởi động thread lắng nghe.
     * Gọi một lần khi ứng dụng khởi động (ví dụ: trong Launcher).
     */
    public void connect() throws IOException {
        socket = new Socket(HOST, PORT);
        out    = new PrintWriter(socket.getOutputStream(), true);
        in     = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        System.out.println("[AuctionClient] Đã kết nối đến server " + HOST + ":" + PORT);

        // Thread riêng lắng nghe phản hồi từ server
        Thread listenerThread = new Thread(this::listenLoop, "auction-client-listener");
        listenerThread.setDaemon(true); // Tự tắt khi đóng app
        listenerThread.start();
    }

    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("[AuctionClient] Lỗi đóng kết nối: " + e.getMessage());
        }
    }

    // Gửi lệnh đặt giá lên server

    /**
     * Gửi lệnh BID theo giao thức: BID||auctionId||userId||bidAmount
     *
     * Được gọi bởi PlaceBidController.placeBidAction()
     */
    public void sendBid(int auctionId, int userId, double bidAmount) {
        if (out == null) {
            System.err.println("[AuctionClient] Chưa kết nối đến server!");
            return;
        }
        String command = "BID||" + auctionId + "||" + userId + "||" + bidAmount;
        out.println(command);
        System.out.println("[AuctionClient] Đã gửi: " + command);
    }

    // Lắng nghe phản hồi từ server (chạy trong thread riêng)

    private void listenLoop() {
        try {
            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                System.out.println("[AuctionClient] Nhận từ server: " + serverMessage);
                handleServerMessage(serverMessage);
            }
        } catch (IOException e) {
            System.out.println("[AuctionClient] Mất kết nối với server.");
        }
    }

    /**
     * Phân tích gói tin từ server và gọi callback để cập nhật UI.
     *
     * Giao thức server gửi về:
     *   NEW_PRICE||auctionId||newPrice||userId  — có giá mới
     *   ERROR||message                          — lỗi
     */
    private void handleServerMessage(String message) {
        if (listener == null) return;

        String[] tokens = message.split("\\|\\|");
        String cmd = tokens[0];

        // Chạy callback trên JavaFX Application Thread
        javafx.application.Platform.runLater(() -> {
            switch (cmd) {
                case "NEW_PRICE" -> {
                    // NEW_PRICE||auctionId||newPrice||userId
                    int    auctionId = Integer.parseInt(tokens[1]);
                    double newPrice  = Double.parseDouble(tokens[2]);
                    String bidderId  = tokens[3];
                    listener.onNewPrice(auctionId, newPrice, bidderId);
                }
                case "ERROR" -> {
                    String errorMsg = tokens.length > 1 ? tokens[1] : "Lỗi không xác định";
                    listener.onError(errorMsg);
                }
                default -> System.out.println("[AuctionClient] Lệnh lạ từ server: " + cmd);
            }
        });
    }

    // Callback interface

    public void setListener(ServerEventListener listener) {
        this.listener = listener;
    }

    /**
     * Interface để PlaceBidController (hoặc bất kỳ Controller nào) nhận sự kiện từ server.
     */
    public interface ServerEventListener {
        /** Server broadcast giá mới thành công */
        void onNewPrice(int auctionId, double newPrice, String bidderId);

        /** Server trả về lỗi (giá không hợp lệ, phiên đóng...) */
        void onError(String message);
    }
}