package com.achldm.chess.test;

import com.achldm.chess.client.network.GameClient;
import com.achldm.chess.common.GameMessage;

import java.io.IOException;

/**
 * 简单悔棋测试
 */
public class SimpleUndoTest {
    
    public static void main(String[] args) {
        try {
            System.out.println("=== 简单悔棋测试 ===");
            
            GameClient red = new GameClient("localhost", 8888);
            GameClient black = new GameClient("localhost", 8888);
            
            // 登录和匹配
            login(red, "红方");
            Thread.sleep(300);
            login(black, "黑方");
            Thread.sleep(300);
            
            match(red);
            Thread.sleep(300);
            match(black);
            Thread.sleep(1000);
            
            System.out.println("游戏开始");
            
            // 红方移动
            System.out.println("1. 红方移动马");
            move(red, 1, 9, 2, 7);
            Thread.sleep(1000);
            
            // 红方悔棋
            System.out.println("2. 红方请求悔棋");
            undo(red);
            Thread.sleep(500);
            
            System.out.println("3. 黑方同意悔棋");
            undoResponse(black, true);
            Thread.sleep(2000);
            
            System.out.println("悔棋完成，应该回到初始状态，轮到红方");
            
            // 红方重新移动
            System.out.println("4. 红方重新移动车");
            move(red, 0, 9, 0, 8);
            Thread.sleep(1000);
            
            // 黑方移动
            System.out.println("5. 黑方移动马");
            move(black, 1, 0, 2, 2);
            Thread.sleep(1000);
            
            // 黑方悔棋
            System.out.println("6. 黑方请求悔棋");
            undo(black);
            Thread.sleep(500);
            
            System.out.println("7. 红方同意悔棋");
            undoResponse(red, true);
            Thread.sleep(2000);
            
            System.out.println("悔棋完成，应该回到红方移动后的状态，轮到黑方");
            
            System.out.println("测试完成");
            
            Thread.sleep(2000);
            red.close();
            black.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void login(GameClient client, String name) {
        GameMessage msg = new GameMessage(GameMessage.MessageType.LOGIN);
        msg.setUsername(name);
        msg.setContent("pass");
        client.sendMessage(msg);
    }
    
    private static void match(GameClient client) {
        client.sendMessage(new GameMessage(GameMessage.MessageType.MATCH_REQUEST));
    }
    
    private static void move(GameClient client, int fx, int fy, int tx, int ty) {
        GameMessage msg = new GameMessage(GameMessage.MessageType.MOVE);
        msg.setFromX(fx);
        msg.setFromY(fy);
        msg.setToX(tx);
        msg.setToY(ty);
        client.sendMessage(msg);
    }
    
    private static void undo(GameClient client) {
        client.sendMessage(new GameMessage(GameMessage.MessageType.UNDO_REQUEST));
    }
    
    private static void undoResponse(GameClient client, boolean accept) {
        GameMessage msg = new GameMessage(GameMessage.MessageType.UNDO_RESPONSE);
        msg.setAccepted(accept);
        client.sendMessage(msg);
    }
}