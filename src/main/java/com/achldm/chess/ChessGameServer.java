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

        // 启动服务器
        startServer();
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
}