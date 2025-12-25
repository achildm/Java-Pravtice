package com.achldm.chess.test;

import com.achldm.chess.client.network.GameClient;
import com.achldm.chess.common.GameMessage;

import java.io.IOException;

/**
 * 悔棋功能专项测试
 */
public class UndoFunctionTest {
    
    public static void main(String[] args) {
        try {
            System.out.println("=== 悔棋功能测试开始 ===");
            
            // 创建两个客户端模拟对战
            GameClient client1 = new GameClient("localhost", 8888);
            GameClient client2 = new GameClient("localhost", 8888);
            
            // 客户端1登录
            testLogin(client1, "玩家1");
            Thread.sleep(500);
            
            // 客户端2登录
            testLogin(client2, "玩家2");
            Thread.sleep(500);
            
            // 客户端1请求匹配
            testMatchRequest(client1);
            Thread.sleep(500);
            
            // 客户端2请求匹配
            testMatchRequest(client2);
            Thread.sleep(1000);
            
            // 模拟一步棋
            testMove(client1, 1, 9, 2, 7); // 红方马移动
            Thread.sleep(1000);
            
            // 客户端1请求悔棋
            testUndoRequest(client1);
            Thread.sleep(500);
            
            // 客户端2同意悔棋
            testUndoResponse(client2, true, null);
            Thread.sleep(1000);
            
            // 再次模拟移动
            testMove(client1, 0, 9, 0, 8); // 红方车移动
            Thread.sleep(1000);
            
            // 客户端1再次请求悔棋
            testUndoRequest(client1);
            Thread.sleep(500);
            
            // 客户端2拒绝悔棋
            testUndoResponse(client2, false, "不能总是悔棋");
            Thread.sleep(1000);
            
            System.out.println("=== 悔棋功能测试完成 ===");
            
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