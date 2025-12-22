package com.achldm.chess.server;

import com.achldm.chess.common.GameMessage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 游戏服务器
 */
public class GameServer {
    private static final int PORT = 8888;
    
    private ServerSocket serverSocket;
    private ConcurrentHashMap<String, ClientHandler> clients = new ConcurrentHashMap<>();
    private CopyOnWriteArrayList<ClientHandler> waitingClients = new CopyOnWriteArrayList<>();
    private ConcurrentHashMap<String, GameRoom> gameRooms = new ConcurrentHashMap<>();
    
    public GameServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("游戏服务器启动，监听端口: " + PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void start() {
        while (!serverSocket.isClosed()) {
            try {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clientHandler.start();
                System.out.println("新客户端连接: " + clientSocket.getInetAddress());
            } catch (IOException e) {
                if (!serverSocket.isClosed()) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * 处理客户端登录
     */
    public synchronized boolean handleLogin(String username, String password, ClientHandler client) {
        // 简单的登录验证（实际项目中应该连接数据库）
        if (username != null && !username.trim().isEmpty() && !clients.containsKey(username)) {
            clients.put(username, client);
            client.setUsername(username);
            return true;
        }
        return false;
    }
    
    /**
     * 处理匹配请求
     */
    public synchronized void handleMatchRequest(ClientHandler client) {
        if (!waitingClients.contains(client)) {
            waitingClients.add(client);
            
            // 如果有两个或以上等待的客户端，进行匹配
            if (waitingClients.size() >= 2) {
                ClientHandler player1 = waitingClients.remove(0);
                ClientHandler player2 = waitingClients.remove(0);
                
                // 创建游戏房间
                String roomId = "room_" + System.currentTimeMillis();
                GameRoom room = new GameRoom(roomId, player1, player2);
                gameRooms.put(roomId, room);
                
                // 通知客户端匹配成功
                GameMessage matchMsg1 = new GameMessage(GameMessage.MessageType.MATCH_FOUND);
                matchMsg1.setRed(true);  // 第一个玩家是红方
                player1.sendMessage(matchMsg1);
                
                GameMessage matchMsg2 = new GameMessage(GameMessage.MessageType.MATCH_FOUND);
                matchMsg2.setRed(false); // 第二个玩家是黑方
                player2.sendMessage(matchMsg2);
                
                System.out.println("匹配成功: " + player1.getUsername() + " vs " + player2.getUsername());
            }
        }
    }
    
    /**
     * 处理游戏移动
     */
    public void handleGameMove(GameMessage moveMessage, ClientHandler sender) {
        // 找到发送者所在的游戏房间
        for (GameRoom room : gameRooms.values()) {
            if (room.containsPlayer(sender)) {
                room.handleMove(moveMessage, sender);
                break;
            }
        }
    }
    
    /**
     * 处理聊天消息
     */
    public void handleChatMessage(GameMessage chatMessage, ClientHandler sender) {
        // 广播聊天消息给所有在线客户端
        for (ClientHandler client : clients.values()) {
            if (client != sender) {
                client.sendMessage(chatMessage);
            }
        }
    }
    
    /**
     * 客户端断开连接
     */
    public synchronized void handleClientDisconnect(ClientHandler client) {
        String username = client.getUsername();
        if (username != null) {
            clients.remove(username);
            waitingClients.remove(client);
            
            // 从游戏房间中移除
            for (GameRoom room : gameRooms.values()) {
                if (room.containsPlayer(client)) {
                    room.handlePlayerDisconnect(client);
                    gameRooms.remove(room.getRoomId());
                    break;
                }
            }
            
            System.out.println("客户端断开连接: " + username);
        }
    }
    
    public static void main(String[] args) {
        GameServer server = new GameServer();
        server.start();
    }
}