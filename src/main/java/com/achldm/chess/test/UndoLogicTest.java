package com.achldm.chess.test;

import com.achldm.chess.client.network.GameClient;
import com.achldm.chess.common.GameMessage;

import java.io.IOException;

/**
 * 悔棋逻辑详细测试
 */
public class UndoLogicTest {
    
    public static void main(String[] args) {
        try {
            System.out.println("=== 悔棋逻辑详细测试 ===");
            
            // 创建两个客户端模拟对战
            GameClient client1 = new GameClient("localhost", 8888);
            GameClient client2 = new GameClient("localhost", 8888);
            
            // 客户端1登录（红方）
            testLogin(client1, "红方");
            Thread.sleep(500);
            
            // 客户端2登录（黑方）
            testLogin(client2, "黑方");
            Thread.sleep(500);
            
            // 匹配
            testMatchRequest(client1);
            Thread.sleep(500);
            testMatchRequest(client2);
            Thread.sleep(1000);
            
            System.out.println("\n=== 游戏开始，初始状态 ===");
            System.out.println("初始状态：红方先行");
            
            System.out.println("\n=== 第1步：红方移动 ===");
            System.out.println("红方移动：马 (1,9) -> (2,7)");
            testMove(client1, 1, 9, 2, 7);
            Thread.sleep(1000);
            System.out.println("现在状态：轮到黑方，红方可以悔棋");
            
            System.out.println("\n=== 第2步：黑方移动 ===");
            System.out.println("黑方移动：马 (1,0) -> (2,2)");
            testMove(client2, 1, 0, 2, 2);
            Thread.sleep(1000);
            System.out.println("现在状态：轮到红方，黑方可以悔棋");
            
            System.out.println("\n=== 测试悔棋：黑方悔棋 ===");
            System.out.println("黑方请求悔棋（应该回到黑方移动前的状态，即红方移动后的状态）");
            testUndoRequest(client2);
            Thread.sleep(500);
            
            System.out.println("红方同意悔棋");
            testUndoResponse(client1, true, null);
            Thread.sleep(2000);
            
            System.out.println("悔棋后状态：应该回到红方移动后、黑方移动前的状态");
            System.out.println("现在应该轮到黑方重新下棋");
            
            System.out.println("\n=== 继续游戏 ===");
            System.out.println("黑方重新移动：炮 (1,2) -> (1,5)");
            testMove(client2, 1, 2, 1, 5);
            Thread.sleep(1000);
            
            System.out.println("红方移动：车 (0,9) -> (0,8)");
            testMove(client1, 0, 9, 0, 8);
            Thread.sleep(1000);
            
            System.out.println("\n=== 测试悔棋：红方悔棋 ===");
            System.out.println("红方请求悔棋（应该回到红方移动前的状态）");
            testUndoRequest(client1);
            Thread.sleep(500);
            
            System.out.println("黑方同意悔棋");
            testUndoResponse(client2, true, null);
            Thread.sleep(2000);
            
            System.out.println("悔棋后状态：应该回到红方移动前的状态");
            System.out.println("现在应该轮到红方重新下棋");
            
            System.out.println("\n=== 悔棋逻辑测试完成 ===");
            
            Thread.sleep(3000);
            
            client1.close();
            client2.close();
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private static void testLogin(GameClient client, String username) {
        GameMessage loginMsg = new GameMessage(GameMessage.MessageType.LOGIN);
        loginMsg.setUsername(username);
        loginMsg.setContent("password");
        client.sendMessage(loginMsg);
    }
    
    private static void testMatchRequest(GameClient client) {
        GameMessage matchMsg = new GameMessage(GameMessage.MessageType.MATCH_REQUEST);
        client.sendMessage(matchMsg);
    }
    
    private static void testMove(GameClient client, int fromX, int fromY, int toX, int toY) {
        GameMessage moveMsg = new GameMessage(GameMessage.MessageType.MOVE);
        moveMsg.setFromX(fromX);
        moveMsg.setFromY(fromY);
        moveMsg.setToX(toX);
        moveMsg.setToY(toY);
        client.sendMessage(moveMsg);
    }
    
    private static void testUndoRequest(GameClient client) {
        GameMessage undoMsg = new GameMessage(GameMessage.MessageType.UNDO_REQUEST);
        client.sendMessage(undoMsg);
    }
    
    private static void testUndoResponse(GameClient client, boolean accepted, String reason) {
        GameMessage response = new GameMessage(GameMessage.MessageType.UNDO_RESPONSE);
        response.setAccepted(accepted);
        if (reason != null) {
            response.setReason(reason);
        }
        client.sendMessage(response);
    }
}