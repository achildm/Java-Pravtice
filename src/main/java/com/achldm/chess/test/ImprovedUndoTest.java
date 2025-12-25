package com.achldm.chess.test;

import com.achldm.chess.client.network.GameClient;
import com.achldm.chess.common.GameMessage;

import java.io.IOException;

/**
 * 改进的悔棋功能测试
 */
public class ImprovedUndoTest {
    
    public static void main(String[] args) {
        try {
            System.out.println("=== 改进的悔棋功能测试开始 ===");
            
            // 创建两个客户端模拟对战
            GameClient client1 = new GameClient("localhost", 8888);
            GameClient client2 = new GameClient("localhost", 8888);
            
            // 客户端1登录（红方）
            testLogin(client1, "红方玩家");
            Thread.sleep(500);
            
            // 客户端2登录（黑方）
            testLogin(client2, "黑方玩家");
            Thread.sleep(500);
            
            // 客户端1请求匹配
            testMatchRequest(client1);
            Thread.sleep(500);
            
            // 客户端2请求匹配
            testMatchRequest(client2);
            Thread.sleep(1000);
            
            System.out.println("游戏开始，红方先行");
            
            // 红方移动一步（客户端1）
            System.out.println("红方移动：马 (1,9) -> (2,7)");
            testMove(client1, 1, 9, 2, 7);
            Thread.sleep(1000);
            
            // 现在红方刚下完棋，应该可以悔棋
            System.out.println("红方请求悔棋（应该可以）");
            testUndoRequest(client1);
            Thread.sleep(500);
            
            // 黑方同意悔棋
            System.out.println("黑方同意悔棋");
            testUndoResponse(client2, true, null);
            Thread.sleep(2000);
            
            // 再次测试：红方移动
            System.out.println("红方再次移动：车 (0,9) -> (0,8)");
            testMove(client1, 0, 9, 0, 8);
            Thread.sleep(1000);
            
            // 黑方移动
            System.out.println("黑方移动：马 (1,0) -> (2,2)");
            testMove(client2, 1, 0, 2, 2);
            Thread.sleep(1000);
            
            // 现在黑方刚下完棋，黑方应该可以悔棋
            System.out.println("黑方请求悔棋（应该可以）");
            testUndoRequest(client2);
            Thread.sleep(500);
            
            // 红方拒绝悔棋
            System.out.println("红方拒绝悔棋");
            testUndoResponse(client1, false, "不同意悔棋");
            Thread.sleep(1000);
            
            // 测试红方尝试悔棋（应该不能，因为不是刚下完棋的一方）
            System.out.println("红方尝试悔棋（应该被拒绝，因为不是刚下完棋的一方）");
            testUndoRequest(client1);
            Thread.sleep(1000);
            
            System.out.println("=== 改进的悔棋功能测试完成 ===");
            
            // 保持连接一段时间观察结果
            Thread.sleep(3000);
            
            client1.close();
            client2.close();
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private static void testLogin(GameClient client, String username) {
        System.out.println("登录: " + username);
        GameMessage loginMsg = new GameMessage(GameMessage.MessageType.LOGIN);
        loginMsg.setUsername(username);
        loginMsg.setContent("password");
        client.sendMessage(loginMsg);
    }
    
    private static void testMatchRequest(GameClient client) {
        System.out.println("请求匹配");
        GameMessage matchMsg = new GameMessage(GameMessage.MessageType.MATCH_REQUEST);
        client.sendMessage(matchMsg);
    }
    
    private static void testMove(GameClient client, int fromX, int fromY, int toX, int toY) {
        System.out.println(String.format("移动棋子: (%d,%d) -> (%d,%d)", fromX, fromY, toX, toY));
        GameMessage moveMsg = new GameMessage(GameMessage.MessageType.MOVE);
        moveMsg.setFromX(fromX);
        moveMsg.setFromY(fromY);
        moveMsg.setToX(toX);
        moveMsg.setToY(toY);
        client.sendMessage(moveMsg);
    }
    
    private static void testUndoRequest(GameClient client) {
        System.out.println("请求悔棋");
        GameMessage undoMsg = new GameMessage(GameMessage.MessageType.UNDO_REQUEST);
        client.sendMessage(undoMsg);
    }
    
    private static void testUndoResponse(GameClient client, boolean accepted, String reason) {
        System.out.println("悔棋回应: " + (accepted ? "同意" : "拒绝") + 
                          (reason != null ? " - " + reason : ""));
        GameMessage response = new GameMessage(GameMessage.MessageType.UNDO_RESPONSE);
        response.setAccepted(accepted);
        if (reason != null) {
            response.setReason(reason);
        }
        client.sendMessage(response);
    }
}