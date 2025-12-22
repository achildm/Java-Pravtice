package com.achldm.chess;

import com.achldm.chess.client.ui.LoginFrame;
import com.achldm.chess.server.GameServer;

import javax.swing.*;

/**
 * 象棋游戏主启动类
 */
public class ChessGameServer {
    
    public static void main(String[] args) {
        // 设置系统外观
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 检查启动参数
//        if (args.length > 0) {
//            String mode = args[0].toLowerCase();
//            switch (mode) {
//                case "server":
                    // 启动服务器
                    startServer();
//                    break;
//                case "client":
                    // 启动单个客户端
//                    startClient();
//                    break;
//                default:
//                    System.out.println("未知参数: " + args[0]);
//                    printUsage();
//                    break;
//            }
//        } else {
            // 默认启动单个客户端
//            startClient();
//        }
    }
    
    /**
     * 打印使用说明
     */
    private static void printUsage() {
        System.out.println("使用方法:");
        System.out.println("  java ChessGameMain server  - 启动服务器");
        System.out.println("  java ChessGameMain client  - 启动客户端");
        System.out.println("  java ChessGameMain test    - 启动两个客户端用于测试");
        System.out.println("  java ChessGameMain         - 默认启动客户端");
    }
    
    /**
     * 启动服务器
     */
    private static void startServer() {
        System.out.println("启动象棋游戏服务器...");
        try {
            GameServer server = new GameServer();
            server.start();
        } catch (Exception e) {
            System.err.println("服务器启动失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 启动单个客户端
     */
    private static void startClient() {
        SwingUtilities.invokeLater(() -> {
            System.out.println("启动象棋游戏客户端...");
            new LoginFrame().setVisible(true);
        });
    }
}