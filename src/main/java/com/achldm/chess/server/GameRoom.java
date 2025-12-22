package com.achldm.chess.server;

import com.achldm.chess.common.GameMessage;
import com.achldm.chess.game.ChessBoard;

/**
 * 游戏房间类
 */
public class GameRoom {
    private String roomId;
    private ClientHandler redPlayer;
    private ClientHandler blackPlayer;
    private ChessBoard chessBoard;
    private boolean gameStarted = false;
    
    public GameRoom(String roomId, ClientHandler player1, ClientHandler player2) {
        this.roomId = roomId;
        this.redPlayer = player1;  // 第一个玩家是红方
        this.blackPlayer = player2; // 第二个玩家是黑方
        this.chessBoard = new ChessBoard();
        
        startGame();
    }
    
    /**
     * 开始游戏
     */
    private void startGame() {
        gameStarted = true;
        
        GameMessage startMsg = new GameMessage(GameMessage.MessageType.GAME_START);
        redPlayer.sendMessage(startMsg);
        blackPlayer.sendMessage(startMsg);
        
        System.out.println("游戏开始 - 房间: " + roomId);
    }
    
    /**
     * 处理棋子移动
     */
    public void handleMove(GameMessage moveMessage, ClientHandler sender) {
        if (!gameStarted) return;
        
        // 验证是否轮到该玩家
        boolean isRedTurn = chessBoard.isRedTurn();
        boolean senderIsRed = (sender == redPlayer);
        
        if (isRedTurn != senderIsRed) {
            // 不是该玩家的回合
            return;
        }
        
        // 验证移动是否合法
        int fromX = moveMessage.getFromX();
        int fromY = moveMessage.getFromY();
        int toX = moveMessage.getToX();
        int toY = moveMessage.getToY();
        
        if (chessBoard.isValidMove(fromX, fromY, toX, toY)) {
            // 执行移动
            chessBoard.movePiece(fromX, fromY, toX, toY);
            
            // 转发移动消息给对手
            ClientHandler opponent = senderIsRed ? blackPlayer : redPlayer;
            opponent.sendMessage(moveMessage);
            
            // 检查游戏是否结束
            if (chessBoard.isGameOver()) {
                handleGameOver(senderIsRed);
            }
        }
    }
    
    /**
     * 处理游戏结束
     */
    private void handleGameOver(boolean redWin) {
        gameStarted = false;
        
        GameMessage gameOverMsg = new GameMessage(GameMessage.MessageType.GAME_OVER);
        gameOverMsg.setRed(redWin);
        
        redPlayer.sendMessage(gameOverMsg);
        blackPlayer.sendMessage(gameOverMsg);
        
        System.out.println("游戏结束 - 房间: " + roomId + ", 获胜方: " + (redWin ? "红方" : "黑方"));
    }
    
    /**
     * 处理玩家断开连接
     */
    public void handlePlayerDisconnect(ClientHandler disconnectedPlayer) {
        if (!gameStarted) return;
        
        gameStarted = false;
        
        // 通知另一个玩家对手已断开连接
        ClientHandler otherPlayer = (disconnectedPlayer == redPlayer) ? blackPlayer : redPlayer;
        if (otherPlayer != null) {
            GameMessage disconnectMsg = new GameMessage(GameMessage.MessageType.DISCONNECT);
            disconnectMsg.setContent("对手已断开连接");
            otherPlayer.sendMessage(disconnectMsg);
        }
        
        System.out.println("玩家断开连接 - 房间: " + roomId);
    }
    
    /**
     * 检查房间是否包含指定玩家
     */
    public boolean containsPlayer(ClientHandler player) {
        return player == redPlayer || player == blackPlayer;
    }
    
    // Getters
    public String getRoomId() {
        return roomId;
    }
    
    public ClientHandler getRedPlayer() {
        return redPlayer;
    }
    
    public ClientHandler getBlackPlayer() {
        return blackPlayer;
    }
    
    public boolean isGameStarted() {
        return gameStarted;
    }
}