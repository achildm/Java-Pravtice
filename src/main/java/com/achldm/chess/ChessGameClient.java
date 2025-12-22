package com.achldm.chess;

import com.achldm.chess.client.ui.LoginFrame;
import com.achldm.chess.server.GameServer;

import javax.swing.*;

public class ChessGameClient {
    public static void main(String[] args) {
        // 设置系统外观
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        startClient();
    }

    /**
     * 启动两个客户端
     */
    private static void startClient() {
        SwingUtilities.invokeLater(() -> {
            System.out.println("启动象棋游戏客户端...");
            new LoginFrame().setVisible(true);
        });

        SwingUtilities.invokeLater(() -> {
            System.out.println("启动象棋游戏客户端...");
            new LoginFrame().setVisible(true);
        });
    }
}
