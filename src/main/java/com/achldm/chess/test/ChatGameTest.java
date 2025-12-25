package com.achldm.chess.test;

import com.achldm.chess.common.GameMessage;

/**
 * 聊天和游戏功能测试类
 */
public class ChatGameTest {
    
    public static void main(String[] args) {
        testMessageTypes();
        testChatMessage();
        testUndoMessage();
        testDrawMessage();
        testSurrenderMessage();
    }
    
    /**
     * 测试消息类型
     */
    private static void testMessageTypes() {
        System.out.println("=== 测试消息类型 ===");
        
        for (GameMessage.MessageType type : GameMessage.MessageType.values()) {
            System.out.println("消息类型: " + type);
        }
        
        System.out.println();
    }
    
    /**
     * 测试聊天消息
     */
    private static void testChatMessage() {
        System.out.println("=== 测试聊天消息 ===");
        
        GameMessage chatMsg = new GameMessage(GameMessage.MessageType.CHAT);
        chatMsg.setUsername("玩家1");
        chatMsg.setContent("大家好！");
        
        System.out.println("聊天消息:");
        System.out.println("  用户: " + chatMsg.getUsername());
        System.out.println("  内容: " + chatMsg.getContent());
        System.out.println("  时间: " + chatMsg.getTimestamp());
        
        System.out.println();
    }
    
    /**
     * 测试悔棋消息
     */
    private static void testUndoMessage() {
        System.out.println("=== 测试悔棋消息 ===");
        
        // 悔棋请求
        GameMessage undoRequest = new GameMessage(GameMessage.MessageType.UNDO_REQUEST);
        undoRequest.setUsername("玩家1");
        
        System.out.println("悔棋请求:");
        System.out.println("  请求者: " + undoRequest.getUsername());
        System.out.println("  类型: " + undoRequest.getType());
        
        // 悔棋回应
        GameMessage undoResponse = new GameMessage(GameMessage.MessageType.UNDO_RESPONSE);
        undoResponse.setAccepted(true);
        
        System.out.println("悔棋回应:");
        System.out.println("  是否接受: " + undoResponse.isAccepted());
        System.out.println("  类型: " + undoResponse.getType());
        
        System.out.println();
    }
    
    /**
     * 测试求和消息
     */
    private static void testDrawMessage() {
        System.out.println("=== 测试求和消息 ===");
        
        // 求和请求
        GameMessage drawRequest = new GameMessage(GameMessage.MessageType.DRAW_REQUEST);
        drawRequest.setUsername("玩家2");
        
        System.out.println("求和请求:");
        System.out.println("  请求者: " + drawRequest.getUsername());
        System.out.println("  类型: " + drawRequest.getType());
        
        // 求和回应（拒绝）
        GameMessage drawResponse = new GameMessage(GameMessage.MessageType.DRAW_RESPONSE);
        drawResponse.setAccepted(false);
        drawResponse.setReason("还想再下几步");
        
        System.out.println("求和回应:");
        System.out.println("  是否接受: " + drawResponse.isAccepted());
        System.out.println("  拒绝原因: " + drawResponse.getReason());
        System.out.println("  类型: " + drawResponse.getType());
        
        System.out.println();
    }
    
    /**
     * 测试认输消息
     */
    private static void testSurrenderMessage() {
        System.out.println("=== 测试认输消息 ===");
        
        GameMessage surrenderMsg = new GameMessage(GameMessage.MessageType.SURRENDER);
        surrenderMsg.setUsername("玩家1");
        surrenderMsg.setRed(false); // 黑方获胜
        
        System.out.println("认输消息:");
        System.out.println("  认输者: " + surrenderMsg.getUsername());
        System.out.println("  红方获胜: " + surrenderMsg.isRed());
        System.out.println("  类型: " + surrenderMsg.getType());
        
        System.out.println();
    }
}