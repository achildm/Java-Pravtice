package com.achldm.chess.client.network;

import com.achldm.chess.client.ui.GameFrame;
import com.achldm.chess.client.ui.LobbyFrame;
import com.achldm.chess.client.ui.LoginFrame;
import com.achldm.chess.common.GameMessage;

import java.io.*;
import java.net.Socket;

/**
 * 游戏客户端网络通信类
 */
public class GameClient {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private MessageHandler messageHandler;
    
    private LoginFrame loginFrame;
    private LobbyFrame lobbyFrame;
    private GameFrame gameFrame;
    
    public GameClient(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        
        messageHandler = new MessageHandler();
        messageHandler.start();
    }
    
    /**
     * 发送消息到服务器
     */
    public void sendMessage(GameMessage message) {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 消息处理线程
     */
    private class MessageHandler extends Thread {
        @Override
        public void run() {
            try {
                while (!socket.isClosed()) {
                    GameMessage message = (GameMessage) in.readObject();
                    handleMessage(message);
                }
            } catch (IOException | ClassNotFoundException e) {
                if (!socket.isClosed()) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * 处理接收到的消息
     */
    private void handleMessage(GameMessage message) {
        switch (message.getType()) {
            case LOGIN_SUCCESS:
                if (loginFrame != null) {
                    loginFrame.onLoginSuccess();
                }
                break;
                
            case LOGIN_FAILED:
                if (loginFrame != null) {
                    loginFrame.onLoginFailed(message.getContent());
                }
                break;
                
            case MATCH_FOUND:
                if (lobbyFrame != null) {
                    lobbyFrame.onMatchFound(message.isRed());
                }
                break;
                
            case GAME_START:
                // 游戏开始的额外处理
                break;
                
            case MOVE:
                if (gameFrame != null) {
                    gameFrame.onOpponentMove(message.getFromX(), message.getFromY(), 
                                           message.getToX(), message.getToY());
                }
                break;
                
            case GAME_OVER:
                if (gameFrame != null) {
                    gameFrame.onGameOver(message.isRed());
                }
                break;
                
            case CHAT:
                if (lobbyFrame != null) {
                    lobbyFrame.onChatMessage(message.getUsername(), message.getContent());
                }
                break;
                
            default:
                System.out.println("未处理的消息类型: " + message.getType());
                break;
        }
    }
    
    /**
     * 关闭连接
     */
    public void close() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Setters for UI frames
    public void setLoginFrame(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
    }
    
    public void setLobbyFrame(LobbyFrame lobbyFrame) {
        this.lobbyFrame = lobbyFrame;
    }
    
    public void setGameFrame(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
    }
}