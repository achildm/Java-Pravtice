package com.achldm.chess.test;

import com.achldm.chess.client.network.GameClient;
import com.achldm.chess.common.GameMessage;

import java.io.IOException;

/**
 * 客户端功能测试
 */
public class ClientTest {
    
    public static void main(String[] args) {
        try {
            // 连接到服务器
            GameClient client = new GameClient("localhost", 8889);
            
            // 测试登录
            testLogin(client, "测试玩家1");
            
            // 等待一下
            Thread.sleep(1000);
            
            // 测试聊天消息
            testChatMessage(client, "大家好，我是测试玩家！");
            
            // 测试悔棋请求
            testUndoRequest(client);
            
            // 测试求和请求
            testDrawRequest(client);
            
            System.out.println("客户端测试完成！");
            
            // 保持连接一段时间
            Thread.sleep(5000);
            
            client.close();
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 测试登录
     */
    private static void testLogin(GameClient client, String username) {
        System.out.println("=== 测试登录 ===");
        
        GameMessage loginMsg = new GameMessage(GameMessage.MessageType.LOGIN);
        loginMsg.setUsername(username);
        loginMsg.setContent("password123"); // 简单密码
        
        client.sendMessage(loginMsg);
        System.out.println("发送登录请求: " + username);
    }
    
    /**
     * 测试聊天消息
     */
    private static void testChatMessage(GameClient client, String message) {
        System.out.println("=== 测试聊天消息 ===");
        
        GameMessage chatMsg = new GameMessage(GameMessage.MessageType.CHAT);
        chatMsg.setUsername("测试玩家1");
        chatMsg.setContent(message);
        
        client.sendMessage(chatMsg);
        System.out.println("发送聊天消息: " + message);
    }
    
    /**
     * 测试悔棋请求
     */
    private static void testUndoRequest(GameClient client) {
        System.out.println("=== 测试悔棋请求 ===");
        
        GameMessage undoMsg = new GameMessage(GameMessage.MessageType.UNDO_REQUEST);
        undoMsg.setUsername("测试玩家1");
        
        client.sendMessage(undoMsg);
        System.out.println("发送悔棋请求");
    }
    
    /**
     * 测试求和请求
     */
    private static void testDrawRequest(GameClient client) {
        System.out.println("=== 测试求和请求 ===");
        
        GameMessage drawMsg = new GameMessage(GameMessage.MessageType.DRAW_REQUEST);
        drawMsg.setUsername("测试玩家1");
        
        client.sendMessage(drawMsg);
        System.out.println("发送求和请求");
    }
}