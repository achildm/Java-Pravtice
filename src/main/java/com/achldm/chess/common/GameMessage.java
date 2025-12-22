package com.achldm.chess.common;

import java.io.Serializable;

/**
 * 网络通信消息类
 */
public class GameMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum MessageType {
        LOGIN,           // 登录
        LOGIN_SUCCESS,   // 登录成功
        LOGIN_FAILED,    // 登录失败
        MATCH_REQUEST,   // 请求匹配
        MATCH_FOUND,     // 找到对手
        GAME_START,      // 游戏开始
        MOVE,           // 移动棋子
        GAME_OVER,      // 游戏结束
        CHAT,           // 聊天消息
        HEARTBEAT,      // 心跳包
        DISCONNECT      // 断开连接
    }
    
    private MessageType type;
    private String username;
    private String content;
    private int fromX, fromY, toX, toY;  // 棋子移动坐标
    private boolean isRed;               // 是否红方
    private long timestamp;
    private int avatarIndex;             // 头像索引
    private String userInfo;             // 用户信息
    
    public GameMessage(MessageType type) {
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public MessageType getType() { return type; }
    public void setType(MessageType type) { this.type = type; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public int getFromX() { return fromX; }
    public void setFromX(int fromX) { this.fromX = fromX; }
    
    public int getFromY() { return fromY; }
    public void setFromY(int fromY) { this.fromY = fromY; }
    
    public int getToX() { return toX; }
    public void setToX(int toX) { this.toX = toX; }
    
    public int getToY() { return toY; }
    public void setToY(int toY) { this.toY = toY; }
    
    public boolean isRed() { return isRed; }
    public void setRed(boolean red) { isRed = red; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
    public int getAvatarIndex() { return avatarIndex; }
    public void setAvatarIndex(int avatarIndex) { this.avatarIndex = avatarIndex; }
    
    public String getUserInfo() { return userInfo; }
    public void setUserInfo(String userInfo) { this.userInfo = userInfo; }
}