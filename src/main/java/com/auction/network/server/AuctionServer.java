package com.auction.network.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AuctionServer {
    private static final int PORT = 5000;

    // Danh sách quản lý tập trung tất cả các client đang trực tuyến để thực hiện broadcast
    private static List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("=== SERVER ĐẤU GIÁ ĐÃ SẴN SÀNG TRÊN CỔNG " + PORT + " ===");

            while (true) {
                // Lắng nghe kết nối từ Client
                Socket socket = serverSocket.accept();
                System.out.println("Có client mới kết nối từ địa chỉ: " + socket.getInetAddress());

                // Tạo một Handler riêng độc lập chạy đa luồng cho Client này
                ClientHandler handler = new ClientHandler(socket);
                clients.add(handler);

                Thread thread = new Thread(handler);
                thread.start();
            }
        } catch (IOException e) {
            System.err.println("Lỗi nghiêm trọng khi khởi chạy Server: " + e.getMessage());
        }
    }

    // Hàm thực hiện bước AuctionManager.broadcast(ServerEvent) gửi tin nhắn tới mọi client
    public static void broadcast(String serverEvent) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.sendgitsaEvent(serverEvent); // Đẩy thông điệp qua mạng về từng máy khách
            }
        }
    }

    public static void removeClient(ClientHandler client) {
        clients.remove(client);
    }
}