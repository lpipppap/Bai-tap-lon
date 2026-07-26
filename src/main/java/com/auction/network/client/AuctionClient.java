package com.auction.network.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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

    private static final String HOST = "10.11.220.210";
    private static final int    PORT = 5000;

    private static volatile AuctionClient instance;

    private Socket       socket;
    private PrintWriter  out;
    private BufferedReader in;

    /** Callback nhận sự kiện từ server — PlaceBidController đăng ký vào đây */
    private final List<ServerEventListener> listeners = new CopyOnWriteArrayList<>();

    // -------------------------------------------------------------------------
    // Singleton
    // -------------------------------------------------------------------------

    public static AuctionClient getInstance() {
        if (instance == null) {
            synchronized (AuctionClient.class) {
                if (instance == null) instance = new AuctionClient();
            }
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

    /** Đăng ký listener — gọi trong initialize() của controller. */
    public void addListener(ServerEventListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
            System.out.println("[AuctionClient] Đã thêm listener: " + listener.getClass().getSimpleName());
        }
    }

    /** Huỷ đăng ký listener — gọi khi controller rời màn hình. */
    public void removeListener(ServerEventListener listener) {
        listeners.remove(listener);
        System.out.println("[AuctionClient] Đã xoá listener: "
                + (listener != null ? listener.getClass().getSimpleName() : "null"));
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
        if (listeners.isEmpty()) return;

        String[] tokens = message.split("\\|\\|");
        String cmd = tokens[0];

        // Chạy callback trên JavaFX Application Thread để update UI an toàn
        javafx.application.Platform.runLater(() -> {
            switch (cmd) {
                case "NEW_PRICE" -> {
                    // NEW_PRICE||auctionId||newPrice||userId
                    int    auctionId = Integer.parseInt(tokens[1]);
                    double newPrice  = Double.parseDouble(tokens[2]);
                    String bidderId  = tokens[3];
                    for (ServerEventListener l : listeners) {
                        l.onNewPrice(auctionId, newPrice, bidderId);
                    }
                }
                case "AUCTION_ENDED" -> {
                    // AUCTION_ENDED||auctionId
                    int auctionId = Integer.parseInt(tokens[1]);
                    for (ServerEventListener l : listeners) {
                        l.onAuctionEnded(auctionId);
                    }
                }
                case "ERROR" -> {
                    // ERROR chỉ gửi đến client gửi giá sai — vẫn broadcast vì
                    // chỉ PlaceBidController đang active mới gọi sendBid()
                    String errorMsg = tokens.length > 1 ? tokens[1] : "Lỗi không xác định";
                    for (ServerEventListener l : listeners) {
                        l.onError(errorMsg);
                    }
                }
                default -> System.out.println("[AuctionClient] Lệnh lạ từ server: " + cmd);
            }
        });
    }

    // Callback interface

    /**
     * Interface cho mọi controller muốn nhận sự kiện real-time từ server.
     *
     * Cách dùng:
     *   - Controller implements ServerEventListener
     *   - Trong initialize():    AuctionClient.getInstance().addListener(this)
     *   - Khi rời màn hình:      AuctionClient.getInstance().removeListener(this)
     */
    public interface ServerEventListener {
        /** Server broadcast giá mới thành công */
        void onNewPrice(int auctionId, double newPrice, String bidderId);

        /** Server thông báo phiên đấu giá kết thúc */
        void onAuctionEnded(int auctionId);

        /** Server trả về lỗi (giá không hợp lệ, phiên đóng...) */
        default void onError(String message) {};
    }
}