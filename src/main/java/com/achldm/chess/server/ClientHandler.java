package com.achldm.chess.server;

import com.achldm.chess.common.GameMessage;

import java.io.*;
import java.net.Socket;

/**
 * 客户端处理器
 */
public class ClientHandler extends Thread {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private GameServer server;
    private String username;
    
    public ClientHandler(Socket socket, GameServer server) {
        this.socket = socket;
        this.server = server;
        
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void run() {
        try {
            while (!socket.isClosed()) {
                GameMessage message = (GameMessage) in.readObject();
                handleMessage(message);
            }
        } catch (IOException | ClassNotFoundException e) {
            if (!socket.isClosed()) {
                System.out.println("客户端连接异常: " + e.getMessage());
            }
        } finally {
            server.handleClientDisconnect(this);
            closeConnection();
        }
    }
    
    /**
     * 处理接收到的消息
     */
    private void handleMessage(GameMessage message) {
        switch (message.getType()) {
            case LOGIN:
                handleLogin(message);
                break;
                
            case MATCH_REQUEST:
                server.handleMatchRequest(this);
                break;
                
            case MOVE:
                server.handleGameMove(message, this);
                break;
                
            case CHAT:
                message.setUsername(username);
                server.handleChatMessage(message, this);
                break;
                
            case HEARTBEAT:
                // 心跳包处理
                sendMessage(new GameMessage(GameMessage.MessageType.HEARTBEAT));
                break;
                
            default:
                System.out.println("未处理的消息类型: " + message.getType());
                break;
        }
    }
    
    /**
     * 处理登录请求
     */
    private void handleLogin(GameMessage loginMessage) {
        String username = loginMessage.getUsername();
        String password = loginMessage.getContent();
        
        if (server.handleLogin(username, password, this)) {
            GameMessage response = new GameMessage(GameMessage.MessageType.LOGIN_SUCCESS);
            response.setUsername(username);
            sendMessage(response);
        } else {
            GameMessage response = new GameMessage(GameMessage.MessageType.LOGIN_FAILED);
            response.setContent("用户名已存在或登录信息无效");
            sendMessage(response);
        }
    }
    
    /**
     * 发送消息给客户端
     */
    public void sendMessage(GameMessage message) {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            System.out.println("发送消息失败: " + e.getMessage());
            closeConnection();
        }
    }
    
    /**
     * 关闭连接
     */
    private void closeConnection() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Getters and Setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public Socket getSocket() {
        return socket;
    }
}