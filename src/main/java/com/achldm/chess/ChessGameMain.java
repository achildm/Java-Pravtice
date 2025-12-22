package com.achldm.chess;

import com.achldm.chess.client.ui.LoginFrame;
import com.achldm.chess.server.GameServer;

import javax.swing.*;

/**
 * 象棋游戏主启动类
 */
public class ChessGameMain {
    
    public static void main(String[] args) {
        // 设置系统外观
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 检查启动参数
        if (args.length > 0 && "server".equals(args[0])) {
            // 启动服务器
            startServer();
        } else {
            // 启动客户端
            startClient();
        }
    }
    
    /**
     * 启动服务器
     */
    private static void startServer() {
        System.out.println("启动象棋游戏服务器...");
        GameServer server = new GameServer();
        server.start();
    }
    
    /**
     * 启动客户端
     */
    private static void startClient() {
        SwingUtilities.invokeLater(() -> {
            System.out.println("启动象棋游戏客户端...");
            new LoginFrame().setVisible(true);
        });
    }
}